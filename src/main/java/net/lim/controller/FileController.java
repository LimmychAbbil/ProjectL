package net.lim.controller;

import net.lim.model.Connection;
import net.lim.model.FileGetter;
import org.json.simple.JSONObject;

public class FileController {
    FileGetter fileGetter;
    private Connection connection;

    public FileController(Connection connection) {
        this.connection = connection;
        JSONObject ftpFileInto = this.connection.getFileServerInfo();
        fileGetter = new FileGetter(ftpFileInto);
        fileGetter.parseIgnoredDirs(connection.getIgnoredDirsInfo());
        fileGetter.setRemoteHashInfo(connection.getFullHashInfo());
    }

    public boolean checkFiles() {
        return fileGetter.checkFiles();
    }
}
