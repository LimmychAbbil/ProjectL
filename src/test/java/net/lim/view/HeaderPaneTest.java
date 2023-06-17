package net.lim.view;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import net.lim.LLauncher;
import net.lim.controller.LauncherController;
import net.lim.unit.BaseFXUnitTestClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

public class HeaderPaneTest extends BaseFXUnitTestClass {

    private HeaderPane pane;
    private LauncherController mockedController;
    private MouseEvent mouseClickedEvent;
    private MouseEvent mousePressedEvent;
    private MouseEvent mouseDragEvent;

    @BeforeEach
    public void setUp() {
        mockedController = Mockito.mock(LauncherController.class);
        pane = new HeaderPane(mockedController);

        //TODO create method to create fake mouse events by type
        mouseClickedEvent =
                new MouseEvent(MouseEvent.MOUSE_CLICKED, 0.0, 0.0,
                        0.0, 0.0, MouseButton.PRIMARY, 1, false, false,
                        false, false, false, false,
                        false, true, false, false, null);

        mousePressedEvent =
                new MouseEvent(MouseEvent.MOUSE_PRESSED, 0.0, 0.0,
                        0.0, 0.0, MouseButton.PRIMARY, 1, false, false,
                        false, false, false, false,
                        false, true, false, false, null);

        mouseDragEvent = new MouseEvent(MouseEvent.MOUSE_DRAGGED, 0.0, 0.0,
                0.0, 0.0, MouseButton.PRIMARY, 1, false, false,
                false, false, false, false,
                false, true, false, false, null);
    }

    @Test
    public void testVersionIsPresentAndCorrectOnHeaderPane() {
        List<Node> allLabelOnHeaderPaneList = pane.getChildren().filtered(node -> node.getClass().equals(Label.class));
        Assertions.assertEquals(1, allLabelOnHeaderPaneList.size());

        Label headerText = (Label) allLabelOnHeaderPaneList.get(0);
        Assertions.assertTrue(headerText.getText().contains(LLauncher.PROGRAM_NAME));
        Assertions.assertTrue(headerText.getText().contains(LLauncher.PROGRAM_VERSION));
    }

    @Test
    public void testHeaderPaneContains3ControlImages() {
        List<Node> allImagesOnHeaderPaneList = pane.getChildren().filtered(node ->
                node.getClass().equals(ImageView.class));

        Assertions.assertEquals(3, allImagesOnHeaderPaneList.size());
    }

    @Test
    public void testExitButtonCloseApplication() {
        List<Node> exitImageOnHeaderPane = pane.getChildren().filtered(node ->
                node.getClass().equals(ImageView.class) && ((ImageView) node).getImage().getUrl().contains("exit.png"));

        Assertions.assertEquals(1, exitImageOnHeaderPane.size());
        ImageView exitImage = (ImageView) exitImageOnHeaderPane.get(0);

        exitImage.fireEvent(mouseClickedEvent);

        Mockito.verify(mockedController).closeButtonPressed();
    }

    @Test
    public void testMinimizeButtonCloseApplication() {
        List<Node> exitImageOnHeaderPane = pane.getChildren().filtered(node ->
                node.getClass().equals(ImageView.class)
                        && ((ImageView) node).getImage().getUrl().contains("minimize.png"));

        Assertions.assertEquals(1, exitImageOnHeaderPane.size());
        ImageView exitImage = (ImageView) exitImageOnHeaderPane.get(0);

        exitImage.fireEvent(mouseClickedEvent);

        Mockito.verify(mockedController).minimizedPressed();
    }

    @Test
    public void testMaximizeButtonCloseApplication() {
        List<Node> exitImageOnHeaderPane = pane.getChildren().filtered(node ->
                node.getClass().equals(ImageView.class)
                        && ((ImageView) node).getImage().getUrl().contains("maximize.png"));

        Assertions.assertEquals(1, exitImageOnHeaderPane.size());
        ImageView exitImage = (ImageView) exitImageOnHeaderPane.get(0);

        exitImage.fireEvent(mouseClickedEvent);

        Mockito.verify(mockedController).maximizePressed();
    }

    @Test
    public void testHeaderDragAndMove() {
        pane.fireEvent(mousePressedEvent);
        Mockito.verify(mockedController).handleMousePress(Mockito.any(MouseEvent.class));

        pane.fireEvent(mouseDragEvent);
        Mockito.verify(mockedController).handleMouseDragged(Mockito.any(MouseEvent.class));
    }
}
