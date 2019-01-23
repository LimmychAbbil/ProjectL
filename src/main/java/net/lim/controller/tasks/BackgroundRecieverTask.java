package net.lim.controller.tasks;

import javafx.concurrent.Task;
import javafx.scene.image.Image;
import net.lim.controller.FileController;
import net.lim.model.connection.Connection;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class BackgroundRecieverTask extends Task<Image> {

    private final Connection connection;
    private final FileController fileController;

    public BackgroundRecieverTask(Connection connection, FileController controller) {
        this.connection = connection;
        this.fileController = controller;
    }

    @Override
    protected Image call() throws Exception {
        if (connection == null || fileController == null) {
            return null;
        }
        String backgroundName = connection.getBackgroundImageName();
        try {
            File backgroundImage = fileController.getBackgroundImage(backgroundName);
            if (backgroundImage != null && backgroundImage.exists()) {

                return new Image(new FileInputStream(backgroundImage));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
