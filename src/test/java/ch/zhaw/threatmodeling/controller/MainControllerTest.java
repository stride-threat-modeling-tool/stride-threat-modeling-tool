package ch.zhaw.threatmodeling.controller;

import ch.zhaw.threatmodeling.App;
import ch.zhaw.threatmodeling.model.threats.Threat;
import ch.zhaw.threatmodeling.model.threats.ThreatGenerator;
import ch.zhaw.threatmodeling.model.enums.STRIDECategory;
import ch.zhaw.threatmodeling.model.enums.State;
import ch.zhaw.threatmodeling.model.enums.ThreatPriority;
import ch.zhaw.threatmodeling.skin.controller.DataFlowDiagramSkinController;
import javafx.beans.property.StringProperty;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;


class MainControllerTest extends ApplicationTest {

    private final Threat threat = new Threat(1,
            State.NOT_STARTED,
            STRIDECategory.SPOOFING,
            "",
            "",
            "test",
            null,
             null, null, null);
    private final Threat threat2 = new Threat(2,
            State.NOT_STARTED,
            STRIDECategory.SPOOFING,
            "",
            "",
            "test2",
            null,
            null, null, null);
    private App app;
    private MainController mainController;
    private DataFlowDiagramSkinController skinController;
    private ThreatGenerator threatGenerator;

    @Override
    public void start(Stage stage) throws Exception {
        app = new App();
        app.start(stage);
        mainController = app.getController();
        threat.setDescription("test desc");
        threat.setTitle("test threat");
        threat2.setDescription("test desc2");
        threat2.setTitle("test threat2");
    }

    @Test
    void testGraphEditorSet() {
        assertNotNull(mainController.getGraphEditor());
    }

    @Test
    void testThreatTableGetsFilled() {
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
    void testSelectingThreatFillsEditFields() {
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
    void testStateChoiceBoxChangesThreat() {
        verifyMainControllerInit();
        fillThreatTableSelectLast();
        showThreatPane();
        interact(() -> {
            SingleSelectionModel<State> model = mainController.getStateChoiceBox().getSelectionModel();
            State currentState = model.getSelectedItem();
            assertFalse(threat.isModified());
            mainController.getStateChoiceBox().requestFocus();
            model.select(model.getSelectedIndex() + 1);
            assertNotEquals(currentState, model.getSelectedItem());
            assertEquals(model.getSelectedItem(), threat.getState());

        });
        assertTrue(threat.isModified());
        assertFalse(threat2.isModified());
    }

    @Test
    void testCategoryChoiceBoxChangesThreat() {
        verifyMainControllerInit();
        fillThreatTableSelectLast();
        showThreatPane();
        interact(() -> {
            SingleSelectionModel<STRIDECategory> model = mainController.getCategoryChoiceBox().getSelectionModel();
            STRIDECategory category = model.getSelectedItem();
            assertFalse(threat.isModified());
            mainController.getCategoryChoiceBox().requestFocus();
            model.select(model.getSelectedIndex() + 1);
            assertNotEquals(category, model.getSelectedItem());
            assertEquals(model.getSelectedItem(), threat.getCategory());
        });
        assertTrue(threat.isModified());
        assertFalse(threat2.isModified());
    }

    @Test
    void testPriorityBoxChangesThreat() {
        verifyMainControllerInit();
        fillThreatTableSelectLast();
        showThreatPane();
        interact(() -> {
            SingleSelectionModel<ThreatPriority> model = mainController.getPriorityChoiceBox().getSelectionModel();
            ThreatPriority priority = model.getSelectedItem();
            assertFalse(threat.isModified());
            mainController.getPriorityChoiceBox().requestFocus();
            model.select(model.getSelectedIndex() + 1);
            assertNotEquals(priority, model.getSelectedItem());
            assertEquals(model.getSelectedItem(), threat.getPriority());

        });
        assertTrue(threat.isModified());
        assertFalse(threat2.isModified());
    }

    @Test
    void testTitleTextFieldChangesThreat() {
        testTextField(mainController.getEditTitleTextField(), threat.getTitle(), threat.getTitleProperty());
    }
    @Test
    void testDescriptionTextAreaChangesThreat() {
        testTextArea(mainController.getDescriptionTextArea(), threat.getDescription(), threat.getDescriptionProperty());
    }

    @Test
    void testJustificationTextAreaChangesThreat() {
        testTextArea(mainController.getJustificationTextArea(), threat.getJustification(), threat.getJustificationProperty());
    }


    private void testTextField(TextField field, String target, StringProperty propertyToTest) {
        verifyMainControllerInit();
        fillThreatTableSelectLast();
        showThreatPane();
        String textToAdd = " modified";
        assertFalse(threat.isModified());
        assertNotEquals(textToAdd, threat.getTitle());
        clickOn(field);
        testTextInput(textToAdd, target, propertyToTest);


    }

    private void testTextInput( String textToAdd, String target, StringProperty propertyToTest) {
        write(textToAdd);
        assertEquals(target + textToAdd, propertyToTest.get());
        assertTrue(threat.isModified());
        assertFalse(threat2.isModified());
    }

    private void testTextArea(TextArea area, String target, StringProperty propertyToTest) {
        verifyMainControllerInit();
        fillThreatTableSelectLast();
        showThreatPane();
        String textToAdd = " modified";
        assertFalse(threat.isModified());
        assertNotEquals(textToAdd, threat.getTitle());
        clickOn(area);
        testTextInput(textToAdd, target, propertyToTest);

    }

    private void showThreatPane() {
        interact(() -> {
            mainController.getExpandableThreatPane().setExpanded(true);
            sleep(250);
        });

    }

    @AfterEach
    public void tearDown() throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }
}
