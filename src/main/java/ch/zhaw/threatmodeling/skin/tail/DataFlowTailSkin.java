package ch.zhaw.threatmodeling.skin.tail;

import ch.zhaw.threatmodeling.connectors.DataFlowConnectorTypes;
import ch.zhaw.threatmodeling.skin.DataFlowSkinConstants;
import ch.zhaw.threatmodeling.skin.utils.ArrowUtils;
import de.tesis.dynaware.grapheditor.GTailSkin;
import de.tesis.dynaware.grapheditor.model.GConnector;
import de.tesis.dynaware.grapheditor.utils.Arrow;
import javafx.geometry.Point2D;
import javafx.scene.Node;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;

public class DataFlowTailSkin extends GTailSkin {

    private static final Logger LOGGER = Logger.getLogger("Data Flow Tail Skin");

    private static final String STYLE_CLASS = "data-flow-tail"; //$NON-NLS-1$

    private static final double ARROW_LENGTH = 16;
    private static final double ARROW_WIDTH = 10;
    private static final double OFFSET_DISTANCE = 0;

    protected final Arrow arrow = new Arrow();

    /**
     * Creates a new data flow tail skin instance.
     * <p>
     * This is simply an arrow (straight line with arrowhead) from the start point (start connector) to the mouse
     * cursor, which is shown while dragging the tail to the end connector.
     *
     * @param connector the {@link GConnector} the skin is being created for
     */
    public DataFlowTailSkin(final GConnector connector) {

        super(connector);

        performChecks();

        arrow.setHeadLength(ARROW_LENGTH);
        arrow.setHeadWidth(ARROW_WIDTH);
        arrow.getStyleClass().setAll(STYLE_CLASS);

    }

    @Override
    public Node getRoot() {
        return arrow;
    }

    @Override
    public void draw(final Point2D start, final Point2D end) {
        ArrowUtils.draw(arrow, start, end, OFFSET_DISTANCE);
    }

    @Override
    public void draw(final Point2D start, final Point2D end, final GConnector target, final boolean valid) {
        draw(start, end);
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
        // Although the joint cannot be seen or manipulated yet while dragging the tail, we already need to define
        // where the joint will be when the actual DataFlowConnection is created.

        final List<Point2D> jointPositions = new ArrayList<>();

        final Point2D startPoint = arrow.getStart();
        final Point2D endPoint = arrow.getEnd();

        Point2D midPoint = startPoint.midpoint(endPoint);

        // For some reason this joint position is offset by -22.5 pixels in each direction, which we have to correct.
        midPoint = midPoint.add(DataFlowSkinConstants.DFD_JOINT_SPAWN_OFFSET, DataFlowSkinConstants.DFD_JOINT_SPAWN_OFFSET);

        // The first joint position should be the mid point of the Bezier curve
        jointPositions.add(midPoint);

        return jointPositions;
    }

    /**
     * Checks that the connector has the correct values to use this skin.
     */
    private void performChecks() {
        if (!DataFlowConnectorTypes.isValid(getItem().getType())) {
            LOGGER.log(Level.INFO, "Connector type '{}' not recognized, setting to 'right'.", getItem().getType());
            getItem().setType(DataFlowConnectorTypes.RIGHT);
        }
    }

    @Override
    protected void selectionChanged(boolean isSelected) {
        // Not implemented
    }

}
