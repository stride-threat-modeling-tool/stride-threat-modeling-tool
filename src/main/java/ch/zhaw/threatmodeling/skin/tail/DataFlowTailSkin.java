package ch.zhaw.threatmodeling.skin.tail;

import ch.zhaw.threatmodeling.connectors.DataFlowConnectorTypes;
import de.tesis.dynaware.grapheditor.GTailSkin;
import de.tesis.dynaware.grapheditor.model.GConnector;
import de.tesis.dynaware.grapheditor.utils.GeometryUtils;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.QuadCurve;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;

public class DataFlowTailSkin extends GTailSkin {

    private static final Logger LOGGER = Logger.getLogger("Data Flow Tail Skin");

    private static final double ENDPOINT_SIZE = 25;

    private static final String STYLE_CLASS_CURVE = "data-flow-tail-curve"; //$NON-NLS-1$
    private static final String STYLE_CLASS_ENDPOINT = "data-flow-tail-endpoint"; //$NON-NLS-1$

    private static final double SIZE = 15;

    protected final Polygon endpoint = new Polygon();
    protected final QuadCurve curve = new QuadCurve();
    protected final Group group = new Group(curve, endpoint);

    /**
     * Creates a new data flow tail skin instance.
     *
     * @param connector the {@link GConnector} the skin is being created for
     */
    public DataFlowTailSkin(final GConnector connector) {

        super(connector);

        performChecks();

        endpoint.getStyleClass().setAll(STYLE_CLASS_ENDPOINT);
        // 4 points (x,y) that make up a square with side length SIZE
        endpoint.getPoints().setAll(0D, 0D, 0D, SIZE, SIZE, SIZE, SIZE, 0D);

        curve.getStyleClass().setAll(STYLE_CLASS_CURVE);
        curve.setFill(null);

        group.setManaged(false);
    }

    @Override
    public Node getRoot() {
        return group;
    }

    @Override
    public void draw(final Point2D start, final Point2D end) {

        endpoint.setVisible(true);
        layoutEndpoint(end);
        drawStraightBezier(start, end);
    }

    @Override
    public void draw(final Point2D start, final Point2D end, final GConnector target, final boolean valid) {

        endpoint.setVisible(false);
        drawStraightBezier(start, end);
    }

    @Override
    public void draw(final Point2D start, final Point2D end, final List<Point2D> jointPositions) {
        draw(start, end);
    }

    @Override
    public void draw(final Point2D start, final Point2D end, final List<Point2D> jointPositions,
                     final GConnector target, final boolean valid) {
        draw(start, end, target, valid);
    }

    @Override
    public List<Point2D> allocateJointPositions() {

        final List<Point2D> jointPositions = new ArrayList<>();

        final Point2D startPoint = new Point2D(curve.getStartX(), curve.getStartY());
        final Point2D endPoint = new Point2D(curve.getEndX(), curve.getEndY());

        Point2D midPoint = startPoint.midpoint(endPoint);

        // For some reason this joint position is offset by -22.5 pixels in each direction
        midPoint = midPoint.add(22.5, 22.5);

        // The first joint position should be the mid point of the Bezier curve
        jointPositions.add(midPoint);

        return jointPositions;
    }

    /**
     * Sets layout values of the endpoint based on the new cursor position.
     *
     * @param position the new cursor position
     */
    protected void layoutEndpoint(final Point2D position) {
        endpoint.setLayoutX(GeometryUtils.moveOnPixel(position.getX() - ENDPOINT_SIZE / 2));
        endpoint.setLayoutY(GeometryUtils.moveOnPixel(position.getY() - ENDPOINT_SIZE / 2));
    }

    /**
     * Checks that the connector has the correct values to use this skin.
     */
    private void performChecks()
    {
        if (!DataFlowConnectorTypes.isValid(getItem().getType()))
        {
            LOGGER.log(Level.INFO, "Connector type '{}' not recognized, setting to 'right'.", getItem().getType());
            getItem().setType(DataFlowConnectorTypes.RIGHT);
        }
    }

    /**
     * Draws the tail simply from the start position to the end.
     *
     * <p>
     * Since the Bezier curve control point cannot be dragged yet when creating a new connection, we construct a
     * Bezier curve that is straight line at first.
     * </p>
     *
     * @param start the start position of the tail
     * @param end the end position of the tail
     */
    private void drawStraightBezier(final Point2D start, final Point2D end) {

        final Point2D midPoint = start.midpoint(end);

        // Start point of curve
        curve.setStartX(start.getX());
        curve.setStartY(start.getY());

        // For the start, it is enough to set the control point equal to the joint position (on curve), as the control
        // point is normally way off the Bezier curve when the user starts dragging the joint around.
        final Point2D controlPoint = midPoint;

        // Add control point in between start and end of curve
        curve.setControlX(controlPoint.getX());
        curve.setControlY(controlPoint.getY());

        // End point of curve
        curve.setEndX(end.getX());
        curve.setEndY(end.getY());
    }

    @Override
    protected void selectionChanged(boolean isSelected) {
        // Not implemented
    }

}
