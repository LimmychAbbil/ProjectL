package net.lim.unit;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;

public abstract class BaseFXUnitTestClass {

    @BeforeAll
    static void initJfxRuntime() {
        Platform.startup(() -> {});
    }
}
