package net.lim.view;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import net.lim.controller.LauncherController;
import net.lim.model.Settings;

public class SettingsPane extends GridPane {
    private LauncherController controller;
    private Slider xmxSlider;
    private CheckBox offlineModeCheckBox;
    private CheckBox useCustomDirectory;
    private TextField directoryPath;
    private TextField serverURL;
    private Button reconnect;

    public SettingsPane(LauncherController controller) {
        this.controller = controller;
        init();
    }

    private void init() {
        initStyle();
        initSlider();
        initOfflineModeCheckbox();
        initUseCustomDir();
        initServerURL();
        initReconnectButton();
        addContent();
    }

    private void initReconnectButton() {
        this.reconnect = new Button("Reconnect");
        this.reconnect.setOnMouseClicked(e -> controller.connectionIconPressed());
    }

    private void initServerURL() {
        this.serverURL = new TextField();
        this.serverURL.setPromptText("LServer URL");
        this.serverURL.focusedProperty().addListener(e -> {
            if (!serverURL.isFocused()) {
                Settings.getInstance().setLserverURL(serverURL.textProperty().get());
            }
        });
    }

    private void initUseCustomDir() {
        this.useCustomDirectory = new CheckBox("Use custom dir");
        this.directoryPath = new TextField();
        this.directoryPath.visibleProperty().bind(useCustomDirectory.selectedProperty());
    }

    private void initOfflineModeCheckbox() {
        this.offlineModeCheckBox = new CheckBox("Use offline mode");
    }

    private void initSlider() {
        this.xmxSlider = new Slider(10, 20, 15);
        xmxSlider.setBlockIncrement(1);
    }

    private void addContent() {
        this.addRow(0);
        this.add(xmxSlider, 0, 0, 3, 1);
        this.addRow(1);
        this.add(offlineModeCheckBox, 0, 1);
        this.addRow(2);
        this.add(useCustomDirectory, 0, 2, 1, 1);
        this.add(directoryPath, 1, 2, 1, 1);
        this.addRow(3);
        this.add(serverURL, 0, 3, 2, 1);
        this.add(reconnect, 2, 3, 1, 1);
    }

    private void initStyle() {
        this.setHgap(10);
        this.setVgap(15);
        this.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

        this.setOpacity(0.85);
        this.setPadding(new Insets(20, 5, 20, 10));
        this.setStyle("-fx-start-margin: 20;");
    }
}
