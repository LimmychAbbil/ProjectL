package net.lim.controller;

import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import net.lim.LLauncher;
import net.lim.model.Connection;
import net.lim.model.RestConnection;
import net.lim.view.ProgressView;
import net.lim.view.RegistrationPane;

import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * Created by Limmy on 28.04.2018.
 */
public class LauncherController {
    Connection connection;
    private Stage primaryStage;
    private HostServices hostServices;
    private static double currentX;
    private static double currentY;
    private double dragOffsetX;
    private double dragOffsetY;
    private boolean isMaximized = false;
    private FileController fileController;
    private ProgressView progressView;

    public LauncherController(Stage primaryStage, HostServices hostServices) {
        this.hostServices = hostServices;
        this.primaryStage = primaryStage;
        initializeDefaultXAndY(primaryStage);
        establishConnection();
    }

    private void establishConnection() {
        //TODO read configuration file (server ip)
        connection = new RestConnection("http://localhost:8080/rest");
    }

    public void hideNewsButtonPressed(ScrollPane pane) {
        pane.setVisible(!pane.isVisible());
    }

    public void linkPressed(URL url) {
        hostServices.showDocument(url.toString());
    }

    public void handleMouseDragged(MouseEvent e) {
        currentX = e.getScreenX() - this.dragOffsetX;
        currentY = e.getScreenY() - this.dragOffsetY;
        primaryStage.setX(currentX);
        primaryStage.setY(currentY);
    }

    public void handleMousePress(MouseEvent e) {
        this.dragOffsetX = e.getScreenX() - primaryStage.getX();
        this.dragOffsetY = e.getScreenY() - primaryStage.getY();
    }

    private void initializeDefaultXAndY(Stage primaryStage) {
        currentX = primaryStage.getX();
        currentY = primaryStage.getY();
    }

    private void deMaximizeStage(Stage stage) {
        stage.setX(currentX);
        stage.setY(currentY);
        stage.setWidth(getDefaultWidth());
        stage.setHeight(getDefaultHeight());
    }

    public void maximizePressed() {
        if (!isMaximized) {
            maximizeStage(primaryStage);
            isMaximized = true;
        } else {
            deMaximizeStage(primaryStage);
            isMaximized = false;
        }
    }

    public double getDefaultWidth() {
        double maxWidth = Screen.getPrimary().getBounds().getWidth();
        double defaultWidth = 0.6 * maxWidth;
        return maxWidth >= LLauncher.MIN_WIDTH ? defaultWidth : LLauncher.MIN_WIDTH;
    }

    public double getDefaultHeight() {
        double maxHeight = Screen.getPrimary().getBounds().getHeight();
        double defaultHeight = 0.6 * maxHeight;
        return defaultHeight >= LLauncher.MIN_HEIGHT ? defaultHeight : LLauncher.MIN_HEIGHT;
    }

    private void maximizeStage(Stage stage) {
        stage.setX(0.0);
        stage.setY(0.0);
        stage.setWidth(Screen.getPrimary().getBounds().getWidth());
        stage.setHeight(Screen.getPrimary().getBounds().getHeight());
    }

    public void closeButtonPressed() {
        Platform.exit();
    }

    public void minimizedPressed() {
        primaryStage.setIconified(true);
    }

    public void loginButtonPressed(String userName, String password) {
        progressView.setVisible(true);
        Task<Boolean> loginTask = createLoginTask(userName, password);
        progressView.getTextMessageProperty().bind(loginTask.messageProperty());
        loginTask.setOnSucceeded(e -> {
            boolean loginSuccess = false;

            loginSuccess = loginTask.getValue();
            if (loginSuccess) {
                startFileChecking();
            } else {
                startTask(createProgressCompleteTask(5000));
            }

        });
        startTask(loginTask);
        /*
        if (loginSuccess) {
            System.out.println("Success");
            //success, start file checking
            initFileController(connection);
            while (true) {
                progressView.startFilesCheck();
                boolean fileCheckResult = fileController.checkFiles();
                progressView.success();
                if (fileCheckResult) {
                    System.out.println("Файлы совпадают, стартуем игру");
                    break;
                } else {
                    progressView.filesCheckFailed();
                    System.out.println("Файлы не совпадают. Удаляем все (кроме игнорируемых) и перепроверяем");
                    fileController.deleteFiles();
                    try {
                        fileController.downloadFiles();
                    } catch (Exception e) {
                        //TODO create error handler
                        throw new RuntimeException(e);
                    }
                }
            }

        } else {
            System.out.println("Fail");
            try {
                Thread.sleep(5000);
                progressView.setVisible(false);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }*/
    }

    private void startFileChecking() {
        initFileController(connection);
        Task<Boolean> fileCheckTask = createFileCheckTask();
        progressView.getTextMessageProperty().bind(fileCheckTask.messageProperty());
        fileCheckTask.setOnSucceeded(e -> {
            boolean filesOK = fileCheckTask.getValue();
            if (filesOK) {
                progressView.getTextMessageProperty().unbind();
                progressView.getTextMessageProperty().setValue("Launching");
                startTask(createProgressCompleteTask(1000));
            } else {
                Task<Void> downloadFilesTask = createDownloadTask();
                progressView.getTextMessageProperty().bind(downloadFilesTask.messageProperty());
                startTask(downloadFilesTask);
                downloadFilesTask.setOnSucceeded(event -> {
                    startFileChecking();
                });
            }
        });
        startTask(fileCheckTask);
    }

    private Task<Void> createDownloadTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                updateMessage("Downloading files...");
                try {
                    fileController.deleteFiles();
                    //TODO add progress
                    fileController.downloadFiles();
                    System.out.println("Download finished");
                } finally {
                    //TODO process finally if something went wrong
                }
                return null;
            }
        };
    }

    private Task<Boolean> createFileCheckTask() {

        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                updateMessage("Check files");
                return fileController.checkFiles();
            }
        };
    }

    private void startTask(Task<?> task) {
        Thread taskThread = new Thread(task);
        taskThread.setDaemon(false);
        taskThread.start();
    }

    private Task<Boolean> createLoginTask(String userName, String password) {
        return new Task<Boolean>() {
            @Override
            protected Boolean call() {
                updateMessage("Login...");
                boolean loginSuccess = false;
                try {
                    loginSuccess = connection.login(userName, password);
                    if (!loginSuccess) {
                        updateMessage("Wrong user or password");
                    }
                } catch (Exception e) {
                    updateMessage("Can't connect: " + e.getMessage());
                }
                return loginSuccess;
            }
        };
    }

    private Task<Void> createProgressCompleteTask(long milis) {

        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(milis);
                progressView.setVisible(false);
                return null;
            }
        };
    }

    private void initFileController(Connection connection) {
        this.fileController = new FileController(connection, progressView);
    }

    public void registrationButtonPressed(RegistrationPane registrationPane) {
        registrationPane.setVisible(true);
    }

    public void rulesClicked() {
        System.out.println("Открыть ссылку на правила");
    }

    public void sendRegistration(RegistrationPane registrationPane) {
        //TODO valid data (check login regex and password is length enough)
        if (!registrationPane.getPassword().getText().equals(registrationPane.getPasswordConfirmation().getText())) {
            System.out.println("Пароли не совпадают");
            return;
        }
        connection.sendRegistration(registrationPane.getUserName().getText(), registrationPane.getPassword().getText());
        registrationPane.setVisible(false);
    }

    public void cancelRegistration(RegistrationPane registrationPane) {
        registrationPane.setVisible(false);
    }

    public void setProgressView(ProgressView progressView) {
        this.progressView = progressView;
    }
}
