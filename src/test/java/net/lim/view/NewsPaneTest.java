package net.lim.view;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextFlow;
import net.lim.controller.NewsController;
import net.lim.model.adv.Advertisement;
import net.lim.unit.BaseFXUnitTestClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class NewsPaneTest extends BaseFXUnitTestClass {
    private NewsPane pane;
    private NewsController mockedController;

    @BeforeEach
    public void setUp() {
        mockedController = Mockito.mock();
        pane = new NewsPane(mockedController);
    }



    @Test
    public void testScrollPane() {
        List<Node> paneList = pane.getChildren().filtered(node -> node.getClass().equals(ScrollPane.class));
        Assertions.assertEquals(1, paneList.size());

        Node flow = ((ScrollPane) paneList.get(0)).getContent();
        Assertions.assertInstanceOf(TextFlow.class, flow);
    }

    @Test
    public void testRectangleWorksAsAShowHideButton() {
        List<Node> paneList = pane.getChildren().filtered(node -> node.getClass().equals(Rectangle.class));
        Assertions.assertEquals(1, paneList.size());

        Rectangle rectangle = (Rectangle) paneList.get(0);

        rectangle.fireEvent(mouseClickedEvent);

        Mockito.verify(mockedController).hideNewsButtonPressed(Mockito.any());
    }

    @Test
    public void testInitFillFlow() {
        pane.postInit();

        Mockito.verify(mockedController).fillNewsFlow(pane);
    }

    @Test
    public void testAddNewAdv() throws MalformedURLException {
        Advertisement testAdv = new Advertisement("someHeader",
                "someText", new URL("https://google.com"));

        pane.putNewToArea(testAdv);

        List<Node> paneList = pane.getChildren().filtered(node -> node.getClass().equals(ScrollPane.class));
        Assertions.assertEquals(1, paneList.size());

        TextFlow flow = (TextFlow) ((ScrollPane) paneList.get(0)).getContent();

        Assertions.assertEquals(3, flow.getChildren().size());

        pane.clearTextFlow();

        Assertions.assertEquals(0, flow.getChildren().size());
    }
}