package ch.zhaw.threatmodeling.skin.nodes.process;

import ch.zhaw.threatmodeling.skin.DataFlowElement;
import ch.zhaw.threatmodeling.skin.nodes.generic.GenericNodeSkin;
import de.tesis.dynaware.grapheditor.GConnectorSkin;
import de.tesis.dynaware.grapheditor.model.GNode;
import javafx.geometry.Point2D;

public class ProcessNodeSkin extends GenericNodeSkin implements DataFlowElement {

    public ProcessNodeSkin(GNode node) {
        super(node);
    }

    @Override
    public void layoutConnectors() {

    }

    @Override
    public Point2D getConnectorPosition(GConnectorSkin gConnectorSkin) {
        return null;
    }

    @Override
    protected void selectionChanged(boolean b) {

    }

    @Override
    protected void createContent() {

    }
}
