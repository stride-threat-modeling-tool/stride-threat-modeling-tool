package ch.zhaw.threatmodeling.skin.nodes.generic.ellipse;

import ch.zhaw.threatmodeling.skin.DataFlowElement;
import ch.zhaw.threatmodeling.skin.connector.DataFlowConnectorSkin;
import ch.zhaw.threatmodeling.skin.nodes.generic.GenericNodeSkin;
import de.tesis.dynaware.grapheditor.GConnectorSkin;
import de.tesis.dynaware.grapheditor.model.GNode;
import de.tesis.dynaware.grapheditor.utils.DraggableBox;
import de.tesis.dynaware.grapheditor.utils.GeometryUtils;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.StrokeType;

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
        layoutConnectors(topConnectorSkins, false, 0, false);
        layoutConnectors(rightConnectorSkins, true, getRoot().getWidth(), false);
        layoutConnectors(bottomConnectorSkins, false, getRoot().getHeight(), false);
        layoutConnectors(leftConnectorSkins, true, 0, true);
    }

    private void layoutConnectors(final List<GConnectorSkin> connectorSkins, final boolean vertical, final double offset, final boolean left) {

        final int count = connectorSkins.size();
        final double yOffsetVertical = -DataFlowConnectorSkin.SIZE / 2.0;

        for (int i = 0; i < count; i++) {

            final GConnectorSkin skin = connectorSkins.get(i);
            final Node skinRoot = skin.getRoot();
            if (vertical) {

                final double offsetX = getMinorOffsetX(skin.getItem());

                if (i % 2 != 0) {
                    //center left/right connector
                    skinRoot.setLayoutX(GeometryUtils.moveOnPixel(offset - skin.getWidth() / 2 + offsetX));
                    skinRoot.setLayoutY((getRoot().getHeight() / (count - 1) * i) + yOffsetVertical);
                } else {
                    //corner connectors
                    final Point2D pointToPlaceConnector = calcDiagonalOffset();

                    LOGGER.info("y before " + skinRoot.getLayoutY());
                    LOGGER.info("x before " + skinRoot.getLayoutX());
                    skinRoot.setLayoutY(pointToPlaceConnector.getY() + skinRoot.getLayoutY());
                    skinRoot.setLayoutX(pointToPlaceConnector.getX() + skinRoot.getLayoutX());
                    LOGGER.info("y " + skinRoot.getLayoutY());
                    LOGGER.info("x " + skinRoot.getLayoutX());

                }

            } else {

                final double offsetX = getRoot().getWidth() / (count + 1);
                final double offsetY = getMinorOffsetY(skin.getItem());

                skinRoot.setLayoutX(GeometryUtils.moveOnPixel((i + 1) * offsetX - skin.getWidth() / 2));
                skinRoot.setLayoutY(GeometryUtils.moveOnPixel(offset - skin.getHeight() / 2 + offsetY));
            }
        }
    }

    private Point2D calcDiagonalOffset() {
        final double alpha = Math.toRadians(45);
        final double c = calcRadiusAtInterSection(alpha);
        LOGGER.info("c: " + c);
        final double b = c / (1.0 / Math.cos(alpha)); // sec(alpha)
        final double a = Math.sin(alpha) * c;
        LOGGER.info("b: " + b);
        LOGGER.info("a: " + a);
        return new Point2D(a, b);

    }

    private double calcRadiusAtInterSection(double angleInRadians){
        //formula taken from here.
        // https://math.stackexchange.com/questions/432902/how-to-get-the-radius-of-an-ellipse-at-a-specific-angle-by-knowing-its-semi-majo
        //trigonometry functions do not work with degrees
        double a = ellipseWithConnectors.getRadiusY();
        double b = ellipseWithConnectors.getRadiusX();
        LOGGER.info("a in rad: " + a);
        LOGGER.info("b in rad: " + b);
        return (a * b) / Math.sqrt(
                Math.pow(a , 2) * Math.pow(Math.sin(angleInRadians), 2) + Math.pow(b, 2) * Math.pow(Math.cos(angleInRadians), 2));
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
