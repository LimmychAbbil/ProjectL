package net.lim.view;

import javafx.scene.Node;
import javafx.scene.control.*;
import net.lim.controller.RegistrationController;
import net.lim.unit.BaseFXUnitTestClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.stream.Collectors;

public class RegistrationPaneTest extends BaseFXUnitTestClass {
    private RegistrationPane registrationPane;
    private RegistrationController registrationController;

    @BeforeEach
    public void setUp() {
        registrationController = Mockito.mock(RegistrationController.class);

        registrationPane = new RegistrationPane(registrationController);
    }

    @Test
    public void testRegistrationPaneHasTwoButtons() {
        List<Node> paneList = registrationPane.getChildren().filtered(node -> node.getClass().equals(Button.class));
        Assertions.assertEquals(2, paneList.size());
    }

    @Test
    public void testRegistrationPaneHasOneTextField() {
        List<Node> paneList = registrationPane.getChildren().filtered(node -> node.getClass().equals(TextField.class));
        Assertions.assertEquals(1, paneList.size());
    }

    @Test
    public void testRegistrationPaneHasTwoPasswordFields() {
        List<Node> paneList = registrationPane.getChildren()
                .filtered(node -> node.getClass().equals(PasswordField.class));
        Assertions.assertEquals(2, paneList.size());
    }

    @Test
    public void testRegistrationPaneHasOneRulesCheckbox() {
        List<Node> paneList = registrationPane.getChildren().filtered(node -> node.getClass().equals(CheckBox.class));
        Assertions.assertEquals(1, paneList.size());
    }

    @Test
    public void testRegistrationPaneHasOneErrorMessageLabel() {
        List<Node> paneList = registrationPane.getChildren().filtered(node -> node.getClass().equals(Label.class));
        Assertions.assertEquals(1, paneList.size());
    }

    @Test
    public void testRegistrationSend() {
        registrationPane.getSendButton().fireEvent(BaseFXUnitTestClass.mouseClickedEvent);

        Mockito.verify(registrationController).sendRegistration(registrationPane);
    }

    @Test
    public void testRegistrationCanceled() {
        List<Button> paneList = registrationPane.getChildren().stream().filter(node -> node.getClass().equals(Button.class))
                .map(node -> (Button) node).filter(button -> button.getText().equals("Cancel")).collect(Collectors.toList());
        Assertions.assertEquals(1, paneList.size());

        paneList.get(0).fireEvent(mouseClickedEvent);

        Mockito.verify(registrationController).cancelRegistration(registrationPane);
    }
}
