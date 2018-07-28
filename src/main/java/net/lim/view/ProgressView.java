package net.lim.view;

import javafx.beans.property.StringProperty;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class ProgressView extends Pane {
    public ProgressIndicator progressIndicator;
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

    public StringProperty getTextMessageProperty() {
        return textMessage.textProperty();
    }
}
