package net.lim;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.lim.controller.LauncherController;
import net.lim.view.BasicPane;

import java.io.FileNotFoundException;

/**
 * Created by Limmy on 13.03.2018.
 */
public class LLauncher extends Application {
    public final static double MIN_WIDTH = 600;
    public final static double MIN_HEIGHT = 400;
    public static final String PROGRAM_NAME = "LLauncher";
    public static final String PROGRAM_VERSION = "0.04l";

    private LauncherController controller;

    public static void main(String[] args) {
        Application.launch();
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        controller = new LauncherController(primaryStage, getHostServices());
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("LLauncher");
        BasicPane basicPane = new BasicPane(controller);
        controller.setBasicView(basicPane);
        controller.init();

        addIcon(primaryStage);

        Scene firstScene = new Scene(basicPane, controller.getDefaultWidth(), controller.getDefaultHeight());

        primaryStage.setScene(firstScene);

        primaryStage.show();
    }

    private void addIcon(Stage primaryStage) {
        Image icon = new Image(this.getClass().getClassLoader().getResource("icon.ico").toString());
        primaryStage.getIcons().add(icon);
    }


}
