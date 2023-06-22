package net.lim.controller;

import net.lim.model.connection.Connection;
import net.lim.model.service.LUtils;
import net.lim.view.RegistrationPane;

import javax.net.ssl.HttpsURLConnection;

public class RegistrationController implements Controller {
    private RegistrationPane registrationPane;

    @Override
    public void init() {

    }

    public void sendRegistration(RegistrationPane registrationPane) {
        if (!LUtils.isNotValidUserName(registrationPane.getUserName().getText())) {
            registrationPane.getErrorMessage().setText("Incorrect username");
            return;
        }
        if (registrationPane.getPassword().getText().isEmpty()) {
            registrationPane.getErrorMessage().setText("Empty password");
            return;
        }
        if (!registrationPane.getPassword().getText().equals(registrationPane.getPasswordConfirmation().getText())) {
            registrationPane.getErrorMessage().setText("Passwords do not match");
            return;
        }

        if (!registrationPane.getRulesConfirmation().isSelected()) {
            registrationPane.getErrorMessage().setText("Need to accept the rules");
            return;
        }
        try {
            int responseCode = ConnectionController.getInstance().getConnection().sendRegistration(
                    registrationPane.getUserName().getText(), registrationPane.getPassword().getText());
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                registrationPane.getErrorMessage().setText("");
                registrationPane.setVisible(false);
            } else {
                registrationPane.getErrorMessage().setText(Connection.getErrorMessage(responseCode));
            }
        } catch (Exception e) {
            registrationPane.getErrorMessage().setText("Can't sign up: " + e.getMessage());
        }

    }

    public void cancelRegistration(RegistrationPane registrationPane) {
        registrationPane.setVisible(false);
    }

    public void rulesClicked() {
        System.out.println("Open rules"); //TODO
    }
}
