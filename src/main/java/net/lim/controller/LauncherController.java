package net.lim.controller;

import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import net.lim.LLauncher;
import net.lim.controller.tasks.BackgroundReceiverTask;
import net.lim.controller.tasks.DownloadFilesService;
import net.lim.controller.tasks.FileCheckerService;
import net.lim.controller.tasks.LoginService;
import net.lim.model.FileManager;
import net.lim.model.ServerInfo;
import net.lim.model.Settings;
import net.lim.model.adv.Advertisement;
import net.lim.model.adv.AdvertisementReceiver;
import net.lim.model.adv.RestAdvertisementReceiver;
import net.lim.model.connection.Connection;
import net.lim.model.connection.RestConnection;
import net.lim.model.connection.StubConnection;
import net.lim.model.service.LUtils;
import net.lim.view.BasicPane;
import net.lim.view.NewsPane;
import net.lim.view.ProgressView;
import net.lim.view.RegistrationPane;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Created by Limmy on 28.04.2018.
 */
public class LauncherController {
    public static final String DEFAULT_COMMAND = "notepad"; //fixme
    private Connection connection;
    private Stage primaryStage;
    private HostServices hostServices;
    private static double currentX;
    private static double currentY;
    private double dragOffsetX;
    private double dragOffsetY;
    private boolean isMaximized = false;
    private FileController fileController;
    private ProgressView progressView;
    private ServerInfo selectedServer;
    private BasicPane basicView;
    private DownloadFilesService downloadService;
    private LoginService loginService;
    private FileCheckerService fileCheckerService;

    public static String token;

    public LauncherController(Stage primaryStage, HostServices hostServices) {
        this.hostServices = hostServices;
        this.primaryStage = primaryStage;
        initializeDefaultXAndY(primaryStage);
    }

    /**
     * @throws IllegalStateException if called before view is ready
     */
    public void init() {
        if (primaryStage == null || basicView == null) {
            throw new IllegalStateException("Not ready");
        }
        establishConnection();
        this.loginService = new LoginService(connection);
        this.fileCheckerService = new FileCheckerService(fileController);
        this.downloadService = new DownloadFilesService(fileController);
    }

    private void establishConnection() {
        String launchServerURL = null;
        if (Settings.getInstance().getLserverURL() == null) {
            //can't be null here
            launchServerURL = readServerURLFromConfigFile();
        } else {
            launchServerURL = Settings.getInstance().getLserverURL();
        }
        boolean connectionOK = false;
        String errorMessage = null;
        try {
            if (!Settings.getInstance().isOfflineMode()) {
                connection = new RestConnection(launchServerURL);
                connectionOK = connection.validateConnection();
                if (connectionOK) {
                    boolean currentVersionSupported = connection.validateVersionSupported(LLauncher.PROGRAM_VERSION);
                    if (!currentVersionSupported) {
                        errorMessage = "Too old launcher version. Please upgrade";
                        connectionOK = false;
                    }

                    initFileController(connection);
                } else {
                    errorMessage = "Can't establish connection";
                }
            } else {
                //do nothing for offline mode
                connection = new StubConnection();
                connectionOK = true;
            }
        } catch (Exception e) {
            errorMessage = e.getMessage();
            System.err.println("Connection attempt failed: " + e.getMessage());
        }
        if (basicView != null) {
            basicView.setConnectionStatus(connectionOK, errorMessage);
        }
    }

