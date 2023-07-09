package net.lim;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.lim.controller.StageController;

/**
 * Created by Limmy on 13.03.2018.
 */
public class LLauncher extends Application {
    public final static double MIN_WIDTH = 600;
    public final static double MIN_HEIGHT = 400;
    public static final String PROGRAM_NAME = "LLauncher";
    public static final String PROGRAM_VERSION = "0.05o";

    private StageController controller;

    private static HostServices hostServices;

    public static void main(String[] args) {
        Application.launch();
    }

    public static HostServices getFXHostServices() {
        return hostServices;
    }

    @Override
    public void start(Stage primaryStage) {
        hostServices = getHostServices();
        controller = new StageController(primaryStage);
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
