package ch.zhaw.threatmodeling.selections;

import ch.zhaw.threatmodeling.selections.utils.ConnectionMaps;
import ch.zhaw.threatmodeling.skin.joint.DataFlowJointSkin;
import de.tesis.dynaware.grapheditor.SkinLookup;
import de.tesis.dynaware.grapheditor.model.GConnection;
import de.tesis.dynaware.grapheditor.model.GConnector;
import de.tesis.dynaware.grapheditor.model.GJoint;
import de.tesis.dynaware.grapheditor.model.GNode;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.util.HashMap;
import java.util.Map;

public class DataFlowConnectionCopier {
    private DataFlowConnectionCopier() {
    }

    public static void copyConnections(Map<GNode, GNode> copies, ConnectionMaps maps, SkinLookup skinLookup) {
        Map<GConnection, GConnection> copiedConnections = new HashMap<>();
        for (GNode node : copies.keySet()) {
            GNode copy = copies.get(node);

            for (GConnector connector : node.getConnectors()) {
                int connectorIndex = node.getConnectors().indexOf(connector);
                GConnector copiedConnector = copy.getConnectors().get(connectorIndex);
                copiedConnector.getConnections().clear();

                for (GConnection connection : connector.getConnections()) {
                    GNode opposingNode = getOpposingNode(connector, connection);
                    boolean opposingNodePresent = copies.containsKey(opposingNode);
                    if (opposingNodePresent) {
                        copiedConnector.getConnections().add(copyConnection(maps, skinLookup, copiedConnections, connector, copiedConnector, connection));
                    }
                }
            }
        }
    }

    private static GConnection copyConnection(ConnectionMaps maps, SkinLookup skinLookup, Map<GConnection, GConnection> copiedConnections, GConnector connector, GConnector copiedConnector, GConnection connection) {
        GConnection copiedConnection;
        if (!copiedConnections.containsKey(connection)) {

            copiedConnection = EcoreUtil.copy(connection);
            copiedConnections.put(connection, copiedConnection);
            String type;
            String text;
            if (maps.getConnectionTypeTextMap().containsKey(connection)) {
                type = maps.getConnectionType(connection);
                text = maps.getConnectionText(connection);
                maps.getConnectionTypeTextMap().remove(connection);
            } else {
                type = getConnectionType(connection, skinLookup);
                text = getConnectionText(connection, skinLookup);
            }
            maps.putConnection(copiedConnection, type, text);

        } else {
            copiedConnection = copiedConnections.get(connection);
        }

        if (connection.getSource().equals(connector)) {
            copiedConnection.setSource(copiedConnector);
        } else {
            copiedConnection.setTarget(copiedConnector);
        }
        return copiedConnection;
    }

    private static String getConnectionType(GConnection connection, SkinLookup skinLookup) {
        //should be expanded if more connection types exist
        return DataFlowJointSkin.ELEMENT_TYPE;
    }

    private static String getConnectionText(GConnection connection, SkinLookup skinLookup) {
        //should be expanded if more connection types exist
        GJoint joint = connection.getJoints().get(0);
        DataFlowJointSkin jointSkin = (DataFlowJointSkin) skinLookup.lookupJoint(joint);
        return jointSkin.getText();
    }

    private static GNode getOpposingNode(GConnector connector, GConnection connection) {
        GConnector opposingConnector;
        if (connection.getSource().equals(connector)) {
            opposingConnector = connection.getTarget();
        } else {
            opposingConnector = connection.getSource();
        }

        return opposingConnector != null && opposingConnector.getParent() != null ? opposingConnector.getParent() : null;
    }
}
