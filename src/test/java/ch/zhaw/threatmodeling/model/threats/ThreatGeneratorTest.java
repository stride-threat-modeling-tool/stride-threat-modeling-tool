package ch.zhaw.threatmodeling.model.threats;

import ch.zhaw.threatmodeling.App;
import ch.zhaw.threatmodeling.controller.MainController;
import ch.zhaw.threatmodeling.skin.connection.DataFlowConnectionSkin;
import ch.zhaw.threatmodeling.skin.controller.DataFlowDiagramSkinController;
import ch.zhaw.threatmodeling.skin.controller.DoController;
import ch.zhaw.threatmodeling.skin.utils.DataFlowConnectionCommands;
import ch.zhaw.threatmodeling.testUtils.ModelUtils;
import de.tesis.dynaware.grapheditor.SelectionManager;
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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.assertj.core.api.Assertions.assertThat;

public class ThreatGeneratorTest extends ApplicationTest {
    private static final Logger LOGGER = Logger.getLogger("Threat Geneartor Test");
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
    void testDataStoreToDatastoreThreats() {
        assertNotNull(doController);
        GModel model = skinController.getGraphEditor().getModel();
        ThreatGenerator threatGenerator = skinController.getThreatGenerator();
        List<GConnection> connections = model.getConnections();
        List<GNode> nodes = model.getNodes();
        interact(() -> {
            mainController.addDataStore();
            mainController.addDataStore();
        });

        GNode node1 = nodes.get(0);
        GNode node2 = nodes.get(1);

        // Add a dataflow connection
        List<GJoint> joints = new ArrayList<>();
        joints.add(ModelUtils.createJoint());
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

        // Analyse threats
        threatGenerator.generateAllThreats();
        Threats threats = threatGenerator.getThreats();

        assertEquals(5, threats.size());
        assertThat(threats.all()).extracting("title") .contains(
                "Spoofing of Source Data Store Data Store",
                "Spoofing of Destination Data Store Data Store",
                "Authenticated Data Flow Compromised",
                "Weak Authentication Scheme",
                "Weakness in SSO Authorization"
        );
    }

    @Test
    void testDataStoreToExternalEntityThreats() {
        assertNotNull(doController);
        GModel model = skinController.getGraphEditor().getModel();
        ThreatGenerator threatGenerator = skinController.getThreatGenerator();
        List<GConnection> connections = model.getConnections();
        List<GNode> nodes = model.getNodes();
        interact(() -> {
            mainController.addDataStore();
            mainController.addExternalEntity();
        });

        GNode node1 = nodes.get(0);
        GNode node2 = nodes.get(1);

        // Add a dataflow connection
        List<GJoint> joints = new ArrayList<>();
        joints.add(ModelUtils.createJoint());
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

        // Analyse threats
        threatGenerator.generateAllThreats();
        Threats threats = threatGenerator.getThreats();

        assertEquals(5, threats.size());
        assertThat(threats.all()).extracting("title") .contains(
                "Spoofing of Source Data Store Data Store",
                "Authenticated Data Flow Compromised",
                "Weak Access Control for a Resource",
                "Weak Authentication Scheme",
                "Weakness in SSO Authorization"
        );
    }

    @Test
    void testDataStoreToProcessThreats() {
        assertNotNull(doController);
        GModel model = skinController.getGraphEditor().getModel();
        ThreatGenerator threatGenerator = skinController.getThreatGenerator();
        List<GConnection> connections = model.getConnections();
        List<GNode> nodes = model.getNodes();
        interact(() -> {
            mainController.addDataStore();
            mainController.addProcess();
        });

        GNode node1 = nodes.get(0);
        GNode node2 = nodes.get(1);

        // Add a dataflow connection
        List<GJoint> joints = new ArrayList<>();
        joints.add(ModelUtils.createJoint());
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

        // Analyse threats
        threatGenerator.generateAllThreats();
        Threats threats = threatGenerator.getThreats();

        assertEquals(10, threats.size());
        assertThat(threats.all()).extracting("title") .contains(
                "Spoofing of Source Data Store Data Store",
                "Risks from Logging",
                "Authenticated Data Flow Compromised",
                "XML DTD and XSLT Processing",
                "JavaScript Object Notation Processing",
                "Cross Site Scripting",
                "Persistent Cross Site Scripting",
                "Weak Access Control for a Resource",
                "Weak Authentication Scheme",
                "Weakness in SSO Authorization"
        );
    }

    @Test
    void testExternalEntityToExternalEntityThreats() {
        assertNotNull(doController);
        GModel model = skinController.getGraphEditor().getModel();
        ThreatGenerator threatGenerator = skinController.getThreatGenerator();
        List<GConnection> connections = model.getConnections();
        List<GNode> nodes = model.getNodes();
        interact(() -> {
            mainController.addExternalEntity();
            mainController.addExternalEntity();
        });

        GNode node1 = nodes.get(0);
        GNode node2 = nodes.get(1);

        // Add a dataflow connection
        List<GJoint> joints = new ArrayList<>();
        joints.add(ModelUtils.createJoint());
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

        // Analyse threats
        threatGenerator.generateAllThreats();
        Threats threats = threatGenerator.getThreats();

        assertEquals(3, threats.size());
        assertThat(threats.all()).extracting("title") .contains(
                "Authenticated Data Flow Compromised",
                "Weak Authentication Scheme",
                "Weakness in SSO Authorization"
        );
    }

