package ch.zhaw.threatmodeling;

import ch.zhaw.threatmodeling.controller.MainController;
import ch.zhaw.threatmodeling.model.Threat;
import ch.zhaw.threatmodeling.model.ThreatGenerator;
import ch.zhaw.threatmodeling.model.enums.STRIDECategory;
import ch.zhaw.threatmodeling.model.enums.State;
import ch.zhaw.threatmodeling.skin.controller.DataFlowDiagramSkinController;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.util.logging.Logger;


public class MainControllerTest extends ApplicationTest {

    private App app;
    private MainController mainController;
    private static final Logger LOGGER = Logger.getLogger("Main Controller Test");
    private DataFlowDiagramSkinController skinController;
    private ThreatGenerator threatGenerator;
    private Threat threat = new Threat(1,
            State.NOT_STARTED,
            "test threat",
            STRIDECategory.SPOOFING,
            "test desc",
            "test",
            null,
            null);
    private Threat threat2 = new Threat(2,
            State.NOT_STARTED,
            "test threat2",
            STRIDECategory.SPOOFING,
            "test desc2",
            "test2",
            null,
            null);

    @Override
    public void start (Stage stage) throws Exception {
        app = new App();
        app.start(stage);
        mainController = app.getController();
    }
    @Before
    public void setUp () throws Exception {
    }

    @Test
    public void testGraphEditorSet() {
        Assert.assertNotNull(mainController.getGraphEditor());
    }

    @Test
    public void testThreatTableGetsFilled(){
        verifyMainControllerInit();
        threatGenerator.getThreats().add(threat);
        Assert.assertEquals(1, mainController.getThreatTable().getItems().size());

        int threatsToAdd = 10;
        for(int i = 0; i < threatsToAdd; i++){
            threatGenerator.getThreats().add(threat);
        }
        Assert.assertEquals(11, mainController.getThreatTable().getItems().size());
    }

    private void verifyMainControllerInit(){
        skinController = mainController.getDfdSkinController();
        Assert.assertNotNull(skinController);
        threatGenerator = skinController.getThreatGenerator();
        Assert.assertNotNull(threatGenerator);
    }

    @Test
    public void testSelectingThreatFillsEditFields() {
        verifyMainControllerInit();
        threatGenerator.getThreats().add(threat2);
        threatGenerator.getThreats().add(threat);

        interact(() -> mainController.getThreatTable().getSelectionModel().select(mainController.getThreatTable().getItems().size() - 1));


        WaitForAsyncUtils.waitForFxEvents();

        Assert.assertEquals(threat.getTitle(), mainController.getEditTitleTextField().getText());
        Assert.assertEquals(threat.getDescription(), mainController.getDescriptionTextArea().getText());
        Assert.assertEquals(threat.getJustification(), mainController.getJustificationTextArea().getText());
        Assert.assertEquals(threat.getState(), mainController.getStateChoiceBox().getSelectionModel().getSelectedItem());
        Assert.assertEquals(threat.getCategory(), mainController.getCategoryChoiceBox().getSelectionModel().getSelectedItem());
        Assert.assertEquals(threat.getPriority(), mainController.getPriorityChoiceBox().getSelectionModel().getSelectedItem());

    }

    @After
    public void tearDown () throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }
}
