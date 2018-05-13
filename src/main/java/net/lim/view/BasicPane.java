package net.lim.view;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import net.lim.controller.LauncherController;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Limmy on 28.04.2018.
 */
public class BasicPane extends Pane {
    private final LauncherController controller;
    private HeaderPane headerPane;
    private NewsPane newsPane;
    private LoginPane loginPane;

    public BasicPane(LauncherController controller) throws IOException {
        this.controller = controller;
        init();
    }

    public void init() throws IOException {
        addBackgroundImage();
        initHeaderPane();
        initNewsPane();
        initLoginPane();
        addContent();
    }

    private void initNewsPane() {
        newsPane = new NewsPane(controller);

        newsPane.prefWidthProperty().bind(this.widthProperty().divide(3));
        newsPane.prefHeightProperty().bind(this.heightProperty().divide(1.5));
    }

    private void initHeaderPane() throws IOException {
        headerPane = new HeaderPane(controller);

        headerPane.minWidthProperty().bind(this.widthProperty());
        headerPane.maxWidthProperty().bind(this.widthProperty());
    }

    private void addContent() {
        getChildren().addAll(headerPane, newsPane, loginPane);
    }

    private void initLoginPane() {
        loginPane = new LoginPane(controller);

        loginPane.minWidthProperty().bind(this.widthProperty());
        loginPane.maxWidthProperty().bind(this.widthProperty());
        loginPane.layoutYProperty().bind(this.heightProperty().add(-48));
    }

    private void addBackgroundImage()  {
        Image backgroundImage = null;
        try {
            String backgroundImagePath = getBackgroundImagePath();
            backgroundImage = new Image(new FileInputStream(backgroundImagePath));
        } catch (FileNotFoundException e) {
            //TODO set default background image
            e.printStackTrace();
        }
        BackgroundImage backGround = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        setBackground(new Background(backGround));
    }

    private String getBackgroundImagePath() {
        //TODO it is a stub
        return "./ProjectL/src/main/java/background.jpg";
    }
}
