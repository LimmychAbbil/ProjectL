package net.lim.controller;

import net.lim.model.FileManager;
import net.lim.model.Settings;
import net.lim.model.connection.Connection;
import net.lim.view.ProgressView;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;

import static net.lim.model.FileManager.DEFAULT_DIRECTORY;

public class FileController {
    private FileManager fileManager;
    private String defaultDir;
    private ProgressView progressView;

    FileController(Connection connection, ProgressView progressView) {
        JSONObject ftpFileInto = connection.getFileServerInfo();
        fileManager = new FileManager(ftpFileInto);
        fileManager.parseIgnoredFiles(connection.getIgnoredFilesInfo());
        fileManager.setRemoteHashInfo(connection.getFullHashInfo());
        defaultDir = DEFAULT_DIRECTORY;
        this.progressView = progressView;
    }

    boolean checkFiles() {
        return fileManager.checkFiles();
    }

    public void deleteFiles() {
       fileManager.deleteFiles(Paths.get(defaultDir));
    }

    public void initFTPConnection() throws IOException {
        fileManager.initFTPConnection();
    }
    public Collection<String> getFileNames() throws Exception {
        return fileManager.getFileNames();
    }


    public void downloadFile(String fileName) throws Exception {
        fileManager.downloadFile(fileName);
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public File getBackgroundImage(String backgroundName) throws IOException {
        File backgroundsDir = new File(DEFAULT_DIRECTORY + "backgrounds/");
        if (!backgroundsDir.exists()) {
            try {
                boolean successfulCreatedDirectory = backgroundsDir.mkdirs();
                if (!successfulCreatedDirectory) {
                    throw new IOException("Unsuccessful dir creation");
                }
            } catch (IOException e) {
                System.err.println("Can't create backgrounds dir. Default background will be used");
                e.printStackTrace();
                return null;
            }
        }
        File image = new File(backgroundsDir, backgroundName);
        if (!image.exists()) {
            downloadBackgroundImage(backgroundName, image.getName());
        }
        return image;
    }

    private void downloadBackgroundImage(String backgroundName, String localFileName) throws IOException {
        fileManager.initFTPConnection();
        fileManager.downloadFile("backgrounds/" + backgroundName, "backgrounds/" + localFileName);
        fileManager.closeFTPConnection();
    }

    public void updateDirectory() {
        if (StringUtils.isNotEmpty(Settings.getInstance().getFilesDir())) {
            this.defaultDir = Settings.getInstance().getFilesDir();
            fileManager.setFilesDirectory(Paths.get(defaultDir));
        }
    }
}
