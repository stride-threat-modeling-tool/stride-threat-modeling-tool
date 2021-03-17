package ch.zhaw.skin;

import ch.zhaw.connectors.DataFlowConnectorTypes;
import de.tesis.dynaware.grapheditor.GTailSkin;
import de.tesis.dynaware.grapheditor.core.skins.defaults.tail.RectangularPathCreator;
import de.tesis.dynaware.grapheditor.model.GConnector;
import de.tesis.dynaware.grapheditor.utils.GeometryUtils;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;

public class DataFlowTailSkin extends GTailSkin {

    private static final Logger LOGGER = Logger.getLogger("DataFlowTailSkin");

    private static final double ENDPOINT_SIZE = 25;

    private static final String STYLE_CLASS = "titled-tail"; //$NON-NLS-1$
    private static final String STYLE_CLASS_ENDPOINT = "titled-tail-endpoint"; //$NON-NLS-1$

    private static final double SIZE = 15;

    protected final Polyline line = new Polyline();
    protected final Polygon endpoint = new Polygon();
    protected final Group group = new Group(line, endpoint);

    /**
     * Creates a new default tail skin instance.
     *
     * @param connector the {@link GConnector} the skin is being created for
     */
    public DataFlowTailSkin(final GConnector connector) {

        super(connector);

        performChecks();

        line.getStyleClass().setAll(STYLE_CLASS);
        endpoint.getStyleClass().setAll(STYLE_CLASS_ENDPOINT);
        // 4 points (x,y) that make up a square with side length SIZE
        endpoint.getPoints().setAll(0D, 0D, 0D, SIZE, SIZE, SIZE, SIZE, 0D);

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
        drawStupid(start, end);
    }

    @Override
    public void draw(final Point2D start, final Point2D end, final GConnector target, final boolean valid) {

        endpoint.setVisible(false);
        if (valid) {
            drawSmart(start, end, target);
        } else {
            drawStupid(start, end);
        }
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

        for (int i = 2; i < line.getPoints().size() - 2; i = i + 2) {

            final double x = GeometryUtils.moveOnPixel(line.getPoints().get(i));
            final double y = GeometryUtils.moveOnPixel(line.getPoints().get(i + 1));

            jointPositions.add(new Point2D(x, y));
        }

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
     * @param start the start position of the tail
     * @param end the end position of the tail
     */
    private void drawStupid(final Point2D start, final Point2D end) {

        clearPoints();
        addPoint(start);

        if (DataFlowConnectorTypes.getSide(getItem().getType()).isVertical()) {
            addPoint((start.getX() + end.getX()) / 2, start.getY());
            addPoint((start.getX() + end.getX()) / 2, end.getY());
        } else {
            addPoint(start.getX(), (start.getY() + end.getY()) / 2);
            addPoint(end.getX(), (start.getY() + end.getY()) / 2);
        }

        addPoint(end);
    }

    /**
     * Draws the tail based additionally on the sides of the nodes it starts and ends at.
     *
     * @param start the start position of the tail
     * @param end the end position of the tail
     * @param target the connector the tail is attaching to
     */
    private void drawSmart(final Point2D start, final Point2D end, final GConnector target) {


        clearPoints();
        addPoint(start);

        final Side startSide = DataFlowConnectorTypes.getSide(getItem().getType());
        final Side endSide = DataFlowConnectorTypes.getSide(target.getType());

        final List<Point2D> points = RectangularPathCreator.createPath(start, end, startSide, endSide);
        points.stream().forEachOrdered(point -> addPoint(point));

        addPoint(end);
    }

    /**
     * Clears all the points from the tail path.
     */
    private void clearPoints() {
        line.getPoints().clear();
    }

    /**
     * Adds the given point to the tail path.
     *
     * @param point the x & y coordinates of the point
     */
    private void addPoint(final Point2D point) {
        addPoint(point.getX(), point.getY());
    }

    /**
     * Adds the given point to the tail path.
     *
     * @param x the x coordinate of the point
     * @param y the y coordinate of the point
     */
    private void addPoint(final double x, final double y) {
        line.getPoints().addAll(GeometryUtils.moveOffPixel(x), GeometryUtils.moveOffPixel(y));
    }

    @Override
    protected void selectionChanged(boolean isSelected) {
        // Not implemented
    }

}
