package net.lim.view;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class ProgressView extends Pane {
    private ProgressIndicator progressIndicator;
    private Text textMessage;

    public ProgressView() {
        init();
    }

    private void init() {
        initProgressBar();
        initTextMessage();
        addContent();
    }

    private void addContent() {
        this.getChildren().addAll(progressIndicator, textMessage);
    }

    private void initProgressBar() {
        progressIndicator = new ProgressIndicator();
        progressIndicator.layoutXProperty().bind(this.widthProperty().divide(2));
        progressIndicator.layoutYProperty().bind(this.heightProperty().divide(2));

    }

    private void initTextMessage() {
        textMessage = new Text("Connection...");
        textMessage.xProperty().bind(progressIndicator.layoutXProperty().add(-20)); //TODO center-allignment
        textMessage.yProperty().bind(progressIndicator.layoutYProperty().add(progressIndicator.heightProperty()).add(20));
        textMessage.setTextAlignment(TextAlignment.CENTER);
        textMessage.setFill(Color.WHITE);
    }

    public void loginFailed(String message) {
        Task<Void> showLoginFailedMessage = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                ProgressView.super.setVisible(true);
                textMessage.textProperty().setValue("Login failed: " + message);
                Thread.sleep(5000);
                return null;
            }
        };

        showLoginFailedMessage.setOnSucceeded(e -> ProgressView.super.setVisible(false));
        new Thread(showLoginFailedMessage).start();
    }

    public void start() {
        this.setVisible(true);
    }

    public void success() {
        Task<Void> sleep = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                textMessage.textProperty().setValue("Success, starting application");
                Thread.sleep(5000);
                return null;
            }
        };
        sleep.setOnSucceeded(event -> ProgressView.super.setVisible(false));
        new Thread(sleep).start();
    }

    public void startFilesCheck() {
        textMessage.textProperty().setValue("Checking files...");
    }

    public void filesCheckFailed() {
        textMessage.textProperty().setValue("Redownloading files...");
    }
}
