package net.lim.controller.tasks;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import net.lim.controller.FileController;
import net.lim.model.Settings;

public class FileCheckerService extends Service<Boolean> {

    private FileController fileController;

    public FileCheckerService(FileController fileController) {
        this.fileController = fileController;
    }

    @Override
    public void start() {
        this.reset();
        super.start();
    }

    @Override
    protected Task<Boolean> createTask()  {
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                updateMessage("Check files");
                if (Settings.getInstance().isOfflineMode()) return true;
                return fileController.checkFiles();
            }
        };
    }
}