    private String readServerURLFromConfigFile() {
        try (InputStream reader = getClass().getClassLoader().getResourceAsStream("configuration/client.config")) {
            Properties properties = new Properties();
            properties.load(reader);
            String serverIp = properties.getProperty("server.ip");
            if (serverIp == null) {
                throw new RuntimeException("Config file doesn't contain server.ip property");
            }
            return serverIp;
        } catch (Exception e) {
            throw new RuntimeException(e);
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
        createLoginTask(userName, password);
    }

    private void startFileChecking(String userName) {
        fileCheckerService.start();
        progressView.getTextMessageProperty().bind(fileCheckerService.messageProperty());
        fileCheckerService.setOnSucceeded(e -> {
            boolean filesOK = fileCheckerService.getValue();
            if (filesOK) {
                progressView.getTextMessageProperty().unbind();
                progressView.getTextMessageProperty().setValue("Launching");
                startTask(createWaitingTask(1000));
                try {
                    launchGame(userName);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            } else {
                createDownloadTask(userName);
            }
        });
    }

    private String getServerURL() {
        if (selectedServer != null) {
            return selectedServer.getIp() + ":" + selectedServer.getPort();
        }
        return ""; //offline connection
    }

    private void launchGame(String login) throws Exception {
        boolean isCustomDirUsed = StringUtils.isNotEmpty(Settings.getInstance().getFilesDir());
        String useCMDCommand = "cmd.exe /c ";
        String goToDiskCommand = isCustomDirUsed ? Settings.getInstance().getFilesDir().substring(0, Settings.getInstance().getFilesDir().indexOf(":") + 1)
                : "C:";
        String commandSeparator = " && ";
        String goToDirCommand;
        if (isCustomDirUsed) {
            goToDirCommand = "cd " + Settings.getInstance().getFilesDir();
        } else {
            goToDirCommand = "cd " + FileManager.DEFAULT_DIRECTORY;
        }
        StringBuilder fullLaunchCommandBuilder = new StringBuilder();
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            fullLaunchCommandBuilder.append(useCMDCommand).append(goToDiskCommand).append(commandSeparator).append(goToDirCommand).append(commandSeparator);
        } else {
            fullLaunchCommandBuilder.append("sh ").append(goToDirCommand).append(" ; ");
        }


        fullLaunchCommandBuilder.append(connection.getServerLaunchCommand(selectedServer));
        /*fullLaunchCommandBuilder.append("java ");
            if (Settings.getInstance().getXmx() != 0) {
                fullLaunchCommandBuilder.append("-Xms").append(Settings.getInstance().getXmx()).append("m ");
            } else {
                fullLaunchCommandBuilder.append("-Xms").append(Settings.DEFAULT_XMS_MB_SIZE).append("m ");
            }

        fullLaunchCommandBuilder.append("-XX:+UseConcMarkSweepGC ")
            .append("-XX:-UseAdaptiveSizePolicy ")
            .append("-Xmn128M ")
            .append("-Djava.library.path=\".minecraft\\versions\\ForgeOptiFine 1.8.9\\natives\" ")
            .append("-cp \".minecraft\\libraries\\net\\minecraftforge\\forge\\1.8.9-11.15.1.2318-1.8.9\\forge-1.8.9-11.15.1.2318-1.8.9.jar;.minecraft\\libraries\\net\\minecraft\\launchwrapper\\1.12\\launchwrapper-1.12.jar;.minecraft\\libraries\\org\\ow2\\asm\\asm-all\\5.0.3\\asm-all-5.0.3.jar;.minecraft\\libraries\\com\\typesafe\\akka\\akka-actor_2.11\\2.3.3\\akka-actor_2.11-2.3.3.jar;.minecraft\\libraries\\com\\typesafe\\config\\1.2.1\\config-1.2.1.jar;.minecraft\\libraries\\org\\scala-lang\\scala-actors-migration_2.11\\1.1.0\\scala-actors-migration_2.11-1.1.0.jar;.minecraft\\libraries\\org\\scala-lang\\scala-compiler\\2.11.1\\scala-compiler-2.11.1.jar;.minecraft\\libraries\\org\\scala-lang\\plugins\\scala-continuations-library_2.11\\1.0.2\\scala-continuations-library_2.11-1.0.2.jar;.minecraft\\libraries\\org\\scala-lang\\plugins\\scala-continuations-plugin_2.11.1\\1.0.2\\scala-continuations-plugin_2.11.1-1.0.2.jar;.minecraft\\libraries\\org\\scala-lang\\scala-library\\2.11.1\\scala-library-2.11.1.jar;.minecraft\\libraries\\org\\scala-lang\\scala-parser-combinators_2.11\\1.0.1\\scala-parser-combinators_2.11-1.0.1.jar;.minecraft\\libraries\\org\\scala-lang\\scala-reflect\\2.11.1\\scala-reflect-2.11.1.jar;.minecraft\\libraries\\org\\scala-lang\\scala-swing_2.11\\1.0.1\\scala-swing_2.11-1.0.1.jar;.minecraft\\libraries\\org\\scala-lang\\scala-xml_2.11\\1.0.2\\scala-xml_2.11-1.0.2.jar;.minecraft\\libraries\\lzma\\lzma\\0.0.1\\lzma-0.0.1.jar;.minecraft\\libraries\\net\\sf\\jopt-simple\\jopt-simple\\4.6\\jopt-simple-4.6.jar;.minecraft\\libraries\\java3d\\vecmath\\1.5.2\\vecmath-1.5.2.jar;.minecraft\\libraries\\net\\sf\\trove4j\\trove4j\\3.0.3\\trove4j-3.0.3.jar;.minecraft\\libraries\\optifine\\OptiFine\\1.8.9_HD_U_I7\\OptiFine-1.8.9_HD_U_I7.jar;.minecraft\\libraries\\org\\tlauncher\\netty\\1.6\\netty-1.6.jar;.minecraft\\libraries\\oshi-project\\oshi-core\\1.1\\oshi-core-1.1.jar;.minecraft\\libraries\\net\\java\\dev\\jna\\jna\\3.4.0\\jna-3.4.0.jar;.minecraft\\libraries\\net\\java\\dev\\jna\\platform\\3.4.0\\platform-3.4.0.jar;.minecraft\\libraries\\com\\ibm\\icu\\icu4j-core-mojang\\51.2\\icu4j-core-mojang-51.2.jar;.minecraft\\libraries\\net\\sf\\jopt-simple\\jopt-simple\\4.6\\jopt-simple-4.6.jar;.minecraft\\libraries\\com\\paulscode\\codecjorbis\\20101023\\codecjorbis-20101023.jar;.minecraft\\libraries\\com\\paulscode\\codecwav\\20101023\\codecwav-20101023.jar;.minecraft\\libraries\\com\\paulscode\\libraryjavasound\\20101123\\libraryjavasound-20101123.jar;.minecraft\\libraries\\com\\paulscode\\librarylwjglopenal\\20100824\\librarylwjglopenal-20100824.jar;.minecraft\\libraries\\com\\paulscode\\soundsystem\\20120107\\soundsystem-20120107.jar;.minecraft\\libraries\\io\\netty\\netty-all\\4.0.23.Final\\netty-all-4.0.23.Final.jar;.minecraft\\libraries\\com\\google\\guava\\guava\\17.0\\guava-17.0.jar;.minecraft\\libraries\\org\\apache\\commons\\commons-lang3\\3.3.2\\commons-lang3-3.3.2.jar;.minecraft\\libraries\\commons-io\\commons-io\\2.4\\commons-io-2.4.jar;.minecraft\\libraries\\commons-codec\\commons-codec\\1.9\\commons-codec-1.9.jar;.minecraft\\libraries\\net\\java\\jinput\\jinput\\2.0.5\\jinput-2.0.5.jar;.minecraft\\libraries\\net\\java\\jutils\\jutils\\1.0.0\\jutils-1.0.0.jar;.minecraft\\libraries\\com\\google\\code\\gson\\gson\\2.2.4\\gson-2.2.4.jar;.minecraft\\libraries\\org\\tlauncher\\authlib\\1.5.21\\authlib-1.5.21.jar;.minecraft\\libraries\\com\\mojang\\realms\\1.7.59\\realms-1.7.59.jar;.minecraft\\libraries\\org\\apache\\commons\\commons-compress\\1.8.1\\commons-compress-1.8.1.jar;.minecraft\\libraries\\org\\apache\\httpcomponents\\httpclient\\4.3.3\\httpclient-4.3.3.jar;.minecraft\\libraries\\commons-logging\\commons-logging\\1.1.3\\commons-logging-1.1.3.jar;.minecraft\\libraries\\org\\apache\\httpcomponents\\httpcore\\4.3.2\\httpcore-4.3.2.jar;.minecraft\\libraries\\org\\apache\\logging\\log4j\\log4j-api\\2.0-beta9\\log4j-api-2.0-beta9.jar;.minecraft\\libraries\\org\\apache\\logging\\log4j\\log4j-core\\2.0-beta9\\log4j-core-2.0-beta9.jar;.minecraft\\libraries\\org\\lwjgl\\lwjgl\\lwjgl\\2.9.4-nightly-20150209\\lwjgl-2.9.4-nightly-20150209.jar;.minecraft\\libraries\\org\\lwjgl\\lwjgl\\lwjgl_util\\2.9.4-nightly-20150209\\lwjgl_util-2.9.4-nightly-20150209.jar;.minecraft\\libraries\\tv\\twitch\\twitch\\6.5\\twitch-6.5.jar;.minecraft\\versions\\ForgeOptiFine 1.8.9\\ForgeOptiFine 1.8.9.jar\" ")
            .append("-Dminecraft.applet.TargetDirectory=.minecraft -Dfml.ignoreInvalidMinecraftCertificates=true -Dfml.ignorePatchDiscrepancies=true ")
            .append("net.minecraft.launchwrapper.Launch ")
            .append("--username ").append(login).append(" ")
            .append("--version \"ForgeOptiFine 1.8.9\" ")
            .append("--gameDir .minecraft ")
            .append("--assetsDir .minecraft\\assets ")
            .append("--assetIndex 1.8 ")
            .append("--uuid 00000000-0000-0000-0000-000000000000 ")
            .append("--accessToken null ")
            .append("--userProperties {\"token\":[\"").append(token).append("\"]} ")
            .append("--userType mojang ")
            .append("--tweakClass net.minecraftforge.fml.common.launcher.FMLTweaker ");

        if (StringUtils.isNotEmpty(getServerURL())) {
            fullLaunchCommandBuilder.append("--server ").append(selectedServer.getIp()).append(" ");
            fullLaunchCommandBuilder.append(" --port ").append(selectedServer.getPort());
        } */

        Process launch = Runtime.getRuntime().exec(fullLaunchCommandBuilder.toString());

        if (launch.isAlive() || launch.exitValue() == 0) {
            startSTDThreads(launch);
            BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(launch.getInputStream()));
            while (inputStreamReader.ready()) {
                System.err.println(inputStreamReader.readLine());
            }
            Platform.exit();
        } else {
            BufferedReader errStreamReader = new BufferedReader(new InputStreamReader(launch.getErrorStream()));
            while (errStreamReader.ready()) {
                System.err.println(errStreamReader.readLine());
            }
        }
    }

