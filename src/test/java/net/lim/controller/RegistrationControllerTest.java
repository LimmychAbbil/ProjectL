package net.lim.controller;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import net.lim.model.connection.Connection;
import net.lim.unit.BaseFXUnitTestClass;
import net.lim.view.RegistrationPane;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

public class RegistrationControllerTest extends BaseFXUnitTestClass {

    private RegistrationController registrationController;
    private RegistrationPane registrationPane;
    private Label errorMessageMock;

    @BeforeEach
    public void setUp() {
        errorMessageMock = Mockito.mock();
        registrationPane = Mockito.mock();
        Mockito.when(registrationPane.getErrorMessage()).thenReturn(errorMessageMock);
        registrationController = new RegistrationController();
    }

    @Test
    public void testRegistrationNotSendWhenIncorrectUsername() {
        String expectedErrorMessage = "Incorrect username";

        TextField mockedTextField = Mockito.mock(TextField.class);
        Mockito.when(mockedTextField.getText()).thenReturn("!1Inc0RR/---CT");
        Mockito.when(registrationPane.getUserName()).thenReturn(mockedTextField);

        registrationController.sendRegistration(registrationPane);


        Mockito.verify(errorMessageMock).setText(expectedErrorMessage);
    }

    @Test
    public void testRegistrationNotSendWhenPasswordEmpty() {
        String expectedErrorMessage = "Empty password";

        TextField mockedTextField = Mockito.mock(TextField.class);
        PasswordField mockedPasswordField = Mockito.mock(PasswordField.class);
        Mockito.when(mockedTextField.getText()).thenReturn("Correct");
        Mockito.when(mockedPasswordField.getText()).thenReturn("");
        Mockito.when(registrationPane.getUserName()).thenReturn(mockedTextField);
        Mockito.when(registrationPane.getPassword()).thenReturn(mockedPasswordField);

        registrationController.sendRegistration(registrationPane);


        Mockito.verify(errorMessageMock).setText(expectedErrorMessage);
    }

    @Test
    public void testRegistrationNotSendWhenPasswordNotMatch() {
        String expectedErrorMessage = "Passwords do not match";

        TextField mockedTextField = Mockito.mock(TextField.class);
        PasswordField mockedPasswordField = Mockito.mock(PasswordField.class);
        PasswordField mockedPasswordConfirmField = Mockito.mock(PasswordField.class);
        Mockito.when(mockedTextField.getText()).thenReturn("Correct");
        Mockito.when(mockedPasswordField.getText()).thenReturn("Pass1");
        Mockito.when(mockedPasswordConfirmField.getText()).thenReturn("Pass2");
        Mockito.when(registrationPane.getUserName()).thenReturn(mockedTextField);
        Mockito.when(registrationPane.getPassword()).thenReturn(mockedPasswordField);
        Mockito.when(registrationPane.getPasswordConfirmation()).thenReturn(mockedPasswordConfirmField);

        registrationController.sendRegistration(registrationPane);


        Mockito.verify(errorMessageMock).setText(expectedErrorMessage);
    }

    @Test
    public void testRegistrationNotSendWhenRulesNotAccepted() {
        String expectedErrorMessage = "Need to accept the rules";

        TextField mockedTextField = Mockito.mock(TextField.class);
        PasswordField mockedPasswordField = Mockito.mock(PasswordField.class);
        PasswordField mockedPasswordConfirmField = Mockito.mock(PasswordField.class);
        CheckBox mockedCheckbox = Mockito.mock(CheckBox.class);
        Mockito.when(mockedCheckbox.isSelected()).thenReturn(false);
        Mockito.when(mockedTextField.getText()).thenReturn("Correct");
        Mockito.when(mockedPasswordField.getText()).thenReturn("Pass1");
        Mockito.when(mockedPasswordConfirmField.getText()).thenReturn("Pass1");
        Mockito.when(registrationPane.getUserName()).thenReturn(mockedTextField);
        Mockito.when(registrationPane.getPassword()).thenReturn(mockedPasswordField);
        Mockito.when(registrationPane.getPasswordConfirmation()).thenReturn(mockedPasswordConfirmField);
        Mockito.when(registrationPane.getRulesConfirmation()).thenReturn(mockedCheckbox);

        registrationController.sendRegistration(registrationPane);


        Mockito.verify(errorMessageMock).setText(expectedErrorMessage);
    }

    @Test
    public void testRegistrationOK() {
        TextField mockedTextField = Mockito.mock(TextField.class);
        PasswordField mockedPasswordField = Mockito.mock(PasswordField.class);
        PasswordField mockedPasswordConfirmField = Mockito.mock(PasswordField.class);
        CheckBox mockedCheckbox = Mockito.mock(CheckBox.class);
        Mockito.when(mockedCheckbox.isSelected()).thenReturn(true);
        Mockito.when(mockedTextField.getText()).thenReturn("Correct");
        Mockito.when(mockedPasswordField.getText()).thenReturn("Pass1");
        Mockito.when(mockedPasswordConfirmField.getText()).thenReturn("Pass1");
        Mockito.when(registrationPane.getUserName()).thenReturn(mockedTextField);
        Mockito.when(registrationPane.getPassword()).thenReturn(mockedPasswordField);
        Mockito.when(registrationPane.getPasswordConfirmation()).thenReturn(mockedPasswordConfirmField);
        Mockito.when(registrationPane.getRulesConfirmation()).thenReturn(mockedCheckbox);

        try (MockedStatic<ConnectionController> conMockedStatic = Mockito.mockStatic(ConnectionController.class)) {
            ConnectionController mockedConnectionController = Mockito.mock(ConnectionController.class);
            Connection mockedConnection = Mockito.mock(Connection.class);
            conMockedStatic.when(ConnectionController::getInstance).thenReturn(mockedConnectionController);
            Mockito.when(mockedConnectionController.getConnection()).thenReturn(mockedConnection);
            Mockito.when(mockedConnection.sendRegistration(Mockito.anyString(), Mockito.anyString()))
                    .thenReturn(200);


            registrationController.sendRegistration(registrationPane);

            Mockito.verify(errorMessageMock).setText("");
            Mockito.verify(registrationPane).setVisible(false);
        }
    }

    @Test
    public void testCancelRegistration() {
        registrationController.cancelRegistration(registrationPane);

        Mockito.verify(registrationPane).setVisible(false);
    }
}