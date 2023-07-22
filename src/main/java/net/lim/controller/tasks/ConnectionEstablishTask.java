package net.lim.controller.tasks;

import javafx.concurrent.Task;
import net.lim.LLauncher;
import net.lim.controller.StageController;
import net.lim.model.Settings;
import net.lim.model.connection.Connection;
import net.lim.model.connection.RestConnection;
import net.lim.model.connection.StubConnection;
import net.lim.service.ConfigReader;
import org.apache.commons.lang3.StringUtils;

public class ConnectionEstablishTask extends Task<Connection> {

    private final StageController stageController;
    private String errorMessage;

    public ConnectionEstablishTask(StageController stageController) {
        this.stageController = stageController;
    }

    @Override
    protected Connection call() {
        Connection connection = establishConnection();
        if (connection == null) {
            throw new RuntimeException("Connection creating failed: " + errorMessage);
        } else if (!connection.validateConnection() || StringUtils.isNotEmpty(errorMessage)) {
            throw new RuntimeException("Connection is not OK: " + errorMessage);
        } else {
            return connection;
        }
    }

    private Connection establishConnection() {
        Connection connection = null;
        String launchServerURL;
        if (Settings.getInstance().getLserverURL() == null) {
            //can't be null here
            launchServerURL = ConfigReader.getProperties().getProperty("server.ip");
        } else {
            launchServerURL = Settings.getInstance().getLserverURL();
        }
        boolean connectionOK = false;
        errorMessage = null;
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

        return connection;
    }
}
