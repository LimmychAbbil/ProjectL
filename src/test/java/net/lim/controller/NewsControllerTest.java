package net.lim.controller;

import javafx.application.HostServices;
import javafx.scene.control.ScrollPane;
import net.lim.LLauncher;
import net.lim.model.adv.Advertisement;
import net.lim.model.adv.RestAdvertisementReceiver;
import net.lim.model.connection.Connection;
import net.lim.unit.BaseFXUnitTestClass;
import net.lim.view.NewsPane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.net.URL;
import java.util.List;

public class NewsControllerTest extends BaseFXUnitTestClass {
    private NewsController controller;

    @BeforeEach
    public void setUp() {
        controller = new NewsController();
    }

    @Test
    void testHideNewsButtonPressed() {
        ScrollPane newsPaneMock = Mockito.mock();
        Mockito.when(newsPaneMock.isVisible()).thenReturn(true,false);

        controller.hideNewsButtonPressed(newsPaneMock);

        Mockito.verify(newsPaneMock).setVisible(false);

        controller.hideNewsButtonPressed(newsPaneMock);
        Mockito.verify(newsPaneMock).setVisible(true);
    }

    @Test
    void testFillNewsFlow() {
        List<Advertisement> fakeAdvs = List.of(new Advertisement("1"), new Advertisement("2", "b"));
        try (MockedStatic<ConnectionController> controllerMockedStatic = Mockito.mockStatic(ConnectionController.class);
             MockedConstruction<RestAdvertisementReceiver> ignored =
                     Mockito.mockConstruction(RestAdvertisementReceiver.class, (mock, context) -> {
                       Mockito.when(mock.receiveAdvertisements()).thenReturn(fakeAdvs);
                     })) {
            ConnectionController mockedController = Mockito.mock();
            Connection mockedConnection = Mockito.mock();
            Mockito.when(mockedController.getConnection()).thenReturn(mockedConnection);
            controllerMockedStatic.when(ConnectionController::getInstance).thenReturn(mockedController);
            NewsPane newsPaneMock = Mockito.mock();

            controller.fillNewsFlow(newsPaneMock);

            Mockito.verify(newsPaneMock).putNewToArea(fakeAdvs.get(0));
            Mockito.verify(newsPaneMock).putNewToArea(fakeAdvs.get(1));
        }
    }

    @Test
    void testLinkPressed() {
        try (MockedStatic<LLauncher> lLauncherMockedStatic = Mockito.mockStatic(LLauncher.class)) {
            HostServices mockedServices = Mockito.mock();
            lLauncherMockedStatic.when(LLauncher::getFXHostServices).thenReturn(mockedServices);

            URL mockedURL = Mockito.mock();
            Mockito.when(mockedURL.toString()).thenReturn("someURL");
            controller.linkPressed(mockedURL);

            Mockito.verify(mockedServices).showDocument("someURL");
        }
    }
}