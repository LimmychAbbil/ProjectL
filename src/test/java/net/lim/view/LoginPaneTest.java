package net.lim.view;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import net.lim.controller.LauncherController;
import net.lim.model.ServerInfo;
import net.lim.unit.BaseFXUnitTestClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.stream.Collectors;

public class LoginPaneTest extends BaseFXUnitTestClass {

    private LoginPane loginPane;
    private LauncherController launcherController;

    @BeforeEach
    public void setUp() {
        launcherController = Mockito.mock(LauncherController.class);

        loginPane = new LoginPane(launcherController ,null);
    }

    @Test
    public void testLoginPaneHasTwoButtons() {
        List<Node> paneList = loginPane.getChildren().filtered(node -> node.getClass().equals(Button.class));
        Assertions.assertEquals(2, paneList.size());
    }

    @Test
    public void testLoginPaneHasUsernameField() {
        List<Node> paneList = loginPane.getChildren().filtered(node -> node.getClass().equals(TextField.class));
        Assertions.assertEquals(1, paneList.size());
    }

    @Test
    public void testLoginPaneHasPasswordField() {
        List<Node> paneList = loginPane.getChildren().filtered(node -> node.getClass().equals(PasswordField.class));
        Assertions.assertEquals(1, paneList.size());
    }

    @Test
    public void testLoginPaneHasServerDropdown() {
        List<Node> paneList = loginPane.getChildren().filtered(node -> node.getClass().equals(ChoiceBox.class));
        Assertions.assertEquals(1, paneList.size());
    }

    @Test
    public void testLoginButtonPressedInitLoginProcess() {
        List<Button> paneList = loginPane.getChildren().stream().filter(node -> node.getClass().equals(Button.class))
                .map(node -> (Button) node).filter(button -> button.getText().equals("Log in")).collect(Collectors.toList());
        Assertions.assertEquals(1, paneList.size());

        paneList.get(0).fire();
        Mockito.verify(launcherController).loginButtonPressed(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void testRegisterButtonPressedInitRegisterProcess() {
        List<Button> paneList = loginPane.getChildren().stream().filter(node -> node.getClass().equals(Button.class))
                .map(node -> (Button) node).filter(button -> button.getText().equals("Sign up")).collect(Collectors.toList());
        Assertions.assertEquals(1, paneList.size());

        paneList.get(0).fire();
        Mockito.verify(launcherController).registrationButtonPressed(Mockito.any());
    }

    @Test
    public void testServerSelectorContainsOfflineMode() {
        List<ChoiceBox> paneList = loginPane.getChildren().stream()
                .filter(node -> node.getClass().equals(ChoiceBox.class)).map(node -> (ChoiceBox) node)
                .collect(Collectors.toList());

        ObservableList options = paneList.get(0).getItems();
        Assertions.assertEquals(1, options.size());

        Assertions.assertEquals(ServerInfo.OFFLINE, options.get(0));
        Assertions.assertEquals(ServerInfo.OFFLINE, paneList.get(0).getValue());
    }

    @Test
    public void testServerSelectorAfterFillUp() {
        ServerInfo someServerInfo = new ServerInfo("someServer", "someIp", 1111);
        List<ServerInfo> serverInfoList = List.of(someServerInfo);
        Mockito.when(launcherController.retrieveServerList()).thenReturn(serverInfoList);

        loginPane.updateServersList();

        List<ChoiceBox> paneList = loginPane.getChildren().stream()
                .filter(node -> node.getClass().equals(ChoiceBox.class)).map(node -> (ChoiceBox) node)
                .collect(Collectors.toList());

        ObservableList options = paneList.get(0).getItems();
        Assertions.assertEquals(2, options.stream()
                .filter(control -> !control.getClass().equals(Separator.class)).count());

        Assertions.assertTrue(options.contains(ServerInfo.OFFLINE));
        Assertions.assertTrue(options.contains(someServerInfo));
    }
}
