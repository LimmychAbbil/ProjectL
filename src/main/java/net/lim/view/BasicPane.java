package net.lim.view;

import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import net.lim.controller.StageController;

import java.net.URL;

/**
 * Created by Limmy on 28.04.2018.
 */
public class BasicPane extends Pane {
    private final static String DEFAULT_BACKGROUND_IMAGE_NAME = "background.jpg";

    private final StageController controller;
    private HeaderPane headerPane;
    private NewsPane newsPane;
    private LoginPane loginPane;
    private RegistrationPane registrationPane;
    private SettingsPane settingsPane;
    private ProgressView progressView;
    private ImageView lServerConnectionStatusIconView;

    private final static URL offlineIconURL = BasicPane.class.getClassLoader().getResource("icons/connection.status/offline.png");
    private final static URL connectingIconURL = BasicPane.class.getClassLoader().getResource("icons/connection.status/connecting.png");
    private final static URL onlineIconURL = BasicPane.class.getClassLoader().getResource("icons/connection.status/online.png");

    public static final Image ONLINE_ICON = new Image(onlineIconURL.toString(), true);
    public static final Image OFFLINE_ICON = new Image(offlineIconURL.toString(), true);
    public static final Image CONNECTING_ICON = new Image(connectingIconURL.toString(), true);

    public BasicPane(StageController controller) {
        this.controller = controller;
        init();
    }

    public void init() {
        initHeaderPane();
        initNewsPane();
        initConnectionStatusIconView();
        initSettingsPane();
        initRegistrationPane();
        initLoginPane();
        initFileCheckView();
        addContent();
        addAllStyleSheets();
        addBackgroundImage(getDefaultBackgroundImage());
    }

    public void setConnectionStatus(boolean isConnected, String message) {
        if (isConnected) {
            controller.setConnectionStatusIcon(lServerConnectionStatusIconView, ONLINE_ICON);
            lServerConnectionStatusIconView.setAccessibleText("Connected to the server");
        } else {
            controller.setConnectionStatusIcon(lServerConnectionStatusIconView, OFFLINE_ICON);
            lServerConnectionStatusIconView.setAccessibleText(message);
        }
        if (lServerConnectionStatusIconView.getAccessibleText() != null) {
            Tooltip.install(lServerConnectionStatusIconView, new Tooltip(lServerConnectionStatusIconView.getAccessibleText()));
        }
    }

    public void setConnectionStatus(boolean isConnected) {
        setConnectionStatus(isConnected, null);
    }

    public ProgressView getProgressView() {
        return progressView;
    }

    public ImageView getlServerConnectionStatusIconView() {
        return lServerConnectionStatusIconView;
    }

    private void initConnectionStatusIconView() {
        lServerConnectionStatusIconView = new ImageView();
        lServerConnectionStatusIconView.xProperty().bind(this.widthProperty().add(-48));
        lServerConnectionStatusIconView.yProperty().bind(headerPane.heightProperty().add(24));

        controller.setConnectionStatusIcon(lServerConnectionStatusIconView, CONNECTING_ICON);
        lServerConnectionStatusIconView.setOnMouseClicked(e -> this.settingsPane.setVisible(!settingsPane.isVisible()));
    }

    private void initSettingsPane() {
        this.settingsPane = controller.getLauncherController().getOrCreateSettingController().getOrCreateSettingsPane();

        this.settingsPane.setVisible(false);
        this.settingsPane.layoutYProperty().bind(lServerConnectionStatusIconView.yProperty().add(32));
        this.settingsPane.layoutXProperty().bind(this.widthProperty().add(settingsPane.widthProperty().multiply(-1)));
    }

    public void postInitAfterConnect() {
        loginPane.updateServersList();
        newsPane.postInit();
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
    }

    private void initRegistrationPane() {
        registrationPane = new RegistrationPane(controller.getLoginController().getOrCreateRegistrationController());
        registrationPane.setVisible(false);
        registrationPane.layoutYProperty().bind(settingsPane.layoutYProperty().add(settingsPane.heightProperty()).add(16));
        registrationPane.layoutXProperty().bind(this.widthProperty().add(registrationPane.widthProperty().multiply(-1)));
    }

    private void initNewsPane() {
        newsPane = new NewsPane(controller.getNewsController());

        newsPane.prefWidthProperty().bind(this.widthProperty().divide(3));
        newsPane.prefHeightProperty().bind(this.heightProperty().divide(1.5));
    }

    private void initHeaderPane() {
        headerPane = new HeaderPane(controller);

        headerPane.minWidthProperty().bind(this.widthProperty());
        headerPane.maxWidthProperty().bind(this.widthProperty());
    }

    private void addContent() {
        getChildren().addAll(headerPane, newsPane, loginPane, settingsPane, registrationPane, lServerConnectionStatusIconView, progressView);
    }

    private void initLoginPane() {
        loginPane = new LoginPane(controller.getLoginController(), registrationPane);

        loginPane.minWidthProperty().bind(this.widthProperty());
        loginPane.maxWidthProperty().bind(this.widthProperty());
        loginPane.layoutYProperty().bind(this.heightProperty().add(-48));
    }

    public void addBackgroundImage(Image backgroundImage) {
        if (backgroundImage == null) {
            backgroundImage = getDefaultBackgroundImage();
        }

        BackgroundImage backGround = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        setBackground(new Background(backGround));
    }

    private Image getDefaultBackgroundImage() {
        URL defaultBackgroundURL = this.getClass().getClassLoader().getResource(DEFAULT_BACKGROUND_IMAGE_NAME);
        if (defaultBackgroundURL == null) {
            throw new IllegalStateException("Can't find default background");
        }
        return new Image(defaultBackgroundURL.toString(), true);
    }

    private void addAllStyleSheets() {
        this.getStylesheets().add("styles/lStyleButtons.css");
    }
}
