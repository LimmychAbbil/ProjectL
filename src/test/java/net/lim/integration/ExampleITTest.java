package net.lim.integration;

import javafx.scene.control.Button;
import org.junit.jupiter.api.*;

import static org.testfx.api.FxAssert.verifyThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ExampleITTest extends LLauncherBaseTest {

    @Test
    @Disabled
//    @Order(1)
//    @DisplayName("should verify text 'click me' before click button")
    public void should_verify_text_click_me_before_click() {

        Button button = lookup("Log in").queryButton();

        Assertions.assertNotNull(button);
        Assertions.assertEquals("Log in", button.getText());
    }
}
