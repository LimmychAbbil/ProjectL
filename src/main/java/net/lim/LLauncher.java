package net.lim;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.lim.controller.LauncherController;
import net.lim.view.BasicPane;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by Limmy on 13.03.2018.
 */
public class LLauncher extends Application {
    public final static double MIN_WIDTH = 600;
    public final static double MIN_HEIGHT = 400;
    public static final String PROGRAM_NAME = "LLauncher";
    public static final String PROGRAM_VERSION = "0.01g";

    private LauncherController controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        controller = new LauncherController(primaryStage, getHostServices());
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("LLauncher");
        Pane basicPane = new BasicPane(controller);

        addIcon(primaryStage);

        Scene firstScene = new Scene(basicPane, controller.getDefaultWidth(), controller.getDefaultHeight());

        primaryStage.setScene(firstScene);

        primaryStage.show();
    }

    private void addIcon(Stage primaryStage) throws FileNotFoundException {
        Image icon = new Image(new FileInputStream("./src/main/resources/icon.ico"));
        primaryStage.getIcons().add(icon);
    }


}
