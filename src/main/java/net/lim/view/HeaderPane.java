package net.lim.view;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import net.lim.LLauncher;
import net.lim.controller.LauncherController;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by Limmy on 13.05.2018.
 */
public class HeaderPane extends Pane {
    private LauncherController controller;
    private Label headerText;
    private ImageView minimizeButton, maximizeButton, exitButton;
    public HeaderPane(LauncherController controller) throws IOException{
        this.controller = controller;
        init();
    }

    private void init() throws IOException {
        setHeaderColorAndSize();
        setDragAction();
        initHeaderText();
        initCloseButton();
        initMaximizeButton();
        initMinimizeButton();
        addElements();
    }

    private void setDragAction() {
        this.setOnMousePressed(e -> controller.handleMousePress(e));
        this.setOnMouseDragged(e -> controller.handleMouseDragged(e));
    }

    private void addElements() {
        getChildren().addAll(headerText, minimizeButton, maximizeButton, exitButton);
    }

    private void initCloseButton() throws IOException {
        Image exitImage = new Image(new FileInputStream("./src/main/resources/exit.png"));
        exitButton = new ImageView(exitImage);
        exitButton.layoutYProperty().setValue(8);
        exitButton.layoutXProperty().bind(this.widthProperty().add(-24));
        exitButton.setOnMouseClicked(event -> controller.closeButtonPressed());
    }

    private void initMaximizeButton() throws IOException {
        Image exitImage = new Image(new FileInputStream("./src/main/resources/maximize.png"));
        maximizeButton = new ImageView(exitImage);
        maximizeButton.layoutYProperty().set(8);
        maximizeButton.layoutXProperty().bind(exitButton.layoutXProperty().add(-24));

        maximizeButton.setOnMouseClicked(event -> controller.maximizePressed());
    }

    private void initMinimizeButton() throws IOException {
        Image exitImage = new Image(new FileInputStream("./src/main/resources/minimize.png"));
        minimizeButton = new ImageView(exitImage);
        minimizeButton.layoutYProperty().set(8);
        minimizeButton.layoutXProperty().bind(maximizeButton.layoutXProperty().add(-24));

        minimizeButton.setOnMouseClicked(event -> controller.minimizedPressed());
    }

    private void initHeaderText() {
        headerText = new Label(LLauncher.PROGRAM_NAME + " " + LLauncher.PROGRAM_VERSION);
        headerText.setStyle("-fx-text-fill: black; -fx-font-size: 16");
        headerText.layoutXProperty().set(8.0);
        headerText.layoutYProperty().setValue(8.0);
    }

    private void setHeaderColorAndSize() {
        this.setOpacity(0.4);
        this.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        this.setMinHeight(32);
        this.setPrefHeight(32);
    }
}
