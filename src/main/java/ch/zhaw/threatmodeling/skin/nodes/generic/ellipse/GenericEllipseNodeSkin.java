package ch.zhaw.threatmodeling.skin.nodes.generic.ellipse;

import ch.zhaw.threatmodeling.skin.DataFlowElement;
import ch.zhaw.threatmodeling.skin.DataFlowSkinConstants;
import ch.zhaw.threatmodeling.skin.nodes.generic.GenericNodeSkin;
import de.tesis.dynaware.grapheditor.GConnectorSkin;
import de.tesis.dynaware.grapheditor.model.GNode;
import de.tesis.dynaware.grapheditor.utils.DraggableBox;
import javafx.geometry.Point2D;
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
        for (GConnectorSkin skin : allConnectors) {
            final Node skinRoot = skin.getRoot();
            final double offsetX = -skin.getWidth() / 2;
            final double offsetY = -skin.getHeight() / 2;
            Point2D coords = calculateConnectorPosition(skin.getItem().getType(), offsetX, offsetY);
            skinRoot.setLayoutX(coords.getX());
            skinRoot.setLayoutY(coords.getY());
        }

    }

    private Point2D calculateConnectorPosition(String connectorType, double offsetX, double offsetY) {
        //formula from here:
        //https://mathopenref.com/coordparamellipse.html
        final double incrementRadian = 2 * Math.PI / DataFlowSkinConstants.DFD_CONNECTOR_LAYOUT_ORDER.size();
        final double startRadian = 2 * Math.PI - incrementRadian;
        final int indexOfType = DataFlowSkinConstants.DFD_CONNECTOR_LAYOUT_ORDER.indexOf(connectorType);
        final double currentRadian = startRadian - incrementRadian * indexOfType;
        return new Point2D(
                ellipseWithConnectors.getCenterX() + ellipseWithConnectors.getRadiusX() * Math.cos(currentRadian) + offsetX,
                ellipseWithConnectors.getCenterY() + ellipseWithConnectors.getRadiusY() * Math.sin(currentRadian) + offsetY);

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

    @Override
    // Because ellipse nodes have different placements of connectors, this method has to be overwritten
    public Point2D getConnectorPosition(GConnectorSkin connectorSkin) {
        return calculateConnectorPosition(connectorSkin.getItem().getType(), 0, 0);
    }
}
