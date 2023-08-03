package net.lim.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import net.lim.LLauncher;
import net.lim.controller.tasks.ui.RotateStatusBarTask;
import net.lim.view.BasicPane;

public class StageController implements Controller {

    private final Stage primaryStage;
    private double currentX, currentY, dragOffsetX, dragOffsetY;
    private boolean isMaximized;

    private BasicPane basicView;
    private final LauncherController launcherController;
    private final ConnectionController connectionController;
    private final LoginController loginController;
    private final NewsController newsController;

    public StageController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initializeDefaultXAndY(primaryStage);
        this.launcherController = new LauncherController(this);
        this.connectionController = ConnectionController.getInstance(launcherController, this);
        this.loginController = new LoginController(this);
        this.newsController = new NewsController();
    }

    @Override
    public void init() {
        getOrCreateBasicView();
        newsController.init();
        connectionController.init();
        launcherController.init();
        loginController.init();
    }

    public BasicPane getOrCreateBasicView() {
        if (basicView == null) {
            this.basicView = new BasicPane(this);
        }
        return basicView;
    }

    public void handleMouseDragged(MouseEvent e) {
        currentX = e.getScreenX() - this.dragOffsetX;
        currentY = e.getScreenY() - this.dragOffsetY;
        primaryStage.setX(currentX);
        primaryStage.setY(currentY);
    }

    public void handleMousePress(MouseEvent e) {
        this.dragOffsetX = e.getScreenX() - primaryStage.getX();
        this.dragOffsetY = e.getScreenY() - primaryStage.getY();
    }

    public void maximizePressed() {
        if (!isMaximized) {
            maximizeStage(primaryStage);
            isMaximized = true;
        } else {
            deMaximizeStage(primaryStage);
            isMaximized = false;
        }
    }

    public double getDefaultWidth() {
        double maxWidth = Screen.getPrimary().getBounds().getWidth();
        double defaultWidth = 0.6 * maxWidth;
        return Math.max(defaultWidth, LLauncher.MIN_WIDTH);
    }

    public double getDefaultHeight() {
        double maxHeight = Screen.getPrimary().getBounds().getHeight();
        double defaultHeight = 0.6 * maxHeight;
        return Math.max(defaultHeight, LLauncher.MIN_HEIGHT);
    }

    public void closeButtonPressed() {
        Platform.exit();
    }

    public void minimizedPressed() {
        primaryStage.setIconified(true);
    }

    public LauncherController getLauncherController() {
        return launcherController;
    }

    private void initializeDefaultXAndY(Stage primaryStage) {
        currentX = primaryStage.getX();
        currentY = primaryStage.getY();
    }

    private void deMaximizeStage(Stage stage) {
        stage.setX(Double.isNaN(currentX) ? 0.0 : currentX);
        stage.setY(Double.isNaN(currentY) ? 0.0 : currentY);
        stage.setWidth(getDefaultWidth());
        stage.setHeight(getDefaultHeight());
    }

    private void maximizeStage(Stage stage) {
        currentX = stage.getX();
        currentY = stage.getY();
        stage.setX(0.0);
        stage.setY(0.0);
        stage.setWidth(Screen.getPrimary().getBounds().getWidth());
        stage.setHeight(Screen.getPrimary().getBounds().getHeight());
    }

    public LoginController getLoginController() {
        return loginController;
    }

    public NewsController getNewsController() {
        return newsController;
    }

    public Task<Void> createWaitingTask(long milis) {

        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(milis);
                getOrCreateBasicView().getProgressView().setVisible(false);
                return null;
            }
        };
    }

    public void setConnectionStatusIcon(ImageView iconView, Image image) {
        iconView.imageProperty().set(image);
    }

    public void updateConnectionStatus(boolean connectionOK, String errorMessage) {
        basicView.setConnectionStatus(connectionOK, errorMessage);
    }

    public RotateStatusBarTask createAndStartRotateStatusIconTask() {
        setConnectionStatusIcon(basicView.getlServerConnectionStatusIconView(), BasicPane.CONNECTING_ICON);
        RotateStatusBarTask rotateStatusBarTask = new RotateStatusBarTask(basicView.getlServerConnectionStatusIconView());
        startTask(rotateStatusBarTask);

        return rotateStatusBarTask;
    }
}
