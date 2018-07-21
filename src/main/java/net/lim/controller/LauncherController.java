package net.lim.controller;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
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
        progressView.start();
        boolean loginResult = login(userName, password);
        if (loginResult) {
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

        }
    }

    private boolean login(String userName, String password) {
        try {
            boolean loginSuccess = connection.login(userName, password);
            if (!loginSuccess) {
                progressView.loginFailed("Wrong user or password");
            }
            return loginSuccess;
        } catch (Exception e) {
            progressView.loginFailed(e.getMessage());
            return false;
        }
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
