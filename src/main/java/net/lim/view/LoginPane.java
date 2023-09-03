package net.lim.view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import net.lim.controller.LoginController;
import net.lim.model.ServerInfo;

import java.util.List;

/**
 * Created by Limmy on 13.05.2018.
 */
public class LoginPane extends HBox {
    private final LoginController controller;
    private TextField userNameField;
    private PasswordField passwordField;
    private Button loginButton;
    private Button registrationButton;
    private final RegistrationPane registrationPane;
    private ChoiceBox<Control> serverListDropdown;

    public LoginPane(LoginController controller, RegistrationPane registrationPane) {
        this.controller = controller;
        this.registrationPane = registrationPane;
        init();
    }

    private void init() {
        initUserNameField();
        initPasswordField();
        initLoginButton();
        initRegistrationButton();
        initComboBox();
        addContent();
        setContentMargin();
    }

    private void initComboBox() {
        serverListDropdown = new ChoiceBox<>();
        serverListDropdown.maxHeightProperty().setValue(16);
        serverListDropdown.setOnAction(e -> controller.serverSelected(serverListDropdown.getValue()));
        serverListDropdown.getItems().add(ServerInfo.OFFLINE);
        serverListDropdown.setValue(ServerInfo.OFFLINE);
    }

    private void initRegistrationButton() {
        registrationButton = new Button("Sign up");
        registrationButton.setOnAction(e -> controller.registrationButtonPressed(registrationPane));
    }

    private void setContentMargin() {
        setMargin(userNameField, new Insets(16, 8, 16, 16));
        setMargin(passwordField, new Insets(16, 8, 16, 16));
        setMargin(loginButton, new Insets(16, 8, 16, 16));
        setMargin(registrationButton, new Insets(16, 8, 16, 0));
        setMargin(serverListDropdown, new Insets(16, 0, 16, 8));
    }

    private void addContent() {
        getChildren().addAll(userNameField, passwordField, loginButton, registrationButton, serverListDropdown);

    }

    private void initLoginButton() {
        loginButton = new Button("Log in");
        loginButton.setOnAction(e -> controller.loginButtonPressed(userNameField.getText(), passwordField.getText()));
    }

    private void initPasswordField() {
        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
    }

    private void initUserNameField() {
        userNameField = new TextField();

        userNameField.setPromptText("User Name");
    }

    public void updateServersList() {
        fillServersList(controller.retrieveServerList());
    }

    private void fillServersList(List<ServerInfo> serverList) {
        if (serverList.size() > 0) {
            serverListDropdown.getItems().clear();
            serverListDropdown.getItems().addAll(0, serverList);
            serverListDropdown.getItems().add(serverList.size(), new Separator());
            serverListDropdown.getItems().add(ServerInfo.OFFLINE);
        }

        serverListDropdown.setValue(ServerInfo.OFFLINE);
    }
}
