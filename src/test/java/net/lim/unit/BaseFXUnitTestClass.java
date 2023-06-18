package net.lim.unit;

import javafx.application.Platform;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.junit.jupiter.api.BeforeAll;

public abstract class BaseFXUnitTestClass {


    protected static MouseEvent mouseClickedEvent;
    protected static MouseEvent mousePressedEvent;
    protected static MouseEvent mouseDragEvent;

    private static boolean platformStarted;

    @BeforeAll
    static void initJfxRuntime() {
        if (!platformStarted) {
            Platform.startup(() -> {});
            platformStarted = true;
        }
        initBasicMouseEvents();
    }

    private static void initBasicMouseEvents() {
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
}
