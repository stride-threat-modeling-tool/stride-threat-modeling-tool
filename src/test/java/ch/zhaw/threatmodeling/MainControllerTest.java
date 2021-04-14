package ch.zhaw.threatmodeling;

import ch.zhaw.threatmodeling.controller.MainController;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import java.util.logging.Logger;


public class MainControllerTest extends ApplicationTest {

    private App app;
    private MainController mainController;
    private static final Logger LOGGER = Logger.getLogger("Main Controller Test");

    @Override
    public void start (Stage stage) throws Exception {
        app = new App();
        app.start(stage);
        mainController = app.getController();
    }
    @BeforeEach
    public void setUp () throws Exception {
    }

    @Test
    public void testGraphEditorSet() {
        Assertions.assertNull(mainController.getGraphEditor());
    }

    @AfterEach
    public void tearDown () throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }
}
