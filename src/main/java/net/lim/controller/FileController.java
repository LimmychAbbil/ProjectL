package net.lim.controller;

import net.lim.model.Connection;
import net.lim.model.FileManager;
import net.lim.view.ProgressView;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

public class FileController {
    FileManager fileManager;
    private Connection connection;
    private String defaultDir;
    private Path homePath;
    private ProgressView progressView;

    FileController(Connection connection, ProgressView progressView) {
        this.connection = connection;
        JSONObject ftpFileInto = this.connection.getFileServerInfo();
        fileManager = new FileManager(ftpFileInto);
        fileManager.parseIgnoredDirs(connection.getIgnoredDirsInfo());
        fileManager.setRemoteHashInfo(connection.getFullHashInfo());
        defaultDir = FileManager.getDefaultDirectory();
        homePath = Paths.get(defaultDir);
        this.progressView = progressView;
    }

    boolean checkFiles() {
        return fileManager.checkFiles();
    }

    void deleteFiles() {
       fileManager.deleteFiles(homePath);
    }

    void initFTPConnection() throws IOException {
        fileManager.initFTPConnection();
    }
    Collection<String> getFileNames() throws Exception {
        return fileManager.getFileNames();
    }


    void downloadFile(String fileName) throws Exception {
        fileManager.downloadFile(fileName);
    }

    public FileManager getFileManager() {
        return fileManager;
    }
}
