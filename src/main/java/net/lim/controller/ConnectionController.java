package net.lim.controller;

import net.lim.LLauncher;
import net.lim.model.Settings;
import net.lim.model.connection.Connection;
import net.lim.model.connection.RestConnection;
import net.lim.model.connection.StubConnection;
import net.lim.service.ConfigReader;

import java.io.InputStream;
import java.util.Properties;

public class ConnectionController implements Controller {

    private static ConnectionController instance;

    private static Connection connection;
    private LauncherController launcherController;
    private StageController stageController;

    public static ConnectionController getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Connection controller not ready, " +
                    "instantiate with #getInstanse(LauncherController, StageController) first");
        }

        return instance;

    }

    public static ConnectionController getInstance(LauncherController launcherController, StageController stageController) {
        if (instance == null) {
            instance = new ConnectionController(launcherController, stageController);
        }

        return instance;
    }

    public Connection getConnection() {
        if (connection == null || connection.isClosed()) {
            return null;
        } else {
            return connection;
        }
    }

    private ConnectionController(LauncherController launcherController, StageController stageController) {
        this.launcherController = launcherController;
        this.stageController = stageController;
    }

    @Override
    public void init() {
        establishConnection();
    }

    public void reconnectButtonPressed() {
        if (Settings.getInstance().isOfflineMode()) {
            connection = null;
        } else if (connection == null || !connection.validateConnection()
                || connection instanceof StubConnection || Settings.getInstance().getLserverURL() != null) {
            establishConnection();
            launcherController.createOrUpdateDownloadFileService();
        }
    }

    private void establishConnection() {
        String launchServerURL;
        if (Settings.getInstance().getLserverURL() == null) {
            //can't be null here
            launchServerURL = ConfigReader.getProperties().getProperty("server.ip");
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

                    launcherController.initFileController();
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
        stageController.updateConnectionStatus(connectionOK, errorMessage);
    }
}
