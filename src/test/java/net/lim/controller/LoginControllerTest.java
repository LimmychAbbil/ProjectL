package net.lim.controller;

import net.lim.model.ServerInfo;
import net.lim.unit.BaseFXUnitTestClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class LoginControllerTest extends BaseFXUnitTestClass {
    private LoginController loginController;
    private StageController stageControllerMock;

    @BeforeEach
    public void setUp() {
        stageControllerMock = Mockito.mock();
        loginController = new LoginController(stageControllerMock);
    }

    @Test
    public void testSetSelectedServerCorrect() {
        ServerInfo serverInfo = new ServerInfo("someName", "someDesc", "SomeIp", 1111);
        loginController.serverSelected(serverInfo);

        Assertions.assertEquals(serverInfo, loginController.getSelectedServer());
    }

    @Test
    public void testSetSelectedServerIncorrectObject() {
        loginController.serverSelected(new Object());

        Assertions.assertNull(loginController.getSelectedServer());
    }

}