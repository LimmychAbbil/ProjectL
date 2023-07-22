package net.lim.controller;

import net.lim.controller.tasks.ConnectionEstablishTask;
import net.lim.controller.tasks.ui.RotateStatusBarTask;
import net.lim.model.Settings;
import net.lim.model.connection.Connection;
import net.lim.model.connection.StubConnection;

import java.util.concurrent.atomic.AtomicReference;

public class ConnectionController implements Controller {

    private static ConnectionController instance;

    private static Connection connection;
    private final LauncherController launcherController;
    private final StageController stageController;
    private final ConnectionEstablishTask connectionEstablishTask;

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
        this.connectionEstablishTask = new ConnectionEstablishTask(stageController);
        initOnRunningAndOnExitForConnectionTask();
    }


    @Override
    public void init() {
        startTask(connectionEstablishTask);

    }

    public void reconnectButtonPressed() {
        if (Settings.getInstance().isOfflineMode()) {
            connection = null;
        } else if (connection == null || !connection.validateConnection()
                || connection instanceof StubConnection || Settings.getInstance().getLserverURL() != null) {
            startTask(connectionEstablishTask);
            launcherController.createOrUpdateDownloadFileService();
        }
    }

    private void initOnRunningAndOnExitForConnectionTask() {
        AtomicReference<RotateStatusBarTask> rotateStatusBarTask = new AtomicReference<>();
        connectionEstablishTask.setOnRunning(event -> {
            rotateStatusBarTask.set(stageController.createAndStartRotateStatusIconTask());
        });

        connectionEstablishTask.setOnSucceeded(event -> {
            connection = connectionEstablishTask.getValue();
            rotateStatusBarTask.get().cancel(false);
            launcherController.initFileController();
            launcherController.createAndStartBackgroundReceiverTask();
            stageController.getOrCreateBasicView().postInitAfterConnect();
        });
    }
}
