package net.lim.view;

import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import net.lim.controller.LauncherController;
import net.lim.controller.tasks.BackgroundReceiverTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

/**
 * Created by Limmy on 28.04.2018.
 */
public class BasicPane extends Pane {
    private final static String DEFAULT_BACKGROUND_IMAGE_NAME = "background.jpg";

    private final LauncherController controller;
    private HeaderPane headerPane;
    private NewsPane newsPane;
    private LoginPane loginPane;
    private RegistrationPane registrationPane;
    private SettingsPane settingsPane;
    private ProgressView progressView;
    private ImageView lServerConnectionStatusIconView;

    private final static URL offlineIconURL = BasicPane.class.getClassLoader().getResource("icons/connection.status/offline.png");
    private final static URL onlineIconURL = BasicPane.class.getClassLoader().getResource("icons/connection.status/online.png");

    public BasicPane(LauncherController controller) throws IOException {
        this.controller = controller;
        init();
    }

    public void init() throws IOException {
        addBackgroundImage();
        initHeaderPane();
        initNewsPane();
        initConnectionStatusIconView();
        initSettingsPane();
        initRegistrationPane();
        initLoginPane();
        initFileCheckView();
        addContent();
        addAllStyleSheets();
    }

    private void initSettingsPane() {
        this.settingsPane = new SettingsPane(controller);
        settingsPane.setVisible(false);
        settingsPane.layoutYProperty().bind(lServerConnectionStatusIconView.yProperty().add(32));
        settingsPane.layoutXProperty().bind(this.widthProperty().add(settingsPane.widthProperty().multiply(-1)));
    }

    private void postInitAfterConnect() {
        addBackgroundImage();
        loginPane.updateServersList();

        newsPane.postInit();
    }

    public void setConnectionStatus(boolean isConnected, String message) {
        if (isConnected) {
            lServerConnectionStatusIconView.imageProperty().set(new Image(onlineIconURL.toString(), true));
            lServerConnectionStatusIconView.setAccessibleText("Connected to the server");
            postInitAfterConnect();
        } else {
            lServerConnectionStatusIconView.imageProperty().set(new Image(offlineIconURL.toString(), true));
            lServerConnectionStatusIconView.setAccessibleText(message);
        }
        if (lServerConnectionStatusIconView.getAccessibleText() != null) {
            Tooltip.install(lServerConnectionStatusIconView, new Tooltip(lServerConnectionStatusIconView.getAccessibleText()));
        }
    }

    public void setConnectionStatus(boolean isConnected) {
        setConnectionStatus(isConnected, null);
    }

    public boolean getConnectionStatus() {
        return "Connected to the server".equals(lServerConnectionStatusIconView.accessibleTextProperty().get());
    }

    private void initConnectionStatusIconView() {
        lServerConnectionStatusIconView = new ImageView();
        lServerConnectionStatusIconView.xProperty().bind(this.widthProperty().add(-48));
        lServerConnectionStatusIconView.yProperty().bind(headerPane.heightProperty().add(24));

        lServerConnectionStatusIconView.imageProperty().set(new Image(offlineIconURL.toString(), true));
        lServerConnectionStatusIconView.setOnMouseClicked(e -> this.settingsPane.setVisible(!settingsPane.isVisible()));
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
        registrationPane.layoutYProperty().bind(settingsPane.layoutYProperty().add(settingsPane.heightProperty()).add(16));
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
        getChildren().addAll(headerPane, newsPane, loginPane, settingsPane, registrationPane, lServerConnectionStatusIconView, progressView);
    }

    private void initLoginPane() {
        loginPane = new LoginPane(controller, registrationPane);

        loginPane.minWidthProperty().bind(this.widthProperty());
        loginPane.maxWidthProperty().bind(this.widthProperty());
        loginPane.layoutYProperty().bind(this.heightProperty().add(-48));
    }

    private void addBackgroundImage() {
        BackgroundReceiverTask backgroundImageTask = controller.createAndStartBackgroundReceiverTask();
        backgroundImageTask.setOnSucceeded(e -> {
            Image backgroundImage = backgroundImageTask.getValue();
            if (backgroundImage == null) {
                backgroundImage = getDefaultBackgroundImage();
            }
            BackgroundImage backGround = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
            setBackground(new Background(backGround));
        });
    }

    private Image getDefaultBackgroundImage() {
        try {
            URL defaultBackgroundURL = this.getClass().getClassLoader().getResource(DEFAULT_BACKGROUND_IMAGE_NAME);
            if (defaultBackgroundURL != null) {
                return new Image(defaultBackgroundURL.toString(), true);
            } else {
                throw new FileNotFoundException("Can't find default background");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void addAllStyleSheets() {
        this.getStylesheets().add("styles/lStyleButtons.css");
    }
}
