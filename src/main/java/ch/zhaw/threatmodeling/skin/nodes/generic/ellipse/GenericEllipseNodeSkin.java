package ch.zhaw.threatmodeling.skin.nodes.generic.ellipse;

import ch.zhaw.threatmodeling.skin.DataFlowElement;
import ch.zhaw.threatmodeling.skin.connector.DataFlowConnectorSkin;
import ch.zhaw.threatmodeling.skin.nodes.generic.GenericNodeSkin;
import de.tesis.dynaware.grapheditor.GConnectorSkin;
import de.tesis.dynaware.grapheditor.model.GNode;
import de.tesis.dynaware.grapheditor.utils.DraggableBox;
import javafx.scene.Node;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.StrokeType;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public abstract class GenericEllipseNodeSkin extends GenericNodeSkin implements DataFlowElement {
    protected static final Paint ELLIPSE_FILL_COLOR = Paint.valueOf("white");
    protected static final Paint ELLIPSE_STROKE_COLOR = Paint.valueOf("black");
    protected static final double STROKE_WIDTH = 1.5;
    private static final Logger LOGGER = Logger.getLogger("Generic Ellipse");
    protected Ellipse ellipseWithConnectors;

    protected GenericEllipseNodeSkin(GNode node) {
        super(node);
    }

    @Override
    public void layoutConnectors() {
        List<GConnectorSkin> allConnectors = new ArrayList<>(rightConnectorSkins);
        allConnectors.addAll(bottomConnectorSkins);
        allConnectors.addAll(leftConnectorSkins);
        allConnectors.addAll(topConnectorSkins);
        final double offset = -DataFlowConnectorSkin.SIZE / 2.0;
        //formula from here:
        //https://mathopenref.com/coordparamellipse.html

        final double incrementRadian = 2 * Math.PI / allConnectors.size();
        double currentRadian = 2 * Math.PI - incrementRadian;
        for (GConnectorSkin skin: allConnectors) {
            final Node skinRoot = skin.getRoot();
            final double x = ellipseWithConnectors.getCenterX() + ellipseWithConnectors.getRadiusX() * Math.cos(currentRadian) + offset;
            final double y = ellipseWithConnectors.getCenterY() + ellipseWithConnectors.getRadiusY() * Math.sin(currentRadian)+ offset;
            skinRoot.setLayoutX(x);
            skinRoot.setLayoutY(y);
            currentRadian += incrementRadian;
        }

    }

    protected void setEllipseProperties(Ellipse ellipse) {
        ellipse.setStroke(ELLIPSE_STROKE_COLOR);
        ellipse.setFill(ELLIPSE_FILL_COLOR);
        ellipse.setStrokeType(StrokeType.INSIDE);
        ellipse.setStrokeWidth(STROKE_WIDTH);
    }

    protected void bindEllipseToRoot(Ellipse ellipse, DraggableBox root) {
        ellipse.centerXProperty().bind(root.widthProperty().divide(2));
        ellipse.centerYProperty().bind(root.heightProperty().divide(2));
        ellipse.radiusXProperty().bind(root.widthProperty().divide(2));
        ellipse.radiusYProperty().bind(root.heightProperty().divide(2));
    }
}
