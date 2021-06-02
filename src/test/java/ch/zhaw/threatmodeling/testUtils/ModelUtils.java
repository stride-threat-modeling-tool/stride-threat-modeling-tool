package ch.zhaw.threatmodeling.testUtils;

import ch.zhaw.threatmodeling.skin.connection.DataFlowConnectionSkin;
import ch.zhaw.threatmodeling.skin.controller.DataFlowDiagramSkinController;
import ch.zhaw.threatmodeling.skin.utils.DataFlowConnectionCommands;
import ch.zhaw.threatmodeling.skin.utils.DataFlowNodeCommands;
import de.tesis.dynaware.grapheditor.GraphEditor;
import de.tesis.dynaware.grapheditor.SkinLookup;
import de.tesis.dynaware.grapheditor.model.*;
import org.testfx.api.FxRobot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ModelUtils {
    public static void addConnections(int n, DataFlowDiagramSkinController skinController, FxRobot robot) {
        final GraphEditor editor = skinController.getGraphEditor();
        final GModel model = editor.getModel();
        final List<GNode> nodes = model.getNodes();
        final SkinLookup skinLookup = editor.getSkinLookup();
        final Random rmd = new Random();
        final int bound = 500;
        robot.interact(() -> {
            for (int i = 0; i < n; i++) {
                GNode srcNode;
                GNode targetNode;
                GConnector srcCon;
                GConnector targetCon;
                do {
                    srcNode = nodes.get(rmd.nextInt(nodes.size()));
                    targetNode = nodes.get(rmd.nextInt(nodes.size()));
                    srcCon = srcNode.getConnectors().get(rmd.nextInt(srcNode.getConnectors().size()));
                    targetCon = targetNode.getConnectors().get(rmd.nextInt(targetNode.getConnectors().size()));
                } while (srcCon == targetCon ||
                        DataFlowNodeCommands.isTrustBoundaryNode(srcNode, skinLookup) ||
                        DataFlowNodeCommands.isTrustBoundaryNode(targetNode, skinLookup));
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

    public static GJoint createJoint() {
        return GraphFactory.eINSTANCE.createGJoint();
    }
}
