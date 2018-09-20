package net.lim.view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import net.lim.controller.LauncherController;
import net.lim.model.ServerInfo;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Limmy on 13.05.2018.
 */
public class LoginPane extends HBox {
    private final LauncherController controller;
    private TextField userNameField;
    private PasswordField passwordField;
    private Button loginButton;
    private Button registrationButton;
    private RegistrationPane registrationPane;
    private ChoiceBox serverListDropdown;

    public LoginPane(LauncherController controller, RegistrationPane registrationPane) {
        this.controller = controller;
        this.registrationPane = registrationPane;
        init();
    }

    private void init() {
        initUserNameField();
        initPasswordField();
        initLoginButton();
        initRegistrationButton();
        initComboBox(controller.retrieveServerList());
        addContent();
        setContentMargin();
    }

    private void initComboBox(List<ServerInfo> serverList) {
        serverListDropdown = new ChoiceBox<>();
        serverListDropdown.setOnAction(e -> controller.serverSelected(serverListDropdown.getValue()));
        serverListDropdown.getItems().add("Offline");
        serverListDropdown.setValue("Offline");
        if (serverList.size() > 0) {
            serverListDropdown.getItems().add(new Separator());
            serverListDropdown.getItems().addAll(serverList);
        }
    }

    private void initRegistrationButton() {
        registrationButton = new Button("Регистрация");
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
        loginButton = new Button("Войти на сервер");
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
}
