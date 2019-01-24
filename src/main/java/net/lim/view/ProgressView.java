package net.lim.view;

import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class ProgressView extends VBox {
    public ProgressIndicator progressIndicator;
    private Text textMessage;

    public ProgressView() {
        init();
    }

    private void init() {
        initProgressBar();
        initTextMessage();
        addContent();
        this.setAlignment(Pos.CENTER);
        this.setSpacing(10);
    }

    private void addContent() {
        this.getChildren().addAll(progressIndicator, textMessage);
    }

    private void initProgressBar() {
        progressIndicator = new ProgressIndicator();
    }

    private void initTextMessage() {
        textMessage = new Text("Connection...");

        textMessage.setTextAlignment(TextAlignment.CENTER);
        textMessage.setFill(Color.WHITE);
    }

    public StringProperty getTextMessageProperty() {
        return textMessage.textProperty();
    }
}
