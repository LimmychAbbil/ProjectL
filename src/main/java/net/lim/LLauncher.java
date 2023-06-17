package net.lim;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.lim.controller.LauncherController;

/**
 * Created by Limmy on 13.03.2018.
 */
public class LLauncher extends Application {
    public final static double MIN_WIDTH = 600;
    public final static double MIN_HEIGHT = 400;
    public static final String PROGRAM_NAME = "LLauncher";
    public static final String PROGRAM_VERSION = "0.05g";

    private LauncherController controller;

    public static void main(String[] args) {
        Application.launch();
    }
    @Override
    public void start(Stage primaryStage) {
        controller = new LauncherController(primaryStage, getHostServices());
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("LLauncher");

        controller.init();

        addIcon(primaryStage);

        Scene firstScene = new Scene(controller.getOrCreateBasicView(),
                controller.getDefaultWidth(), controller.getDefaultHeight());

        primaryStage.setScene(firstScene);

        primaryStage.show();
    }

    private void addIcon(Stage primaryStage) {
        Image icon = new Image(this.getClass().getClassLoader().getResource("icon.ico").toString());
        primaryStage.getIcons().add(icon);
    }


}
