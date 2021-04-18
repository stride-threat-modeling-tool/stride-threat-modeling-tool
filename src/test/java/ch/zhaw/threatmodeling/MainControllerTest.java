package ch.zhaw.threatmodeling;

import ch.zhaw.threatmodeling.controller.MainController;
import ch.zhaw.threatmodeling.model.Threat;
import ch.zhaw.threatmodeling.model.ThreatGenerator;
import ch.zhaw.threatmodeling.model.enums.STRIDECategory;
import ch.zhaw.threatmodeling.model.enums.State;
import ch.zhaw.threatmodeling.model.enums.ThreatPriority;
import ch.zhaw.threatmodeling.skin.controller.DataFlowDiagramSkinController;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;


class MainControllerTest extends ApplicationTest {

    private static final Logger LOGGER = Logger.getLogger("Main Controller Test");
    private final Threat threat = new Threat(1,
            State.NOT_STARTED,
            "test threat",
            STRIDECategory.SPOOFING,
            "test desc",
            "test",
            null,
            null);
    private final Threat threat2 = new Threat(2,
            State.NOT_STARTED,
            "test threat2",
            STRIDECategory.SPOOFING,
            "test desc2",
            "test2",
            null,
            null);
    private App app;
    private MainController mainController;
    private DataFlowDiagramSkinController skinController;
    private ThreatGenerator threatGenerator;

    @Override
    public void start(Stage stage) throws Exception {
        app = new App();
        app.start(stage);
        mainController = app.getController();
    }

    @Test
    public void testGraphEditorSet() {
        assertNotNull(mainController.getGraphEditor());
    }

    @Test
    public void testThreatTableGetsFilled() {
        verifyMainControllerInit();
        threatGenerator.getThreats().add(threat);
        assertEquals(1, mainController.getThreatTable().getItems().size());

        int threatsToAdd = 10;
        for (int i = 0; i < threatsToAdd; i++) {
            threatGenerator.getThreats().add(threat);
        }
        assertEquals(11, mainController.getThreatTable().getItems().size());
    }

    private void verifyMainControllerInit() {
        skinController = mainController.getDfdSkinController();
        assertNotNull(skinController);
        threatGenerator = skinController.getThreatGenerator();
        assertNotNull(threatGenerator);
    }

    private void fillThreatTableSelectLast() {
        threatGenerator.getThreats().add(threat2);
        threatGenerator.getThreats().add(threat);

        interact(() -> mainController.getThreatTable().getSelectionModel().select(mainController.getThreatTable().getItems().size() - 1));
    }

    @Test
    public void testSelectingThreatFillsEditFields() {
        verifyMainControllerInit();
        fillThreatTableSelectLast();
        assertEquals(threat.getTitle(), mainController.getEditTitleTextField().getText());
        assertEquals(threat.getDescription(), mainController.getDescriptionTextArea().getText());
        assertEquals(threat.getJustification(), mainController.getJustificationTextArea().getText());
        assertEquals(threat.getState(), mainController.getStateChoiceBox().getSelectionModel().getSelectedItem());
        assertEquals(threat.getCategory(), mainController.getCategoryChoiceBox().getSelectionModel().getSelectedItem());
        assertEquals(threat.getPriority(), mainController.getPriorityChoiceBox().getSelectionModel().getSelectedItem());

    }

    @Test
    public void testStateChoiceBoxChangesThreat() {
        verifyMainControllerInit();
        fillThreatTableSelectLast();
        interact(() -> {
            SingleSelectionModel<State> model = mainController.getStateChoiceBox().getSelectionModel();
            State currentState = model.getSelectedItem();
            assertFalse(threat.isModified());
            model.select(model.getSelectedIndex() + 1);
            assertNotEquals(currentState, model.getSelectedItem());
            assertEquals(model.getSelectedItem(), threat.getState());

        });
        assertTrue(threat.isModified());
        assertFalse(threat2.isModified());
    }

    @Test
    public void testCategoryChoiceBoxChangesThreat() {
        verifyMainControllerInit();
        fillThreatTableSelectLast();
        interact(() -> {
            SingleSelectionModel<STRIDECategory> model = mainController.getCategoryChoiceBox().getSelectionModel();
            STRIDECategory category = model.getSelectedItem();
            assertFalse(threat.isModified());
            model.select(model.getSelectedIndex() + 1);
            assertNotEquals(category, model.getSelectedItem());
            assertEquals(model.getSelectedItem(), threat.getCategory());
        });
        assertTrue(threat.isModified());
        assertFalse(threat2.isModified());
    }

    @Test
    public void testPriorityBoxChangesThreat() {
        verifyMainControllerInit();
        fillThreatTableSelectLast();
        interact(() -> {
            SingleSelectionModel<ThreatPriority> model = mainController.getPriorityChoiceBox().getSelectionModel();
            ThreatPriority priority = model.getSelectedItem();
            assertFalse(threat.isModified());
            model.select(model.getSelectedIndex() + 1);
            assertNotEquals(priority, model.getSelectedItem());
            assertEquals(model.getSelectedItem(), threat.getPriority());

        });
        assertTrue(threat.isModified());
        assertFalse(threat2.isModified());
    }

    @Test
    public void testTitleTextFieldChangesThreat() {
        String testTitle = "modified title";
        verifyMainControllerInit();
        fillThreatTableSelectLast();
        showThreatPane();
        assertFalse(threat.isModified());
        interact(() -> {
            assertNotEquals(testTitle, threat.getTitle());
            clickOn(mainController.getEditTitleTextField());
            write(testTitle);
            sleep(5000);
            assertEquals(testTitle, threat.getTitle());

        });
        assertTrue(threat.isModified());
        assertFalse(threat2.isModified());
    }

    private void showThreatPane() {
        interact(() -> {
            mainController.getExpandableThreatPane().setExpanded(true);
            sleep(2000);
        });

    }

    @AfterEach
    public void tearDown() throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }
}