    private void startSTDThreads(Process launch) {
        Thread tSTDOut = new Thread(() -> {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(launch.getInputStream()))){
                while (bufferedReader.ready()) {
                    System.out.println(bufferedReader.readLine());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Thread tSTDErr = new Thread(() -> {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(launch.getErrorStream()))){
                while (bufferedReader.ready()) {
                    System.err.println(bufferedReader.readLine());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        tSTDOut.setDaemon(true);
        tSTDErr.setDaemon(true);

        tSTDOut.start();
        tSTDErr.start();
    }

    private void createDownloadTask(String userName) {
        downloadService.reset();
        downloadService.start();

        progressView.getTextMessageProperty().bind(downloadService.messageProperty());
        downloadService.setOnSucceeded(event -> {
            startFileChecking(userName);
        });
        downloadService.setOnFailed(e -> {
            startTask(createWaitingTask(5 * 1000));
        });
    }

    private void startTask(Task<?> task) {
        Thread taskThread = new Thread(task);
        taskThread.setDaemon(true);
        taskThread.start();
    }

    private void createLoginTask(String userName, String password) {
        loginService.start(userName, password);

        loginService.setOnSucceeded(e -> {
                    boolean loginSuccess = loginService.getValue();

                    if (loginSuccess) {
                        startFileChecking(userName);
                    } else {
                        startTask(createWaitingTask(5000));
                    }
                }
        );
    }

    private Task<Void> createWaitingTask(long milis) {

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
        this.fileController = new FileController(connection);
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

    public List<ServerInfo> retrieveServerList() {
        if (connection == null) return Collections.emptyList();
        JSONObject serversInfoJSON = connection.getServersInfoJSON();
        if (serversInfoJSON == null) {
            return Collections.emptyList();
        }
        List<ServerInfo> serverInfoList = new ArrayList<>();
        JSONArray serversInfoArray = (JSONArray) serversInfoJSON.get("Servers");
        for (Object serverInfoJSONObject : serversInfoArray) {
            JSONObject serverInfoJSON = (JSONObject) serverInfoJSONObject;
            String serverName = (String) serverInfoJSON.get("serverName");
            String serverDescription = (String) serverInfoJSON.get("serverDescription");
            String serverIPPort = (String) serverInfoJSON.get("serverIP");

            serverInfoList.add(new ServerInfo(serverName, serverDescription, serverIPPort.split(":")[0], Integer.parseInt(serverIPPort.split(":")[1])));
        }

        return serverInfoList;
    }

    public void serverSelected(Object selectedServer) {
        if (selectedServer instanceof ServerInfo) {
            this.selectedServer = (ServerInfo) selectedServer;
        }
    }

    public void setBasicView(BasicPane basicView) {
        this.basicView = basicView;
    }

    public BackgroundReceiverTask createAndStartBackgroundReceiverTask() {
        BackgroundReceiverTask readServerImageTask = new BackgroundReceiverTask(connection, fileController);
        startTask(readServerImageTask);
        return readServerImageTask;
    }

    public void fillNewsFlow(NewsPane newsPane) {
        if (connection != null) {
            AdvertisementReceiver advertisementReceiver = new RestAdvertisementReceiver(connection);
            List<Advertisement> allAds = advertisementReceiver.receiveAdvertisements();

            for (Advertisement ad : allAds) {
                newsPane.putNewToArea(ad);
            }
        }
    }

    public void reconnectButtonPressed() {
        if (connection == null || !basicView.getConnectionStatus()
                || connection instanceof StubConnection || Settings.getInstance().getLserverURL() != null) {
            establishConnection();
            this.downloadService = new DownloadFilesService(fileController);
            this.loginService = new LoginService(connection);
        }
    }

    public void defaultDirectorySelected(String text) {
        Settings.getInstance().setFilesDir(text);
        if (fileController != null) {
            fileController.updateDirectory();
        }
    }
}
