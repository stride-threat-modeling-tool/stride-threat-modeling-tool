package ch.zhaw.threatmodeling.persistence.utils;

import ch.zhaw.threatmodeling.model.threats.Threat;
import ch.zhaw.threatmodeling.persistence.utils.objects.DataFlowConnectionObject;
import ch.zhaw.threatmodeling.persistence.utils.objects.DataFlowNodeObject;
import ch.zhaw.threatmodeling.persistence.utils.objects.DataFlowPositionedObject;
import ch.zhaw.threatmodeling.persistence.utils.objects.ThreatObject;
import ch.zhaw.threatmodeling.skin.nodes.generic.GenericNodeSkin;
import ch.zhaw.threatmodeling.skin.utils.DataFlowConnectionCommands;
import de.tesis.dynaware.grapheditor.SkinLookup;
import de.tesis.dynaware.grapheditor.model.GConnection;
import de.tesis.dynaware.grapheditor.model.GConnector;
import de.tesis.dynaware.grapheditor.model.GJoint;
import de.tesis.dynaware.grapheditor.model.GModel;
import de.tesis.dynaware.grapheditor.model.GNode;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class JSONPreparatory {
    private JSONPreparatory() {
    }

    public static List<DataFlowNodeObject> createSavableNodes(GModel model, SkinLookup skinLookup) {
        List<DataFlowNodeObject> serializableObjects = new ArrayList<>();
        model.getNodes().forEach(node -> serializableObjects.add(translateToSavableNode(node, skinLookup)));
        return serializableObjects;
    }

    public static List<DataFlowConnectionObject> createSavableConnections(GModel model, SkinLookup skinLookup) {
        List<DataFlowConnectionObject> serializableObjects = new ArrayList<>();
        model.getConnections().forEach(con -> serializableObjects.add(translateToSavableConnection(con, skinLookup, model.getNodes())));
        return serializableObjects;
    }

    public static List<ThreatObject> createSavableThreats(GModel model, List<Threat> threats){
        List<ThreatObject> serializableThreats = new ArrayList<>();
        List<GNode> nodes = model.getNodes();
        List<GConnection> connections = model.getConnections();
        threats.forEach(threat -> serializableThreats.add(translateToSavableThreat(threat, nodes, connections)));
        return serializableThreats;
    }

    private static DataFlowConnectionObject translateToSavableConnection(GConnection connection, SkinLookup skinLookup, List<GNode> nodes) {
        final Pair<String, String> typeTextPair = DataFlowConnectionCommands.getTypeAndJointLabel(connection, skinLookup);
        final GConnector srcConnector = connection.getSource();
        final GConnector destConnector = connection.getTarget();
        final GNode srcNode = srcConnector.getParent();
        final GNode destNode = destConnector.getParent();
        final int srcNodeIndex = nodes.indexOf(srcNode);
        final int destNodeIndex = nodes.indexOf(destNode);
        final int srcConnectorIndex = srcNode.getConnectors().indexOf(srcConnector);
        final int destConnectorIndex = destNode.getConnectors().indexOf(destConnector);
        final DataFlowPositionedObject joint = translateToSavableJoint(typeTextPair, connection.getJoints().get(0));
        return new DataFlowConnectionObject(
                typeTextPair.getKey(),
                srcNodeIndex,
                destNodeIndex,
                srcConnectorIndex,
                destConnectorIndex,
                joint
        );
    }

    private static DataFlowPositionedObject translateToSavableJoint(Pair<String, String> typeTextPair, GJoint gJoint) {
        return new DataFlowPositionedObject(typeTextPair.getKey(), typeTextPair.getValue(), gJoint.getX(), gJoint.getY());
    }

    private static DataFlowNodeObject translateToSavableNode(GNode node, SkinLookup skinLookup) {
        GenericNodeSkin skin = (GenericNodeSkin) skinLookup.lookupNode(node);
        return new DataFlowNodeObject(skin.getType(), skin.getText(), node.getX(), node.getY(), node.getWidth(), node.getHeight());
    }

    private static ThreatObject translateToSavableThreat(Threat threat, List<GNode> nodes, List<GConnection> connections){
        return new ThreatObject(
          threat.getId(),
          threat.getState(),
          threat.getTitle(),
          threat.getTitleTemplate(),
          threat.getDescriptionTemplate(),
          threat.getCategory(),
          threat.getDescription(),
          threat.getJustification(),
          threat.getPriority(),
          connections.indexOf(threat.getConnection()),
          nodes.indexOf(threat.getNodeName1().getNode()),
          nodes.indexOf(threat.getNodeName2().getNode())
        );
    }


}
