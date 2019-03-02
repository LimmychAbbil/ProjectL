package net.lim.controller.tasks;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import net.lim.controller.FileController;
import net.lim.model.Settings;
import org.json.simple.JSONObject;

import java.util.Collection;

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
                if (!Settings.getInstance().isOfflineMode()) {
                    Collection<String> remoteFiles = fileController.getFileNames();
                    Collection<String> localFiles = fileController.getFileManager().getAllLocalFiles(fileController.getFileManager().getFilesDirectory());
                    if (remoteFiles.size() != localFiles.size()) {
                        //TODO print to console
                        updateMessage("Size differ: " + localFiles.size() + " but expected " + remoteFiles.size());

                        localFiles.stream().filter(key -> !remoteFiles.contains(key))
                                .forEach(o -> System.out.println(o + " is not allowed locally"));

                        return false;
                    }

                    //TODO add progress here
                    try {
                        JSONObject localHash = fileController.getFileManager().readAllLocalFilesHash();
                        for (Object localFileNameObject : localHash.keySet()) {
                            if (!fileController.getFileManager().checkFile(localFileNameObject)) {
                                //TODO print to console
                                updateMessage(localFileNameObject + " is different");
                                return false;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                return true;
            }
        };
    }
}
