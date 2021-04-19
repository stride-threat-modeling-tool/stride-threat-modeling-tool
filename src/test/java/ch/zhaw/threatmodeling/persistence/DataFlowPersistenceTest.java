package ch.zhaw.threatmodeling.persistence;

import ch.zhaw.threatmodeling.App;
import ch.zhaw.threatmodeling.controller.MainController;
import ch.zhaw.threatmodeling.skin.connection.DataFlowConnectionSkin;
import ch.zhaw.threatmodeling.skin.controller.DataFlowDiagramSkinController;
import ch.zhaw.threatmodeling.skin.nodes.generic.GenericNodeSkin;
import ch.zhaw.threatmodeling.skin.nodes.trustboundary.TrustBoundaryNodeSkin;
import ch.zhaw.threatmodeling.skin.utils.DataFlowConnectionCommands;
import ch.zhaw.threatmodeling.skin.utils.DataFlowNodeCommands;
import de.tesis.dynaware.grapheditor.GraphEditor;
import de.tesis.dynaware.grapheditor.SkinLookup;
import de.tesis.dynaware.grapheditor.model.*;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationTest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataFlowPersistenceTest extends ApplicationTest {
    private MainController mainController;
    private DataFlowDiagramSkinController skinController;
    private static final Logger LOGGER = Logger.getLogger("Persistence test");


    @TempDir
    File tempDir;

    @Override
    public void start(Stage stage) throws Exception {
        App app = new App();
        app.start(stage);
        mainController = app.getController();
        skinController = mainController.getDfdSkinController();
    }

    @Test
    void testRestoredNodesIdenticalToOriginal() throws IOException {
        Map<String, Pair<String, GNode>> targetNodes = new HashMap<>();
        initSpiedPersistence();
        addNodes(25);
        saveNodeState(targetNodes);
        interact(() ->  {
            mainController.saveDiagram();
            mainController.clearAll();
            mainController.loadDiagram();
        });
        compareNodeState(targetNodes);
    }

    @Test
    void testRestoredConnectionsIdenticalToOriginal() throws IOException {
        Map<String, Pair<String, GNode>> targetNodes = new HashMap<>();
        Map<String, Pair<Pair<GConnection, String>, Pair<String, String>>> targetConnections = new HashMap<>();
        initSpiedPersistence();
        addNodes(15);
        saveNodeState(targetNodes);
        addConnections(75);
        saveConnectionsState(targetConnections);
        interact(() ->  {
            mainController.saveDiagram();
            mainController.clearAll();
            mainController.loadDiagram();
        });
        compareNodeState(targetNodes);
        compareConnectionState(targetConnections);

    }

    private void compareConnectionState(Map<String, Pair<Pair<GConnection, String>, Pair<String, String>>> targetConnections) {
        final GraphEditor editor = skinController.getGraphEditor();
        final SkinLookup skinLookup = editor.getSkinLookup();
        List<GConnection> connections = editor.getModel().getConnections();
        assertEquals(targetConnections.size(), connections.size());
        connections.forEach(con -> {
            String nodeLabel = DataFlowConnectionCommands.getJointLabel(con, skinLookup);
            assertTrue(targetConnections.containsKey(nodeLabel));
            Pair<Pair<GConnection, String>, Pair<String, String>> savedConProperties = targetConnections.get(nodeLabel);
            Pair<String, String> savedNodeNames = savedConProperties.getValue();
            GConnection savedCon = savedConProperties.getKey().getKey();
            assertEquals(
                    getSourceConnectorIndex(savedCon, savedCon.getSource()),
                    getSourceConnectorIndex(con, con.getSource()));
            assertEquals(
                    getTargetConnectorIndex(savedCon, savedCon.getTarget()),
                    getTargetConnectorIndex(con, con.getTarget())
            );
            GJoint savedJoint = savedCon.getJoints().get(0);
            GJoint joint = con.getJoints().get(0);
            assertEquals(savedJoint.getX(), joint.getX());
            assertEquals(savedJoint.getY(), joint.getY());
            assertEquals(
                    savedNodeNames.getKey(),
                    DataFlowNodeCommands.getTextOfNode(con.getSource().getParent(), skinLookup));
            assertEquals(savedNodeNames.getValue(),
                    DataFlowNodeCommands.getTextOfNode(con.getTarget().getParent(), skinLookup));
            assertTrue(nodeLabel.contains(joint.getType()));
            LOGGER.info("Connection name restored " + nodeLabel);

        });
    }

    private int getSourceConnectorIndex(GConnection con, GConnector connector){
        return getConnectorIndex(con.getSource().getParent(), connector);
    }

    private int getConnectorIndex(GNode node, GConnector connector) {
        return node.getConnectors().indexOf(connector);
    }

    private int getTargetConnectorIndex(GConnection con, GConnector connector){
        return  getConnectorIndex(con.getTarget().getParent(), connector);
    }

    private void saveConnectionsState(Map<String, Pair<Pair<GConnection, String>, Pair<String, String>>> targetConnections) {
        final GraphEditor editor = skinController.getGraphEditor();
        final SkinLookup skinLookup = editor.getSkinLookup();
        final GModel model = editor.getModel();
        final List<GConnection> connections = model.getConnections();
        final Random rmd = new Random();
        connections.forEach(con -> {
            String uniqueName;
            do{
                uniqueName = DataFlowConnectionCommands.getJointLabel(con, skinLookup);
                uniqueName += rmd.nextInt();
            } while(targetConnections.containsKey(uniqueName));
            final String finalName = uniqueName;
            interact(() -> {
                DataFlowConnectionCommands.setJointLabel(con, finalName, skinLookup);
            });
            String targetNodeName = DataFlowNodeCommands.getTextOfNode(con.getTarget().getParent(), skinLookup);
            String srcNodeName = DataFlowNodeCommands.getTextOfNode(con.getSource().getParent(), skinLookup);
            targetConnections.put(uniqueName,
                    new Pair<>(
                            new Pair<>(con, DataFlowConnectionCommands.getType(con, skinLookup)),
                            new Pair<>(srcNodeName, targetNodeName)));
        });

    }



    private void initSpiedPersistence() throws IOException {
        mainController.clearAll();
        assertTrue(tempDir.isDirectory());
        File toSaveTo = new File(tempDir, "threatmodeling_tempfile" + DataFlowPersistence.EXTENSION);
        assertTrue(toSaveTo.createNewFile());
        DataFlowPersistence spiedPersistence = Mockito.spy(new DataFlowPersistence());

        Mockito.doReturn(toSaveTo).when(spiedPersistence).showFileChooser(Mockito.any(), Mockito.anyBoolean());
        mainController.setPersistence(spiedPersistence);
    }

    private void compareNodeState(Map<String, Pair<String, GNode>> targetNodes) {
        final GraphEditor editor = skinController.getGraphEditor();
        final SkinLookup skinLookup = editor.getSkinLookup();
        List<GNode> nodes = editor.getModel().getNodes();
        assertEquals(targetNodes.size(), nodes.size());
        nodes.forEach(node -> {
            GenericNodeSkin nodeSkin = (GenericNodeSkin) skinLookup.lookupNode(node);
            String text = nodeSkin.getText();
            assertTrue(targetNodes.containsKey(text));
            Pair<String, GNode> savedState = targetNodes.get(text);
            assertEquals(savedState.getKey(), node.getType());
            GNode savedNodeState = savedState.getValue();
            assertEquals(savedNodeState.getHeight(), node.getHeight());
            assertEquals(savedNodeState.getWidth(), node.getWidth());
            assertEquals(savedNodeState.getX(), node.getX());
            assertEquals(savedNodeState.getY(), node.getY());
            if(!nodeSkin.getType().equals(TrustBoundaryNodeSkin.TITLE_TEXT)){
                assertTrue(text.contains(savedState.getKey()));
            }
        });
    }

    private void saveNodeState(Map<String, Pair<String, GNode>> targetNodes) {
        final GraphEditor editor = skinController.getGraphEditor();
        final SkinLookup skinLookup = editor.getSkinLookup();
        final int bound = 500;
        final Random rmd = new Random();
        interact(() -> {
            editor.getModel().getNodes().forEach(node -> {
                node.setWidth(rmd.nextInt(bound));
                node.setHeight(rmd.nextInt(bound));
                node.setY(rmd.nextInt(bound));
                node.setX(rmd.nextInt(bound));
                GenericNodeSkin nodeSkin = (GenericNodeSkin) skinLookup.lookupNode(node);
                do{
                    nodeSkin.setText(nodeSkin.getText() + rmd.nextInt());
                } while(targetNodes.containsKey(nodeSkin.getText()));

                targetNodes.put(nodeSkin.getText(), new Pair<>(nodeSkin.getType(), node));

            });
        });

    }

    private void addNodes(int n){
        interact(() -> {
            for(int i = 0; i < n; i++){
                mainController.addDataStore();
                mainController.addExternalEntity();
                mainController.addProcess();
                mainController.addMultipleProcess();
                mainController.addTrustBoundary();
            }
        });
    }
    private GJoint createJoint() {
        return GraphFactory.eINSTANCE.createGJoint();
    }

    private void addConnections(int n) {
        final GraphEditor editor = skinController.getGraphEditor();
        final GModel model = editor.getModel();
        final List<GNode> nodes = model.getNodes();
        final Random rmd = new Random();
        final int bound = 500;
        interact(() -> {
            for(int i = 0; i < n; i++){
                GNode srcNode;
                GNode targetNode;
                GConnector srcCon;
                GConnector targetCon;
                do{
                     srcNode = nodes.get(rmd.nextInt(nodes.size()));
                     targetNode = nodes.get(rmd.nextInt(nodes.size()));
                     srcCon = srcNode.getConnectors().get(rmd.nextInt(srcNode.getConnectors().size()));
                     targetCon = targetNode.getConnectors().get(rmd.nextInt(targetNode.getConnectors().size()));
                } while (srcCon == targetCon);
                List<GJoint> joints = new ArrayList<>();
                final GJoint joint = createJoint();
                joint.setType(DataFlowConnectionSkin.type);
                joint.setX(rmd.nextInt(bound));
                joint.setY(rmd.nextInt(bound));
                joints.add(joint);
                DataFlowConnectionCommands.addConnection(
                        model,
                        srcCon,
                        targetCon,
                        DataFlowConnectionSkin.type,
                        joints,
                        null,
                        null);
            }
        });
    }
}
