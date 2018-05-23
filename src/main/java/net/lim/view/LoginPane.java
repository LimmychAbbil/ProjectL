package net.lim.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import net.lim.controller.LauncherController;

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
        addContent();
        setContentMargin();
    }

    private void initRegistrationButton() {
        registrationButton = new Button("Регистрация");
        registrationButton.setOnAction(e -> controller.registrationButtonPressed(registrationPane));
    }

    private void setContentMargin() {
        setMargin(userNameField, new Insets(16, 8, 16, 16));
        setMargin(passwordField, new Insets(16, 8, 16, 16));
        setMargin(loginButton, new Insets(16, 8, 16, 0));
        setMargin(registrationButton, new Insets(16, 0, 16, 8));
    }

    private void addContent() {
        getChildren().addAll(userNameField, passwordField, loginButton, registrationButton);

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
