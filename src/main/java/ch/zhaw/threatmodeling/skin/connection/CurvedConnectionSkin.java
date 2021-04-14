package ch.zhaw.threatmodeling.skin.connection;

import ch.zhaw.threatmodeling.skin.connector.DataFlowConnectorSkin;
import ch.zhaw.threatmodeling.skin.joint.DataFlowJointSkin;
import de.tesis.dynaware.grapheditor.GConnectionSkin;
import de.tesis.dynaware.grapheditor.GJointSkin;
import de.tesis.dynaware.grapheditor.SkinLookup;
import de.tesis.dynaware.grapheditor.model.GConnection;
import de.tesis.dynaware.grapheditor.model.GConnector;
import de.tesis.dynaware.grapheditor.utils.ArrowHead;
import de.tesis.dynaware.grapheditor.utils.DraggableBox;
import de.tesis.dynaware.grapheditor.utils.GeometryUtils;
import javafx.css.PseudoClass;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static ch.zhaw.threatmodeling.skin.DataFlowSkinConstants.PSEUDO_CLASS_SELECTED;
import static ch.zhaw.threatmodeling.skin.DataFlowSkinConstants.PSEUDO_CLASS_HOVER;

/**
 * A simple curved connection skin.
 *
 * <p>
 * Shows a quadratic Bezier curve connection shape based on the position of its single joint.
 * </p>
 */
public class CurvedConnectionSkin extends GConnectionSkin {

    protected final Group root = new Group();
    protected final Path path = new Path();
    protected final QuadCurveTo curve = new QuadCurveTo();
    protected final ArrowHead arrowHead = new ArrowHead();

    private static final double ARROW_LENGTH = 16;
    private static final double ARROW_WIDTH = 10;

    private static final String STYLE_CLASS = "curved-connection";
    private static final String STYLE_CLASS_ARROW = "curved-connection-arrow";

    private List<GJointSkin> jointSkins;

    private static final Logger LOGGER = Logger.getLogger("Curved Connection Skin");

    /**
     * Creates a new curved connection skin instance.
     *
     * @param connection the {@link GConnection} the skin is being created for
     */
    public CurvedConnectionSkin(final GConnection connection) {

        super(connection);

        root.setManaged(false);

        // ArrowHead at the end the curve
        arrowHead.setLength(ARROW_LENGTH);
        arrowHead.setWidth(ARROW_WIDTH);
        arrowHead.setMouseTransparent(true);

        path.setMouseTransparent(false); // allows connection to be highlighted on hover and select
        path.getStyleClass().setAll(STYLE_CLASS);

        root.getChildren().add(path);
        root.getChildren().add(arrowHead);

        initEventListener();
    }

    private void initEventListener() {
        // Change style of dataflow on mouseover
        path.setOnMouseEntered(mouseEvent -> highlightDataflow());
        path.setOnMouseExited(mouseEvent -> unhighlightDataflow());
    }


    private void highlightDataflow() {
       // Highlight the connection, joint, connectors
        SkinLookup skinLookup = getGraphEditor().getSkinLookup();

        // Connection
        path.pseudoClassStateChanged(PSEUDO_CLASS_HOVER, true);

        // Joint
        final DataFlowJointSkin jointSkin = (DataFlowJointSkin) jointSkins.get(0);
        jointSkin.getRoot().pseudoClassStateChanged(PSEUDO_CLASS_HOVER, true);

        // Connectors
        setConnectorsStyle(skinLookup, this.getItem(), PSEUDO_CLASS_HOVER, true);
    }

    private void unhighlightDataflow() {
        // Unhighlight the connection, joint, connectors
        SkinLookup skinLookup = getGraphEditor().getSkinLookup();

        // Connection
        path.pseudoClassStateChanged(PSEUDO_CLASS_HOVER, false);

        // Joint
        final DataFlowJointSkin jointSkin = (DataFlowJointSkin) jointSkins.get(0);
        jointSkin.getRoot().pseudoClassStateChanged(PSEUDO_CLASS_HOVER, false);

        setConnectorsStyle(skinLookup, this.getItem(), PSEUDO_CLASS_HOVER, false);
    }

