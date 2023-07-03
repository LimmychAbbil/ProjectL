package net.lim.view;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import net.lim.controller.ConnectionController;
import net.lim.controller.SettingsController;
import net.lim.model.Settings;
import net.lim.unit.BaseFXUnitTestClass;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class SettingsPaneTest extends BaseFXUnitTestClass {

    private SettingsPane settingsPane;
    private SettingsController mockedController;

    @BeforeEach
    public void setUp() {
        mockedController = Mockito.mock();
        settingsPane = new SettingsPane(mockedController);
    }

    @Test
    public void settingsPaneHasRAMSlider() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<Node> sliderList = settingsPane.getChildren().filtered(node -> node instanceof Slider);
        Assertions.assertEquals(1, sliderList.size());

        Slider slider = (Slider) sliderList.get(0);
        Assumptions.assumeTrue(Settings.MAX_RAM_MB_SIZE > Settings.DEFAULT_XMS_MB_SIZE);

        Assertions.assertEquals(Settings.DEFAULT_XMS_MB_SIZE, slider.getValue());
        Assertions.assertEquals(1024, slider.getMin());
        Assertions.assertEquals(Settings.MAX_RAM_MB_SIZE, slider.getMax());

        Method m = Node.class.getDeclaredMethod("setFocused", boolean.class);
        m.setAccessible(true);
        m.invoke(slider, true);

        double newXmxValue = 2 * slider.getValue();
        slider.setValue(newXmxValue);

        m.invoke(slider, false);

        Assertions.assertEquals((long) newXmxValue, Settings.getInstance().getXmx());
    }

    @Test
    public void testOfflineModeCheckbox() {
        try (MockedStatic<Settings> mockedStaticSettings = Mockito.mockStatic(Settings.class);
             MockedStatic<ConnectionController> mockedStaticConnectionController
                     = Mockito.mockStatic(ConnectionController.class)) {
            Settings mockedSettings = Mockito.mock();
            mockedStaticSettings.when(Settings::getInstance).thenReturn(mockedSettings);

            ConnectionController mockedConnectionController = Mockito.mock();
            mockedStaticConnectionController.when(ConnectionController::getInstance).thenReturn(mockedConnectionController);
            List<Node> checkboxList = settingsPane.getChildren().filtered(node -> node instanceof CheckBox);

            CheckBox offlineModeCheckbox = checkboxList.stream()
                    .filter(node -> node instanceof CheckBox).map(node -> (CheckBox) node)
                    .filter(checkBox -> "Use offline mode".equals(checkBox.getText()))
                    .findFirst().orElseGet(Assertions::fail);

            offlineModeCheckbox.fireEvent(mouseClickedEvent);
            Mockito.verify(mockedConnectionController).reconnectButtonPressed();
        }
    }

    @Test
    public void testUseCustomDirCheckbox() {
        List<Node> checkboxList = settingsPane.getChildren().filtered(node -> node instanceof CheckBox);

        CheckBox offlineModeCheckbox = checkboxList.stream()
                .filter(node -> node instanceof CheckBox).map(node -> (CheckBox) node)
                .filter(checkBox -> "Use custom dir".equals(checkBox.getText()))
                .findFirst().orElseGet(Assertions::fail);

        List<Node> customDirTextFieldList = settingsPane.getChildren().filtered((node) -> (node instanceof TextField)
                && StringUtils.isEmpty(((TextField) node).getPromptText()));

        Assertions.assertEquals(1, customDirTextFieldList.size());
        Assertions.assertFalse(customDirTextFieldList.get(0).isVisible());
        offlineModeCheckbox.setSelected(true);
        Assertions.assertTrue(customDirTextFieldList.get(0).isVisible());
    }

    @Test
    public void testUseCustomDirTextField() {
        File mockedFile = Mockito.mock();
        Mockito.when(mockedFile.toString()).thenReturn("");
        try (MockedConstruction<DirectoryChooser> mockedConstruction = Mockito.mockConstruction(DirectoryChooser.class,
                (mock, context) -> {
                    Mockito.doReturn(mockedFile).when(mock).showDialog(Mockito.any());
                })) {
            settingsPane = new SettingsPane(mockedController);
            List<Node> customDirTextFieldList = settingsPane.getChildren().filtered((node) -> (node instanceof TextField)
                    && StringUtils.isEmpty(((TextField) node).getPromptText()));
            Assertions.assertEquals(1, customDirTextFieldList.size());
            customDirTextFieldList.get(0).fireEvent(mouseClickedEvent);

            Mockito.verify(mockedController).defaultDirectorySelected(Mockito.anyString());
        }
    }

    @Test
    public void testReconnectButton() {
        try (MockedStatic<ConnectionController> mockedStaticConnectionController
                     = Mockito.mockStatic(ConnectionController.class)) {
            ConnectionController mockedInstance = Mockito.mock();
            mockedStaticConnectionController.when(ConnectionController::getInstance).thenReturn(mockedInstance);
            List<Node> buttonList = settingsPane.getChildren().filtered(node -> node instanceof Button);
            Assertions.assertEquals(1, buttonList.size());

            buttonList.get(0).fireEvent(mouseClickedEvent);

            Mockito.verify(mockedInstance).reconnectButtonPressed();

        }
    }

    @Test
    public void testServerURLConfigSetToSettingsWhenUnfocused() throws Exception {
        List<Node> textFieldList = settingsPane.getChildren().filtered(node -> (node instanceof TextField)
                && StringUtils.isNotEmpty(((TextField) node).getPromptText()));

        Assertions.assertEquals(1, textFieldList.size());

        TextField serverURLField = (TextField) textFieldList.get(0);

        Method m = Node.class.getDeclaredMethod("setFocused", boolean.class);
        m.setAccessible(true);
        m.invoke(serverURLField, true);

        serverURLField.setText("someValue");
        Assertions.assertNotEquals("someValue", Settings.getInstance().getLserverURL());

        m.invoke(serverURLField, false);

        Assertions.assertEquals("someValue", Settings.getInstance().getLserverURL());
    }
}