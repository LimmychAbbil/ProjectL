package net.lim.view;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
import net.lim.controller.*;
import net.lim.controller.tasks.BackgroundReceiverTask;
import net.lim.unit.BaseFXUnitTestClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.net.URL;
import java.util.List;

public class BasicPaneTest extends BaseFXUnitTestClass {

    private BasicPane basicPane;
    private SettingsPane settingsPane;

    private LoginController loginControllerMock;
    private RegistrationController registrationControllerMock;
    private StageController stageControllerMock;
    private LauncherController launcherControllerMock;
    private SettingsController settingsControllerMock;

    @BeforeEach
    public void setUp() {
        settingsControllerMock = Mockito.mock();
        settingsPane = new SettingsPane(settingsControllerMock);
        Mockito.when(settingsControllerMock.getOrCreateSettingsPane()).thenReturn(settingsPane);
        launcherControllerMock = Mockito.mock();
        stageControllerMock = Mockito.mock();
        Mockito.when(launcherControllerMock.getOrCreateSettingController()).thenReturn(settingsControllerMock);

        BackgroundReceiverTask backgroundReceiverTaskMock = Mockito.mock();
        Mockito.when(launcherControllerMock.createAndStartBackgroundReceiverTask()).thenReturn(backgroundReceiverTaskMock);
        Mockito.when(stageControllerMock.getLauncherController()).thenReturn(launcherControllerMock);

        registrationControllerMock = Mockito.mock();
        loginControllerMock = Mockito.mock();
        Mockito.when(loginControllerMock.getOrCreateRegistrationController()).thenReturn(registrationControllerMock);
        Mockito.when(stageControllerMock.getLoginController()).thenReturn(loginControllerMock);
        basicPane = new BasicPane(stageControllerMock);
    }

    @Test
    public void testHeaderPaneAddedToBasicPane() {
        List<Node> allLabelOnHeaderPaneList = basicPane.getChildren()
                .filtered(node -> node.getClass().equals(HeaderPane.class));
        Assertions.assertEquals(1, allLabelOnHeaderPaneList.size());
    }

    @Test
    public void testNewsPaneAddedToBasicPane() {
        List<Node> allLabelOnHeaderPaneList = basicPane.getChildren()
                .filtered(node -> node.getClass().equals(NewsPane.class));
        Assertions.assertEquals(1, allLabelOnHeaderPaneList.size());
    }

    @Test
    public void testLoginPaneAddedToBasicPane() {
        List<Node> allLabelOnHeaderPaneList = basicPane.getChildren()
                .filtered(node -> node.getClass().equals(LoginPane.class));
        Assertions.assertEquals(1, allLabelOnHeaderPaneList.size());
    }

    @Test
    public void testRegistrationPaneAddedToBasicPane() {
        List<Node> allLabelOnHeaderPaneList = basicPane.getChildren()
                .filtered(node -> node.getClass().equals(RegistrationPane.class));
        Assertions.assertEquals(1, allLabelOnHeaderPaneList.size());
    }

    @Test
    public void testSettingsPaneAddedToBasicPane() {
        List<Node> allLabelOnHeaderPaneList = basicPane.getChildren()
                .filtered(node -> node.getClass().equals(SettingsPane.class));
        Assertions.assertEquals(1, allLabelOnHeaderPaneList.size());
    }

    @Test
    public void testProgressViewAddedToBasicPane() {
        List<Node> allLabelOnHeaderPaneList = basicPane.getChildren()
                .filtered(node -> node.getClass().equals(ProgressView.class));
        Assertions.assertEquals(1, allLabelOnHeaderPaneList.size());
    }

    @Test
    public void testBackgroundImageAddedToBasicPane() {
        List<Node> allImageViewOnHeaderPaneList = basicPane.getChildren()
                .filtered(node -> node.getClass().equals(ImageView.class));
        Assertions.assertEquals(1, allImageViewOnHeaderPaneList.size());
    }

    @Test
    public void testSetConnectionStatusOffline() {
        List<Node> allLabelOnHeaderPaneList = basicPane.getChildren()
                .filtered(node -> node.getClass().equals(ImageView.class));

        ImageView connectionStatusImageView = (ImageView) allLabelOnHeaderPaneList.get(0);

        basicPane.setConnectionStatus(false);

        Assertions.assertTrue(connectionStatusImageView.getImage().getUrl().contains("offline"));
    }

    @Test
    public void testSetConnectionStatusOnline() {
        try (MockedStatic<ConnectionController> connectionControllerMockedStatic
                     = Mockito.mockStatic(ConnectionController.class)) {
            ConnectionController mockedConnectionController = Mockito.mock();
            connectionControllerMockedStatic.when(() -> ConnectionController.getInstance())
                    .thenReturn(mockedConnectionController);
            List<Node> allImageViewPaneList = basicPane.getChildren()
                    .filtered(node -> node.getClass().equals(ImageView.class));

            ImageView connectionStatusImageView = (ImageView) allImageViewPaneList.get(0);

            basicPane.setConnectionStatus(true);

            Assertions.assertTrue(connectionStatusImageView.getImage().getUrl().contains("online"));
            Assertions.assertEquals("Connected to the server", connectionStatusImageView.getAccessibleText());

            Mockito.verify(loginControllerMock).retrieveServerList();
        }
    }

    @Test
    public void testAddBackgroundImageNullGetDefault() {
        basicPane.addBackgroundImage(null);

        URL defaultBackgroundURL = BasicPane.class.getClassLoader().getResource("background.jpg");
        Assertions.assertEquals(basicPane.getBackground().getImages().get(0).getImage().getUrl(),
                defaultBackgroundURL.toString());
    }

    @Test
    public void testGetProgressView() {
        ProgressView progressView = basicPane.getProgressView();

        List<Node> allLabelOnHeaderPaneList = basicPane.getChildren()
                .filtered(node -> node.getClass().equals(ProgressView.class));
        Assertions.assertEquals(progressView, allLabelOnHeaderPaneList.get(0));
    }
}