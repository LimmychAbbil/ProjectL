package net.lim.controller.tasks;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import net.lim.controller.FileController;

public class DownloadFilesService extends Service<Void> {
    private FileController fileController;

    public DownloadFilesService(FileController controller) {
        this.fileController = controller;
    }


    @Override
    protected Task<Void> createTask() {
        fileController.getFileManager().resetProgressCounter();
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                updateMessage("Downloading files...");
                try {
                    fileController.deleteFiles();
                    fileController.initFTPConnection();
                    for (String fileName : fileController.getFileNames()) {
                        fileController.downloadFile(fileName);
                        updateProgress(fileController.getFileManager().getProgressCounter(), fileController.getFileManager().getTotalFilesSize());
                        updateMessage("Downloading files: " + fileController.getFileManager().getProgressCounter() +
                                " / " + fileController.getFileManager().getTotalFilesSize());
                    }
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
}