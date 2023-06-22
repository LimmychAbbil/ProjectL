package net.lim.controller;

import net.lim.model.Settings;
import net.lim.view.SettingsPane;

public class SettingsController implements Controller {

    private SettingsPane settingsView;

    private final FileController fileController;
    private final ConnectionController connectionController;

    public SettingsController(ConnectionController connectionController, FileController fileController) {
        this.fileController = fileController;
        this.connectionController = connectionController;
    }

    @Override
    public void init() {

    }

    public void defaultDirectorySelected(String text) {
        Settings.getInstance().setFilesDir(text);
        if (fileController != null) {
            fileController.updateDirectory();
        }
    }

    public SettingsPane getOrCreateSettingsPane() {
        if (this.settingsView == null) {
            this.settingsView = new SettingsPane(this);
        }

        return settingsView;
    }


    public ConnectionController getConnectionController() {
        return connectionController;
    }
}
