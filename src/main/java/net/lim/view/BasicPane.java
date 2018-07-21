package net.lim.view;

import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
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
    private RegistrationPane registrationPane;
    private ProgressView progressView;

    public BasicPane(LauncherController controller) throws IOException {
        this.controller = controller;
        init();
    }

    public void init() throws IOException {
        addBackgroundImage();
        initHeaderPane();
        initNewsPane();
        initRegistrationPane();
        initLoginPane();
        initFileCheckView();
        addContent();
    }

    private void initFileCheckView() {
        progressView = new ProgressView();
        progressView.layoutXProperty().bind(this.layoutXProperty());
        progressView.layoutYProperty().bind(this.layoutYProperty().add(headerPane.heightProperty()));
        progressView.prefHeightProperty().bind(this.heightProperty().add(headerPane.heightProperty().multiply(-1)).add(loginPane.heightProperty().multiply(-1)));
        progressView.prefWidthProperty().bind(this.widthProperty());
        progressView.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
        progressView.setOpacity(0.9);
        progressView.setVisible(false);

        controller.setProgressView(progressView);
    }

    private void initRegistrationPane() {
        registrationPane = new RegistrationPane(controller);
        registrationPane.setVisible(false);
        registrationPane.layoutYProperty().bind(this.heightProperty().divide(2.5));
        registrationPane.layoutXProperty().bind(this.widthProperty().add(registrationPane.widthProperty().multiply(-1)));
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
        getChildren().addAll(headerPane, newsPane, loginPane,registrationPane, progressView);
    }

    private void initLoginPane() {
        loginPane = new LoginPane(controller, registrationPane);

        loginPane.minWidthProperty().bind(this.widthProperty());
        loginPane.maxWidthProperty().bind(this.widthProperty());
        loginPane.layoutYProperty().bind(this.heightProperty().add(-48));
    }

    private void addBackgroundImage()  {
        Image backgroundImage = null;
        try {
            backgroundImage = new Image(new FileInputStream(getBackgroundImagePath()));
        } catch (FileNotFoundException e) {
            //TODO set default background image
            e.printStackTrace();
        }
        BackgroundImage backGround = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        setBackground(new Background(backGround));
    }

    private String getBackgroundImagePath() {
        //TODO it is a stub
        return "./src/main/resources/background.jpg";
    }
}
