package ch.zhaw.threatmodeling.skin.controller;

import ch.zhaw.threatmodeling.App;
import ch.zhaw.threatmodeling.controller.MainController;
import ch.zhaw.threatmodeling.skin.nodes.datastore.DataStoreNodeSkin;
import ch.zhaw.threatmodeling.skin.nodes.externalentity.ExternalEntityNodeSkin;
import ch.zhaw.threatmodeling.skin.nodes.generic.GenericNodeSkin;
import ch.zhaw.threatmodeling.skin.nodes.multipleprocess.MultipleProcessNodeSkin;
import ch.zhaw.threatmodeling.skin.nodes.process.ProcessNodeSkin;
import ch.zhaw.threatmodeling.skin.utils.DataFlowNodeCommands;
import de.tesis.dynaware.grapheditor.GraphEditor;
import de.tesis.dynaware.grapheditor.SelectionManager;
import de.tesis.dynaware.grapheditor.SkinLookup;
import de.tesis.dynaware.grapheditor.model.GConnection;
import de.tesis.dynaware.grapheditor.model.GJoint;
import de.tesis.dynaware.grapheditor.model.GModel;
import de.tesis.dynaware.grapheditor.model.GNode;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.*;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class DataFlowDiagramSkinControllerTest extends ApplicationTest {
    private static final Logger LOGGER = Logger.getLogger("DataFlowDiagramSkinControllerTest");
    private MainController mainController;
    private DoController doController;
    private DataFlowDiagramSkinController skinController;
    private SelectionManager selectionManager;
    private final String TRUST_BOUNDARY_TYPE = "Trust Boundary";

    @Override
    public void start(Stage stage) throws Exception {
        App app = new App();
        app.start(stage);
        mainController = app.getController();
        skinController = mainController.getDfdSkinController();
        doController = skinController.getDoController();
        selectionManager = skinController.getGraphEditor().getSelectionManager();
        doController.flushCommandStack();
    }

    @Test
    public void testCopyPasteAddingDataStore() {
        interact(() -> mainController.addDataStore());
        verifyCopyPasteSingleNode("Copy"+ DataStoreNodeSkin.TITLE_TEXT);
    }

    @Test
    public void testCopyPasteAddingExternalEntity() {
        interact(() -> mainController.addExternalEntity());
        verifyCopyPasteSingleNode("Copy"+ ExternalEntityNodeSkin.TITLE_TEXT);
    }

    @Test
    public void testCopyPasteAddingProcess() {
        interact(() -> mainController.addProcess());
        verifyCopyPasteSingleNode("Copy"+ ProcessNodeSkin.TITLE_TEXT);
    }

    @Test
    public void testCopyPasteAddingMultipleProcess() {
        interact(() -> mainController.addMultipleProcess());
        verifyCopyPasteSingleNode("Copy"+ MultipleProcessNodeSkin.TITLE_TEXT);
    }

    private void verifyCopyPasteSingleNode(String expectedText) {
        final GraphEditor editor = skinController.getGraphEditor();
        final GModel model = editor.getModel();
        final List<GNode> nodes = model.getNodes();
        final SelectionManager selector = editor.getSelectionManager();
        final SkinLookup skinLookup = editor.getSkinLookup();
        final List<GConnection> connections = model.getConnections();
        assertEquals(1, nodes.size());
        assertEquals(0, connections.size());
        GenericNodeSkin nodeSkin = (GenericNodeSkin) skinLookup.lookupNode(nodes.get(0));

        nodes.forEach(node -> {
            interact(() -> {
                nodeSkin.setText(expectedText);
                selector.select(node);
            });
        });
        mainController.copy();
        interact(() -> {
            mainController.paste();
        });
        assertEquals(2, model.getNodes().size());
        assertEquals(expectedText, DataFlowNodeCommands.getTextOfNode(nodes.get(0), skinLookup));
        assertTrue(expectedText.contains(DataFlowNodeCommands.getTypeOfNode(nodes.get(0), skinLookup)));
    }

    @Test
    public void testCopyPasteTrustBoundaryByJoint() {
        // index out of bounds exception
        final GraphEditor editor = skinController.getGraphEditor();
        final GModel model = editor.getModel();
        final List<GNode> nodes = model.getNodes();
        final SelectionManager selector = editor.getSelectionManager();
        final List<GConnection> connections = model.getConnections();

        interact(() -> mainController.addTrustBoundary());

        final GJoint joint = connections.get(0).getJoints().get(0);

        assertEquals(2, nodes.size());
        assertEquals(1, connections.size());

        interact(() -> {
            selector.select(joint);
        });
        mainController.copy();
        interact(() -> {
            mainController.paste();
        });

        assertEquals(4, model.getNodes().size());
        assertEquals(2, model.getConnections().size());
        validateTrustBoundaryElementTypes(nodes, connections);
    }

    @Test
    public void testCopyPasteTrustBoundaryBySourceNode() {
        // doesn't work, only pastes a ghost node
        final GraphEditor editor = skinController.getGraphEditor();
        final GModel model = editor.getModel();
        final List<GNode> nodes = model.getNodes();
        final SelectionManager selector = editor.getSelectionManager();

        interact(() -> mainController.addTrustBoundary());
        final List<GConnection> connections = model.getConnections();

        assertEquals(2, nodes.size());
        assertEquals(1, connections.size());

        final GConnection connection = connections.get(0);
        final GNode node = connection.getSource().getParent();

        interact(() -> {
            selector.select(node);
        });
        mainController.copy();
        interact(() -> {
            mainController.paste();
        });

        assertEquals(4, model.getNodes().size());
        assertEquals(2, model.getConnections().size());
        validateTrustBoundaryElementTypes(nodes, connections);
    }

    @Test
    public void testCopyPasteTrustBoundaryByTargetNode() {
        // doesn't work, only pastes a ghost node
        final GraphEditor editor = skinController.getGraphEditor();
        final GModel model = editor.getModel();
        final List<GNode> nodes = model.getNodes();
        final SelectionManager selector = editor.getSelectionManager();

        interact(() -> mainController.addTrustBoundary());
        final List<GConnection> connections = model.getConnections();

        assertEquals(2, nodes.size());
        assertEquals(1, connections.size());

        final GConnection connection = connections.get(0);
        final GNode node = connection.getTarget().getParent();

        interact(() -> {
            selector.select(node);
        });
        mainController.copy();
        interact(() -> {
            mainController.paste();
        });

        assertEquals(4, model.getNodes().size());
        assertEquals(2, model.getConnections().size());
        validateTrustBoundaryElementTypes(nodes, connections);
    }

    @Test
    public void testCopyPasteTrustBoundaryByNodes() {
        final GraphEditor editor = skinController.getGraphEditor();
        final GModel model = editor.getModel();
        final List<GNode> nodes = model.getNodes();
        final SelectionManager selector = editor.getSelectionManager();
        final List<GConnection> connections = model.getConnections();

        interact(() -> mainController.addTrustBoundary());

        assertEquals(2, nodes.size());
        assertEquals(1, connections.size());

        nodes.forEach(node -> {
            interact(() -> {
                selector.select(node);
            });
        });
        mainController.copy();
        interact(() -> {
            mainController.paste();
        });

        assertEquals(4, model.getNodes().size());
        assertEquals(2, model.getConnections().size());
        validateTrustBoundaryElementTypes(nodes, connections);
    }

    @Test
    public void testCopyPasteTrustBoundaryCompletely() {
        final GraphEditor editor = skinController.getGraphEditor();
        final GModel model = editor.getModel();
        final List<GNode> nodes = model.getNodes();
        final SelectionManager selector = editor.getSelectionManager();

        interact(() -> mainController.addTrustBoundary());
        final List<GConnection> connections = model.getConnections();

        assertEquals(2, nodes.size());
        assertEquals(1, connections.size());

        final GConnection connection = connections.get(0);
        final GJoint joint = connections.get(0).getJoints().get(0);
        selector.select(connection);
        selector.select(joint);

        nodes.forEach(node -> {
            interact(() -> {
                selector.select(node);
            });
        });

        assertEquals(4, selector.getSelectedItems().size());

        mainController.copy();
        interact(() -> {
            mainController.paste();
        });

        assertEquals(4, model.getNodes().size());
        assertEquals(2, model.getConnections().size());
        validateTrustBoundaryElementTypes(nodes, connections);
    }

    private void validateTrustBoundaryElementTypes(List<GNode> nodes, List<GConnection> connections) {
        nodes.forEach(node -> assertEquals(TRUST_BOUNDARY_TYPE, node.getType()));
        for (GConnection currentConnection : connections) {
            assertEquals(TRUST_BOUNDARY_TYPE, currentConnection.getType());
            for (GJoint currentJoint : currentConnection.getJoints()) {
                assertEquals(TRUST_BOUNDARY_TYPE, currentJoint.getType());
            }
        }
    }

    @Test
    public void testCopyPasteAllStencils() {
        interact(() -> {
            mainController.addDataStore();
            mainController.addProcess();
            mainController.addExternalEntity();
            mainController.addMultipleProcess();
            mainController.addTrustBoundary();
        });

        final GraphEditor editor = skinController.getGraphEditor();
        final GModel model = editor.getModel();
        final List<GNode> nodes = model.getNodes();
        final SelectionManager selector = editor.getSelectionManager();
        final List<GConnection> connections = model.getConnections();
        assertEquals(6, nodes.size());
        assertEquals(1, connections.size());

        nodes.forEach(node -> {
            interact(() -> {
                selector.select(node);
            });
        });
        mainController.copy();
        interact(() -> {
            mainController.paste();
        });

        assertEquals(12, nodes.size());
        assertEquals(2, connections.size());
    }

    @AfterEach
    public void tearDown() throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

}
