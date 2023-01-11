package net.lim.controller.tasks;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import net.lim.controller.FileController;
import net.lim.model.FileManager;
import net.lim.model.Settings;
import org.json.simple.JSONObject;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

public class FileCheckerService extends Service<Boolean> {

    private final FileController fileController;

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
                        String errorMessage = "Size differ: " + localFiles.size() + " but expected " + remoteFiles.size();
                        System.out.println(errorMessage);
                        updateMessage(errorMessage);

                        localFiles.stream().filter(key -> !remoteFiles.contains(key))
                                .forEach(o -> System.out.println(o + " is not allowed locally"));

                        return false;
                    }

                    //TODO add progress here
                    try {
                        JSONObject localHash = readAllLocalFilesHash(localFiles, fileController.getFileManager().getFilesDirectory());
                        for (Object localFileNameObject : localHash.keySet()) {
                            if (!fileController.getFileManager().checkFile(localFileNameObject)) {
                                String errorMessage = localFileNameObject + " is different";
                                System.out.println(errorMessage);
                                updateMessage(errorMessage);
                                return false;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                return true;
            }

            public JSONObject readAllLocalFilesHash(Collection<String> allLocalFiles, Path filesDirectory) {
                JSONObject localHashInfo = new JSONObject();
                int progressCounter = 0;

                for (String fileName : allLocalFiles) {
                    Path localFile = Paths.get(filesDirectory.toString(), fileName).toAbsolutePath();
                    localHashInfo.put(fileName, FileManager.computeMD5ForFile(localFile));
                    updateMessage("Checking files... " + progressCounter++ + "/" + allLocalFiles.size());
                }
                fileController.getFileManager().setLocalHashInfo(localHashInfo);
                return localHashInfo;
            }
        };
    }
}
