package ch.zhaw.threatmodeling.skin.controller;

import ch.zhaw.threatmodeling.App;
import ch.zhaw.threatmodeling.controller.MainController;
import ch.zhaw.threatmodeling.skin.connection.DataFlowConnectionSkin;
import ch.zhaw.threatmodeling.skin.nodes.datastore.DataStoreNodeSkin;
import ch.zhaw.threatmodeling.skin.nodes.externalentity.ExternalEntityNodeSkin;
import ch.zhaw.threatmodeling.skin.nodes.generic.GenericNodeSkin;
import ch.zhaw.threatmodeling.skin.nodes.multipleprocess.MultipleProcessNodeSkin;
import ch.zhaw.threatmodeling.skin.nodes.process.ProcessNodeSkin;
import ch.zhaw.threatmodeling.skin.utils.DataFlowConnectionCommands;
import ch.zhaw.threatmodeling.skin.utils.DataFlowNodeCommands;
import de.tesis.dynaware.grapheditor.GraphEditor;
import de.tesis.dynaware.grapheditor.SelectionManager;
import de.tesis.dynaware.grapheditor.SkinLookup;
import de.tesis.dynaware.grapheditor.core.view.GraphEditorView;
import de.tesis.dynaware.grapheditor.model.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class DoControllerTest extends ApplicationTest {
    private static final Logger LOGGER = Logger.getLogger("docontroller test");
    private MainController mainController;
    private DoController doController;
    private DataFlowDiagramSkinController skinController;
    private SelectionManager selectionManager;

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
    void testUndoRedoAddingDataStore() {
        interact(() -> mainController.addDataStore());
        verifyUndoRedoSingleNode("Undo" + DataStoreNodeSkin.TITLE_TEXT);


    }

    @Test
    void testUndoRedoAddingExternalEntity() {
        interact(() -> mainController.addExternalEntity());
        verifyUndoRedoSingleNode("Undo" + ExternalEntityNodeSkin.TITLE_TEXT);


    }

    @Test
    void testUndoRedoAddingProcess() {
        interact(() -> mainController.addProcess());
        verifyUndoRedoSingleNode("Undo" + ProcessNodeSkin.TITLE_TEXT);


    }

    @Test
    void testUndoRedoAddingMultipleProcess() {
        interact(() -> mainController.addMultipleProcess());
        verifyUndoRedoSingleNode("Undo" + MultipleProcessNodeSkin.TITLE_TEXT);
    }

    @Test
    void testSelectTrustBoundaryJointDeletesWholeTrustBoundary() {
        assertNotNull(doController);
        interact(() -> mainController.addTrustBoundary());
        GModel model = skinController.getGraphEditor().getModel();
        List<GConnection> connections = model.getConnections();
        List<GNode> nodes = model.getNodes();
        assertEquals(1, connections.size());
        assertEquals(2, nodes.size());

        GConnection connection = connections.get(0);
        selectionManager.select(connection.getJoints().get(0));
        verifyTrustBoundaryDeletionIsReversible(nodes, connections);
    }

    @Test
    void testSelectTrustBoundaryNodeDeletesWholeTrustBoundary() {
        assertNotNull(doController);
        interact(() -> mainController.addTrustBoundary());
        GModel model = skinController.getGraphEditor().getModel();
        List<GConnection> connections = model.getConnections();
        List<GNode> nodes = model.getNodes();
        assertEquals(1, connections.size());
        assertEquals(2, nodes.size());

        selectionManager.select(nodes.get(0));
        verifyTrustBoundaryDeletionIsReversible(nodes, connections);
        interact(() -> doController.undo());
        selectionManager.select(nodes.get(1));
        verifyTrustBoundaryDeletionIsReversible(nodes, connections);
    }

    @Test
    void testSelectTrustBoundaryConnectionDeletesWholeTrustBoundary() {
        assertNotNull(doController);
        interact(() -> mainController.addTrustBoundary());
        GModel model = skinController.getGraphEditor().getModel();
        List<GConnection> connections = model.getConnections();
        List<GNode> nodes = model.getNodes();
        assertEquals(1, connections.size());
        assertEquals(2, nodes.size());

        selectionManager.select(connections.get(0));
        verifyTrustBoundaryDeletionIsReversible(nodes, connections);
    }

    @Test
    void testDeleteDataFlowViaJointIsReversible() {
        assertNotNull(doController);
        String text = "test text";
        GModel model = skinController.getGraphEditor().getModel();
        List<GConnection> connections = model.getConnections();
        List<GNode> nodes = model.getNodes();
        interact(() -> {
            mainController.addProcess();
            mainController.addDataStore();
        });
        GNode node1 = nodes.get(0);
        GNode node2 = nodes.get(1);
        List<GJoint> joints = new ArrayList<>();
        joints.add(createJoint());
        interact(() -> DataFlowConnectionCommands.addConnection(
                model,
                node1.getConnectors().get(0),
                node2.getConnectors().get(1),
                DataFlowConnectionSkin.getType(),
                joints,
                null,
                doController.getCreateCommandToTypeMapping()
        ));

        assertEquals(1, connections.size());
        GConnection connection = connections.get(0);
        interact(() -> DataFlowConnectionCommands.setJointLabel(connection, text, skinController.getGraphEditor().getSkinLookup()));
        selectionManager.select(connection.getJoints().get(0));
        verifyDataFlowDeletionIsReversible(nodes, connections);
        assertEquals(text, DataFlowConnectionCommands.getJointLabel(connection, skinController.getGraphEditor().getSkinLookup()));
    }

    @Test
    void testDeleteDataFlowViaConnectionIsReversible() {
        assertNotNull(doController);
        String text = "test text";
        GModel model = skinController.getGraphEditor().getModel();
        List<GConnection> connections = model.getConnections();
        List<GNode> nodes = model.getNodes();
        interact(() -> {
            mainController.addProcess();
            mainController.addDataStore();
        });
        GNode node1 = nodes.get(0);
        GNode node2 = nodes.get(1);
        List<GJoint> joints = new ArrayList<>();
        joints.add(createJoint());
        interact(() -> DataFlowConnectionCommands.addConnection(
                model,
                node1.getConnectors().get(0),
                node2.getConnectors().get(1),
                DataFlowConnectionSkin.getType(),
                joints,
                null,
                doController.getCreateCommandToTypeMapping()
        ));

        assertEquals(1, connections.size());
        GConnection connection = connections.get(0);
        interact(() -> DataFlowConnectionCommands.setJointLabel(connection, text, skinController.getGraphEditor().getSkinLookup()));
        selectionManager.select(connection);
        verifyDataFlowDeletionIsReversible(nodes, connections);
        assertEquals(text, DataFlowConnectionCommands.getJointLabel(connection, skinController.getGraphEditor().getSkinLookup()));

    }

    private void verifyDataFlowDeletionIsReversible(List<GNode> nodes, List<GConnection> connections) {
        interact(() -> skinController.deleteSelection());
        assertEquals(0, connections.size());
        interact(() -> doController.undo());
        assertEquals(1, connections.size());

        interact(() -> doController.redo());
        assertEquals(0, connections.size());
        assertEquals(2, nodes.size());
        interact(() -> doController.undo());
    }

    private void verifyTrustBoundaryDeletionIsReversible(List<GNode> nodes, List<GConnection> connections) {
        assertNotNull(doController);
        String text = "test text";
        interact(() -> DataFlowConnectionCommands.setJointLabel(connections.get(0), text, skinController.getGraphEditor().getSkinLookup()));
        interact(() -> skinController.deleteSelection());
        assertEquals(0, connections.size());
        assertEquals(0, nodes.size());
        skinController.getGraphEditor().getView().getChildrenUnmodifiable().forEach(elem -> {
            if (elem.getStyleClass().contains("graph-editor-connection-layer") ||
                    elem.getStyleClass().contains(("graph-editor-node-layer"))) {
                assertEquals(0, ((Pane) elem).getChildren().size());
            }

        });

        interact(() -> doController.undo());
        assertEquals(2, nodes.size());
        assertEquals(1, connections.size());
        assertEquals(text, DataFlowConnectionCommands.getJointLabel(connections.get(0), skinController.getGraphEditor().getSkinLookup()));

        interact(() -> doController.redo());
        assertEquals(0, nodes.size());
        assertEquals(0, connections.size());
    }

    @Test
    void testUndoRedoAddingTrustBoundary() {
        GraphEditor editor = mainController.getDfdSkinController().getGraphEditor();
        GModel model = editor.getModel();
        List<GNode> nodes = model.getNodes();
        List<GConnection> connections = model.getConnections();
        interact(() -> mainController.addTrustBoundary());
        assertEquals(2, nodes.size());
        assertEquals(1, connections.size());

        interact(() -> doController.undo());
        assertEquals(0, nodes.size());
        assertEquals(0, connections.size());

        interact(() -> doController.redo());
        assertEquals(2, nodes.size());
        assertEquals(1, connections.size());
    }

    private void verifyUndoRedoSingleNode(String expectedText) {
        GraphEditor editor = mainController.getDfdSkinController().getGraphEditor();
        GModel model = editor.getModel();
        SkinLookup skinLookup = editor.getSkinLookup();
        List<GNode> nodes = model.getNodes();
        List<GConnection> connections = model.getConnections();
        assertEquals(1, nodes.size());
        assertEquals(0, connections.size());
        GenericNodeSkin nodeSkin = (GenericNodeSkin) skinLookup.lookupNode(nodes.get(0));


        interact(() -> {
            nodeSkin.setText(expectedText);
            doController.undo();
        });
        assertEquals(0, nodes.size());

        interact(() -> doController.redo());
        assertEquals(1, nodes.size());
        assertEquals(expectedText, DataFlowNodeCommands.getTextOfNode(nodes.get(0), skinLookup));
        assertTrue(expectedText.contains(DataFlowNodeCommands.getTypeOfNode(nodes.get(0), skinLookup)));
    }

    private GJoint createJoint() {
        return GraphFactory.eINSTANCE.createGJoint();
    }

    @AfterEach
    public void tearDown() throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

}
