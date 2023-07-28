package net.lim.controller;

import net.lim.controller.tasks.ConnectionEstablishService;
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
    private final ConnectionEstablishService connectionEstablishService;

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
        this.connectionEstablishService = new ConnectionEstablishService(stageController);
        initOnRunningAndOnExitForConnectionTask();
    }


    @Override
    public void init() {
        connectionEstablishService.start();

    }

    public void reconnectButtonPressed() {
        if (Settings.getInstance().isOfflineMode()) {
            connection = null;
        } else if (connection == null || !connection.validateConnection()
                || connection instanceof StubConnection || Settings.getInstance().getLserverURL() != null) {
            connectionEstablishService.restart();
        }
    }

    private void initOnRunningAndOnExitForConnectionTask() {
        AtomicReference<RotateStatusBarTask> rotateStatusBarTask = new AtomicReference<>();
        connectionEstablishService.setOnRunning(event -> {
            rotateStatusBarTask.set(stageController.createAndStartRotateStatusIconTask());
        });

        connectionEstablishService.setOnSucceeded(event -> {
            connection = connectionEstablishService.getValue();
            rotateStatusBarTask.get().cancel(false);
            launcherController.initFileController();
            launcherController.createAndStartBackgroundReceiverTask();
            stageController.getOrCreateBasicView().postInitAfterConnect();
        });

        connectionEstablishService.setOnFailed(event -> {
            rotateStatusBarTask.get().cancel(false);
        });
    }
}
