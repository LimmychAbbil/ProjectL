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
import net.lim.model.FileManager;
import net.lim.model.RestConnection;
import net.lim.model.service.LUtils;
import net.lim.view.ProgressView;
import net.lim.view.RegistrationPane;

import javax.net.ssl.HttpsURLConnection;
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
        try {
            //TODO make connection attempt async
            establishConnection();
        } catch (RuntimeException e) {
            //TODO show temporary error popup
            System.err.println("Can't establish connection: " + e.getMessage());
        }
    }

    private void establishConnection() {
        //TODO read configuration file (server ip)
        connection = new RestConnection("http://localhost:8080/rest");
        boolean connectionOK = connection.validateConnection();
        if (connectionOK) {
            boolean currentVersionSupported = connection.validateVersionSupported(LLauncher.PROGRAM_VERSION);
            if (!currentVersionSupported) {
                throw new RuntimeException("Too old launcher version. Please upgrade");
            }
        } else {
            throw new RuntimeException("Can't establish connection");
        }
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
            boolean loginSuccess = loginTask.getValue();

            if (loginSuccess) {
                startFileChecking(userName);
            } else {
                startTask(createProgressCompleteTask(5000));
            }

        });
        startTask(loginTask);
    }

    private void startFileChecking(String userName) {
        initFileController(connection);
        Task<Boolean> fileCheckTask = createFileCheckTask();
        progressView.getTextMessageProperty().bind(fileCheckTask.messageProperty());
        fileCheckTask.setOnSucceeded(e -> {
            boolean filesOK = fileCheckTask.getValue();
            if (filesOK) {
                progressView.getTextMessageProperty().unbind();
                progressView.getTextMessageProperty().setValue("Launching");
                startTask(createProgressCompleteTask(1000));
                try {
                    launchGame(getServerURL(), userName);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            } else {
                Task<Void> downloadFilesTask = createDownloadTask();
                progressView.getTextMessageProperty().bind(downloadFilesTask.messageProperty());
                startTask(downloadFilesTask);
                downloadFilesTask.setOnSucceeded(event -> {
                    startFileChecking(userName);
                });
            }
        });
        startTask(fileCheckTask);
    }

    private String getServerURL() {
        //TODO its a stub
        return "myServerURL";
    }

    private void launchGame(String serverURL, String login) throws Exception {
        final String useCMDCommand = "cmd.exe /c ";
        String goToDiskCCommand = "C:";
        String goToDefaultDirCommand = "cd " + FileManager.DEFAULT_DIRECTORY ;
        String fulllaunchCommand = new StringBuilder(useCMDCommand).append(goToDiskCCommand).append(" && ").append(goToDefaultDirCommand).append(" && ")
                .append("javaw ")
                .append("-Xmx1G ")
                .append("-XX:+UseConcMarkSweepGC ")
                .append("-XX:-UseAdaptiveSizePolicy ")
                .append("-Xmn128M ")
                .append("-Djava.library.path=.minecraft\\versions\\1.8.7\\1.8.7-natives-270480024547 ")
                .append("-cp .minecraft\\libraries\\oshi-project\\oshi-core\\1.1\\oshi-core-1.1.jar;.minecraft\\libraries\\net\\java\\dev\\jna\\jna\\3.4.0\\jna-3.4.0.jar;.minecraft\\libraries\\net\\java\\dev\\jna\\platform\\3.4.0\\platform-3.4.0.jar;.minecraft\\libraries\\com\\ibm\\icu\\icu4j-core-mojang\\51.2\\icu4j-core-mojang-51.2.jar;.minecraft\\libraries\\net\\sf\\jopt-simple\\jopt-simple\\4.6\\jopt-simple-4.6.jar;.minecraft\\libraries\\com\\paulscode\\codecjorbis\\20101023\\codecjorbis-20101023.jar;.minecraft\\libraries\\com\\paulscode\\codecwav\\20101023\\codecwav-20101023.jar;.minecraft\\libraries\\com\\paulscode\\libraryjavasound\\20101123\\libraryjavasound-20101123.jar;.minecraft\\libraries\\com\\paulscode\\librarylwjglopenal\\20100824\\librarylwjglopenal-20100824.jar;.minecraft\\libraries\\com\\paulscode\\soundsystem\\20120107\\soundsystem-20120107.jar;.minecraft\\libraries\\io\\netty\\netty-all\\4.0.23.Final\\netty-all-4.0.23.Final.jar;.minecraft\\libraries\\com\\google\\guava\\guava\\17.0\\guava-17.0.jar;.minecraft\\libraries\\org\\apache\\commons\\commons-lang3\\3.3.2\\commons-lang3-3.3.2.jar;.minecraft\\libraries\\commons-io\\commons-io\\2.4\\commons-io-2.4.jar;.minecraft\\libraries\\commons-codec\\commons-codec\\1.9\\commons-codec-1.9.jar;.minecraft\\libraries\\net\\java\\jinput\\jinput\\2.0.5\\jinput-2.0.5.jar;.minecraft\\libraries\\net\\java\\jutils\\jutils\\1.0.0\\jutils-1.0.0.jar;.minecraft\\libraries\\com\\google\\code\\gson\\gson\\2.2.4\\gson-2.2.4.jar;.minecraft\\libraries\\com\\mojang\\authlib\\1.5.21\\authlib-1.5.21.jar;.minecraft\\libraries\\com\\mojang\\realms\\1.7.23\\realms-1.7.23.jar;.minecraft\\libraries\\org\\apache\\commons\\commons-compress\\1.8.1\\commons-compress-1.8.1.jar;.minecraft\\libraries\\org\\apache\\httpcomponents\\httpclient\\4.3.3\\httpclient-4.3.3.jar;.minecraft\\libraries\\commons-logging\\commons-logging\\1.1.3\\commons-logging-1.1.3.jar;.minecraft\\libraries\\org\\apache\\httpcomponents\\httpcore\\4.3.2\\httpcore-4.3.2.jar;.minecraft\\libraries\\org\\apache\\logging\\log4j\\log4j-api\\2.0-beta9\\log4j-api-2.0-beta9.jar;.minecraft\\libraries\\org\\apache\\logging\\log4j\\log4j-core\\2.0-beta9\\log4j-core-2.0-beta9.jar;.minecraft\\libraries\\org\\lwjgl\\lwjgl\\lwjgl\\2.9.4-nightly-20150209\\lwjgl-2.9.4-nightly-20150209.jar;.minecraft\\libraries\\org\\lwjgl\\lwjgl\\lwjgl_util\\2.9.4-nightly-20150209\\lwjgl_util-2.9.4-nightly-20150209.jar;.minecraft\\libraries\\tv\\twitch\\twitch\\6.5\\twitch-6.5.jar;.minecraft\\versions\\1.8.7\\1.8.7.jar ")
                .append("net.minecraft.client.main.Main ")
                .append("--username ").append(login).append(" ")
                .append("--version 1.8.7 ")
                .append("--gameDir .minecraft ")
                .append("--assetsDir .minecraft\\assets ")
                .append("--assetIndex 1.8 ")
                .append("--uuid e90ca68a26004b80a737ace4cd74797d ")
                .append("--accessToken 7658368cabe94fa2b7439e1b24b59910 ")
                .append("--userProperties {} ")
                .append("--userType mojang ")
                .append("--server ").append(serverURL).toString();
        Process launch = Runtime.getRuntime().exec(fulllaunchCommand);


        if (launch.isAlive() || launch.exitValue() == 0) {
            Platform.exit();
        }
    }

    private Task<Void> createDownloadTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                updateMessage("Downloading files...");
                try {
                    fileController.deleteFiles();
                    fileController.initFTPConnection();
                    for (String fileName: fileController.getFileNames()) {
                        fileController.downloadFile(fileName);
//                        updateProgress(fileController.getFileManager().getProgressCounter(), fileController.getFileManager().getTotalFilesSize());
                        updateMessage("Downloading files: " + fileController.getFileManager().getProgressCounter() +
                                " / " + fileController.getFileManager().getTotalFilesSize());
                    }
                    System.out.println("Download finished");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    //TODO process finally if something went wrong
                    fileController.getFileManager().closeFTPConnection();
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
        taskThread.setDaemon(true);
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
        if (!LUtils.isNotValidUserName(registrationPane.getUserName().getText())) {
            registrationPane.getErrorMessage().setText("Неправильное имя пользователя");
            return;
        }
        if (registrationPane.getPassword().getText().isEmpty()) {
            registrationPane.getErrorMessage().setText("Пустой пароль");
            return;
        }
        if (!registrationPane.getPassword().getText().equals(registrationPane.getPasswordConfirmation().getText())) {
            registrationPane.getErrorMessage().setText("Пароли не совпадают");
            return;
        }

        if (!registrationPane.getRulesConfirmation().isSelected()) {
            registrationPane.getErrorMessage().setText("Подтвердите согласие с правилами");
            return;
        }
        try {
            int responseCode = connection.sendRegistration(registrationPane.getUserName().getText(), registrationPane.getPassword().getText());
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                registrationPane.getErrorMessage().setText("");
                registrationPane.setVisible(false);
            } else {
                registrationPane.getErrorMessage().setText(Connection.getErrorMessage(responseCode));
            }
        } catch (Exception e) {
            registrationPane.getErrorMessage().setText("Не удалось зарегистрироваться: " + e.getMessage());
        }

    }

    public void cancelRegistration(RegistrationPane registrationPane) {
        registrationPane.setVisible(false);
    }

    public void setProgressView(ProgressView progressView) {
        this.progressView = progressView;
    }
}