    private void setConnectorsStyle(SkinLookup skinLookup, GConnection connection, final PseudoClass pseudoClass, boolean active) {
        final GConnector sourceConnector = connection.getSource();
        final GConnector targetConnector = connection.getTarget();
        final DataFlowConnectorSkin sourceConnectorSkin = (DataFlowConnectorSkin) skinLookup.lookupConnector(sourceConnector);
        final DataFlowConnectorSkin targetConnectorSkin = (DataFlowConnectorSkin) skinLookup.lookupConnector(targetConnector);
        sourceConnectorSkin.getRoot().pseudoClassStateChanged(pseudoClass, active);
        targetConnectorSkin.getRoot().pseudoClassStateChanged(pseudoClass, active);
    }

    @Override
    public Node getRoot() {
        return root;
    }

    @Override
    public void setJointSkins(final List<GJointSkin> jointSkins) {
        this.jointSkins = jointSkins;
    }

    @Override
    public Point2D[] update()
    {
        final Point2D[] points = super.update();
        return points;
    }

    @Override
    public void draw(final Map<GConnectionSkin, Point2D[]> allPoints)
    {
        super.draw(allPoints);

        final Point2D[] points = allPoints == null ? null : allPoints.get(this);
        if (points != null)
        {
            drawCurve(points);
        }
        else
        {
            path.getElements().clear();
        }
    }


    /**
     * Draws the curve for the connection.
     *
     * @param points all points that the connection should pass through (both connector and joint positions)
     */
    private void drawCurve(final Point2D[] points)
    {
        final Point2D startPoint = points[0];
        final double startX = startPoint.getX();
        final double startY = startPoint.getY();

        // points[1] is the old control point which we ignore since the new control point is only decided by the
        // joint position
        final Point2D endPoint = points[points.length-1];

        final double endX = endPoint.getX();
        final double endY = endPoint.getY();

        // Get current joint object of this connection
        final GJointSkin jointSkin = jointSkins.get(0);

        // Get the coordinates of the joint we use to drag the curve around
        final DraggableBox jointRoot = jointSkin.getRoot();
        final Point2D jointPosition = new Point2D(jointRoot.getLayoutX(), jointRoot.getLayoutY());

        final Point2D midPoint = startPoint.midpoint(endPoint);

        // Calculate the (invisible) control point according to: https://pomax.github.io/bezierinfo/#molding
        // Since we want to let the user drag a point on the Bezier curve around to control its curvature instead of
        // a control point like the JavaFX QuadCurve intends it, we have to do the calculation below:
        final Point2D controlPoint = jointPosition.subtract(midPoint.subtract(jointPosition));

        // Set the QuadCurveTo parameters
        curve.setControlX(controlPoint.getX());
        curve.setControlY(controlPoint.getY());

        // Calculate angle of the arrow so that the arrow always points towards the connector
        final double deltaX = endX - controlPoint.getX();
        final double deltaY = endY - controlPoint.getY();
        final double angle = Math.atan2(deltaX, deltaY);

        // The curve ends a bit before the connector coordinates as we finish off the curve with an arrow head
        final double endOffset = ARROW_LENGTH / 2;

        final double endXOffset = endX - endOffset * Math.sin(angle);
        final double endYOffset = endY - endOffset * Math.cos(angle);

        curve.setX(endXOffset);
        curve.setY(endYOffset);

        arrowHead.setCenter(endXOffset, endYOffset);
        arrowHead.setAngle(Math.toDegrees(-angle));
        arrowHead.draw();

        // Initial MoveTo is needed to start the path
        final MoveTo moveTo = new MoveTo(GeometryUtils.moveOffPixel(startX), GeometryUtils.moveOffPixel(startY));

        path.getElements().clear();
        path.getElements().add(moveTo);
        path.getElements().add(curve);
    }

    @Override
    protected void selectionChanged(boolean isSelected)
    {
        final DataFlowJointSkin jointSkin = (DataFlowJointSkin) jointSkins.get(0);
        if (isSelected) {
            path.pseudoClassStateChanged(PSEUDO_CLASS_SELECTED, true);
            jointSkin.getRoot().pseudoClassStateChanged(PSEUDO_CLASS_SELECTED, true);
            setConnectorsStyle(getGraphEditor().getSkinLookup(), this.getItem(), PSEUDO_CLASS_SELECTED, true);
        } else {
            path.pseudoClassStateChanged(PSEUDO_CLASS_SELECTED, false);
            jointSkin.getRoot().pseudoClassStateChanged(PSEUDO_CLASS_SELECTED, false);
            setConnectorsStyle(getGraphEditor().getSkinLookup(), this.getItem(), PSEUDO_CLASS_SELECTED, false);
        }
    }
}
