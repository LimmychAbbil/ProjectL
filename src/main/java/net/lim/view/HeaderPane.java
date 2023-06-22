package net.lim.view;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import net.lim.LLauncher;
import net.lim.controller.Controller;
import net.lim.controller.LauncherController;
import net.lim.controller.StageController;

import java.io.IOException;

/**
 * Created by Limmy on 13.05.2018.
 */
public class HeaderPane extends Pane {
    private final StageController controller;
    private Label headerText;
    private ImageView minimizeButton, maximizeButton, exitButton;

    public HeaderPane(StageController controller) {
        this.controller = controller;
        init();
    }

    private void init() {
        setHeaderColorAndSize();
        setDragAction();
        initHeaderText();
        initCloseButton();
        initMaximizeButton();
        initMinimizeButton();
        addElements();
    }

    private void setDragAction() {
        this.setOnMousePressed(controller::handleMousePress);
        this.setOnMouseDragged(controller::handleMouseDragged);
    }

    private void addElements() {
        getChildren().addAll(headerText, minimizeButton, maximizeButton, exitButton);
    }

    private void initCloseButton() {
        Image exitImage = new Image(this.getClass().getClassLoader().getResource("exit.png").toString());
        exitButton = initControlButton(exitImage, this.widthProperty());

        exitButton.setOnMouseClicked(event -> controller.closeButtonPressed());
    }

    private void initMaximizeButton() {
        Image maximizeImage = new Image(this.getClass().getClassLoader().getResource("maximize.png").toString());
        maximizeButton = initControlButton(maximizeImage, exitButton.layoutXProperty());

        maximizeButton.setOnMouseClicked(event -> controller.maximizePressed());
    }

    private void initMinimizeButton() {
        Image minimizeImage = new Image(this.getClass().getClassLoader().getResource("minimize.png").toString());
        minimizeButton = initControlButton(minimizeImage, maximizeButton.layoutXProperty());

        minimizeButton.setOnMouseClicked(event -> controller.minimizedPressed());
    }


    private ImageView initControlButton(Image image, ReadOnlyDoubleProperty anchor) {
        ImageView imageButton = new ImageView(image);
        imageButton.layoutYProperty().set(8);
        imageButton.setPickOnBounds(true);
        imageButton.layoutXProperty().bind(anchor.add(-24));

        return imageButton;
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
