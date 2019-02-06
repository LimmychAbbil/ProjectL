package net.lim.view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import net.lim.controller.LauncherController;

/**
 * Created by Limmy on 22.05.2018.
 */
public class RegistrationPane extends GridPane {
    private LauncherController controller;
    private TextField userName;
    private PasswordField password;
    private PasswordField passwordConfirmation;
    private Hyperlink rules;
    private CheckBox rulesConfirmation;
    private Label errorMessage;
    private Button sendButton;
    private Button cancelButton;
    public RegistrationPane(LauncherController controller) {
        this.controller = controller;
        init();
    }

    private void init() {
        initStyle();
        initTextFields();
        addContent();
    }

    private void initStyle() {
        this.setHgap(5);
        this.setVgap(5);
        this.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));

        this.setOpacity(0.95);
        this.setPadding(new Insets(20, 5, 20, 10));
        this.setStyle("-fx-start-margin: 20;");
    }

    private void initTextFields() {
        userName = new TextField();
        userName.setPromptText("User Name");
        password = new PasswordField();
        password.setPromptText("Your Password");
        passwordConfirmation = new PasswordField();
        passwordConfirmation.setPromptText("Confirm password");
        rules = new Hyperlink("Я прочитал правила");
        rules.setOnMouseClicked(e -> controller.rulesClicked());
        rulesConfirmation = new CheckBox();
        rulesConfirmation.setAccessibleText("Ссылка на правила");

        errorMessage = new Label();
        errorMessage.setStyle("-fx-text-fill: red; -fx-font-size: 12");


        sendButton = new Button("Зарегистрироваться");
        sendButton.setOnMouseClicked(e -> controller.sendRegistration(this));

        cancelButton = new Button("Отмена");
        cancelButton.setOnMouseClicked(e -> controller.cancelRegistration(this));
    }

    private void addContent() {
        //0-2 rows content shouldn't have default width
        this.addRow(0);
        this.add(userName, 0, 0, 2, 1);
        this.addRow(1);
        this.add(password, 0, 1, 2, 1);
        this.addRow(2);
        this.add(passwordConfirmation, 0, 2, 2, 1);
        this.addRow(3, rules, rulesConfirmation);
        this.addRow(4);
        this.add(errorMessage, 0, 4, 2, 1);
        this.addRow(5, sendButton, cancelButton);
    }

    public LauncherController getController() {
        return controller;
    }

    public TextField getUserName() {
        return userName;
    }

    public PasswordField getPassword() {
        return password;
    }

    public PasswordField getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public Hyperlink getRules() {
        return rules;
    }

    public CheckBox getRulesConfirmation() {
        return rulesConfirmation;
    }

    public Button getSendButton() {
        return sendButton;
    }

    public Label getErrorMessage() {
        return errorMessage;
    }
}
