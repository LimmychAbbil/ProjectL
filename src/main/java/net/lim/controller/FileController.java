package net.lim.controller;

import net.lim.model.Connection;
import net.lim.model.FileManager;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileController {
    FileManager fileManager;
    private Connection connection;
    private String defaultDir;
    private Path homePath;

    FileController(Connection connection) {
        this.connection = connection;
        JSONObject ftpFileInto = this.connection.getFileServerInfo();
        fileManager = new FileManager(ftpFileInto);
        fileManager.parseIgnoredDirs(connection.getIgnoredDirsInfo());
        fileManager.setRemoteHashInfo(connection.getFullHashInfo());
        defaultDir = FileManager.getDefaultDirectory();
        homePath = Paths.get(defaultDir);
    }

    boolean checkFiles() {
        return fileManager.checkFiles();
    }

    void deleteFiles() {
       fileManager.deleteFiles(homePath);
    }

    void downloadFiles() throws Exception {
        fileManager.downloadFiles();
    }
}