    @Test
    void testExternalEntityToDataStoreThreats() {
        assertNotNull(doController);
        GModel model = skinController.getGraphEditor().getModel();
        ThreatGenerator threatGenerator = skinController.getThreatGenerator();
        List<GConnection> connections = model.getConnections();
        List<GNode> nodes = model.getNodes();
        interact(() -> {
            mainController.addExternalEntity();
            mainController.addDataStore();
        });

        GNode node1 = nodes.get(0);
        GNode node2 = nodes.get(1);

        // Add a dataflow connection
        List<GJoint> joints = new ArrayList<>();
        joints.add(ModelUtils.createJoint());
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

        // Analyse threats
        threatGenerator.generateAllThreats();
        Threats threats = threatGenerator.getThreats();

        assertEquals(4, threats.size());
        assertThat(threats.all()).extracting("title") .contains(
                "Spoofing of Destination Data Store Data Store",
                "Authenticated Data Flow Compromised",
                "Weak Authentication Scheme",
                "Weakness in SSO Authorization"
        );
    }

    @Test
    void testExternalEntityToProcessThreats() {
        assertNotNull(doController);
        GModel model = skinController.getGraphEditor().getModel();
        ThreatGenerator threatGenerator = skinController.getThreatGenerator();
        List<GConnection> connections = model.getConnections();
        List<GNode> nodes = model.getNodes();
        interact(() -> {
            mainController.addExternalEntity();
            mainController.addProcess();
        });

        GNode node1 = nodes.get(0);
        GNode node2 = nodes.get(1);

        // Add a dataflow connection
        List<GJoint> joints = new ArrayList<>();
        joints.add(ModelUtils.createJoint());
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

        // Analyse threats
        threatGenerator.generateAllThreats();
        Threats threats = threatGenerator.getThreats();

        assertEquals(8, threats.size());
        assertThat(threats.all()).extracting("title") .contains(
                "Spoofing the External Entity External Entity",
                "Authenticated Data Flow Compromised",
                "XML DTD and XSLT Processing",
                "JavaScript Object Notation Processing",
                "Cross Site Scripting",
                "Weak Authentication Scheme",
                "Weakness in SSO Authorization",
                "Elevation Using Impersonation"
        );
    }

    @Test
    void testProcessToProcessThreats() {
        assertNotNull(doController);
        GModel model = skinController.getGraphEditor().getModel();
        ThreatGenerator threatGenerator = skinController.getThreatGenerator();
        List<GConnection> connections = model.getConnections();
        List<GNode> nodes = model.getNodes();
        interact(() -> {
            mainController.addProcess();
            mainController.addProcess();
        });

        GNode node1 = nodes.get(0);
        GNode node2 = nodes.get(1);

        // Add a dataflow connection
        List<GJoint> joints = new ArrayList<>();
        joints.add(ModelUtils.createJoint());
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

        // Analyse threats
        threatGenerator.generateAllThreats();
        Threats threats = threatGenerator.getThreats();

        assertEquals(9, threats.size());
        assertThat(threats.all()).extracting("title") .contains(
                "Replay Attacks",
                "Collision Attacks",
                "Authenticated Data Flow Compromised",
                "XML DTD and XSLT Processing",
                "JavaScript Object Notation Processing",
                "Cross Site Scripting",
                "Weak Authentication Scheme",
                "Weakness in SSO Authorization",
                "Elevation Using Impersonation"
        );
    }


    @Test
    void testProcessToDataStoreThreats() {
        assertNotNull(doController);
        GModel model = skinController.getGraphEditor().getModel();
        ThreatGenerator threatGenerator = skinController.getThreatGenerator();
        List<GConnection> connections = model.getConnections();
        List<GNode> nodes = model.getNodes();
        interact(() -> {
            mainController.addProcess();
            mainController.addDataStore();
        });

        GNode node1 = nodes.get(0);
        GNode node2 = nodes.get(1);

        // Add a dataflow connection
        List<GJoint> joints = new ArrayList<>();
        joints.add(ModelUtils.createJoint());
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

        // Analyse threats
        threatGenerator.generateAllThreats();
        Threats threats = threatGenerator.getThreats();

        assertEquals(10, threats.size());
        assertThat(threats.all()).extracting("title") .contains(
                "Spoofing of Destination Data Store Data Store",
                "Risks from Logging",
                "Authenticated Data Flow Compromised",
                "Insufficient Auditing",
                "Potential Weak Protections for Audit Data",
                "Authorization Bypass",
                "Weak Credential Storage",
                "Weak Authentication Scheme",
                "Potential Excessive Resource Consumption for Process or Data Store",
                "Weakness in SSO Authorization"
        );
    }

    @Test
    void testProcessToExternalEntityThreats() {
        assertNotNull(doController);
        GModel model = skinController.getGraphEditor().getModel();
        ThreatGenerator threatGenerator = skinController.getThreatGenerator();
        List<GConnection> connections = model.getConnections();
        List<GNode> nodes = model.getNodes();
        interact(() -> {
            mainController.addProcess();
            mainController.addExternalEntity();
        });

        GNode node1 = nodes.get(0);
        GNode node2 = nodes.get(1);

        // Add a dataflow connection
        List<GJoint> joints = new ArrayList<>();
        joints.add(ModelUtils.createJoint());
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

        // Analyse threats
        threatGenerator.generateAllThreats();
        Threats threats = threatGenerator.getThreats();

        assertEquals(3, threats.size());
        assertThat(threats.all()).extracting("title") .contains(
                "Authenticated Data Flow Compromised",
                "Weak Authentication Scheme",
                "Weakness in SSO Authorization"
        );
    }

    @AfterEach
    public void tearDown() throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }
}
