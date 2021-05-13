package ch.zhaw.threatmodeling.skin.connection;

import ch.zhaw.threatmodeling.skin.joint.TrustBoundaryJointSkin;
import ch.zhaw.threatmodeling.skin.nodes.trustboundary.TrustBoundaryNodeSkin;
import de.tesis.dynaware.grapheditor.GConnectionSkin;
import de.tesis.dynaware.grapheditor.GJointSkin;
import de.tesis.dynaware.grapheditor.SelectionManager;
import de.tesis.dynaware.grapheditor.SkinLookup;
import de.tesis.dynaware.grapheditor.core.skins.defaults.connection.SimpleConnectionSkin;
import de.tesis.dynaware.grapheditor.model.GConnection;
import de.tesis.dynaware.grapheditor.model.GConnector;
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

import static ch.zhaw.threatmodeling.skin.DataFlowSkinConstants.PSEUDO_CLASS_HOVER;
import static ch.zhaw.threatmodeling.skin.DataFlowSkinConstants.PSEUDO_CLASS_SELECTED;

/**
 * The DataFlow connection skin.
 *
 * <p>
 * Extension of {@link SimpleConnectionSkin} that provides a mechanism for creating and removing joints.
 * </p>
 */
public class TrustBoundaryConnectionSkin extends GConnectionSkin {
    protected final Group root = new Group();
    protected final Path path = new Path();
    protected final QuadCurveTo curve = new QuadCurveTo();
    private static final String TYPE = TrustBoundaryJointSkin.ELEMENT_TYPE;

    private static final String STYLE_CLASS = "trust-boundary-connection";

    private List<GJointSkin> jointSkins;

    private static final Logger LOGGER = Logger.getLogger("Trust Boundary Connection Skin");


    /**
     * Creates a new data flow connection skin instance.
     *
     * @param connection the {@link GConnection} the skin is being created for
     */
    public TrustBoundaryConnectionSkin(final GConnection connection) {

        super(connection);

        root.setManaged(false);

        root.getChildren().add(path);
        path.setMouseTransparent(false); // allows connection to be highlighted on hover and select
        path.getStyleClass().setAll(STYLE_CLASS);

        initEventListener();
    }

    private void initEventListener() {
        // Change style of TrustBoundary on mouseover
        path.setOnMouseClicked(mouseEvent -> selectTrustBoundary());
        path.setOnMouseEntered(mouseEvent -> highlightTrustBoundary());
        path.setOnMouseExited(mouseEvent -> unhighlightTrustBoundary());
    }

    private void selectTrustBoundary() {
        // Select the whole trust boundary when the connection is clicked
        final SelectionManager selectionManager = getGraphEditor().getSelectionManager();
        GConnection connection = this.getItem();
        final GConnector sourceConnector = connection.getSource();
        final GConnector targetConnector = connection.getTarget();
        selectionManager.select(sourceConnector.getParent());
        selectionManager.select(targetConnector.getParent());
        selectionManager.select(connection.getJoints().get(0));
    }

    private void highlightTrustBoundary() {
        // Highlight the connection, joint, nodes
        SkinLookup skinLookup = getGraphEditor().getSkinLookup();

        // Connection
        path.pseudoClassStateChanged(PSEUDO_CLASS_HOVER, true);

        // Joint
        final TrustBoundaryJointSkin jointSkin = (TrustBoundaryJointSkin) jointSkins.get(0);
        jointSkin.getRoot().pseudoClassStateChanged(PSEUDO_CLASS_HOVER, true);

        // TrustBoundary nodes
        setNodesStyle(skinLookup, this.getItem(), PSEUDO_CLASS_HOVER, true);
    }

    private void unhighlightTrustBoundary() {
        // Unhighlight the connection, joint, nodes
        SkinLookup skinLookup = getGraphEditor().getSkinLookup();

        // Connection
        path.pseudoClassStateChanged(PSEUDO_CLASS_HOVER, false);

        // Joint
        final TrustBoundaryJointSkin jointSkin = (TrustBoundaryJointSkin) jointSkins.get(0);
        jointSkin.getRoot().pseudoClassStateChanged(PSEUDO_CLASS_HOVER, false);

        // TrustBoundary nodes
        setNodesStyle(skinLookup, this.getItem(), PSEUDO_CLASS_HOVER, false);
    }

    private void setNodesStyle(SkinLookup skinLookup, GConnection connection, final PseudoClass pseudoClass, boolean active) {
        final GConnector sourceConnector = connection.getSource();
        final GConnector targetConnector = connection.getTarget();
        final TrustBoundaryNodeSkin sourceNode = (TrustBoundaryNodeSkin) skinLookup.lookupNode(sourceConnector.getParent());
        final TrustBoundaryNodeSkin targetNode = (TrustBoundaryNodeSkin) skinLookup.lookupNode(targetConnector.getParent());
        if(sourceNode != null) {
            sourceNode.getRoot().getChildren().get(0).pseudoClassStateChanged(pseudoClass, active);
        }
        if(targetNode != null){
            targetNode.getRoot().getChildren().get(0).pseudoClassStateChanged(pseudoClass, active);
        }
    }

    public static String getType() {
        return TYPE;
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
    public Point2D[] update() {
        final Point2D[] points = super.update();
        return points;
    }

    @Override
    public void draw(final Map<GConnectionSkin, Point2D[]> allPoints) {
        super.draw(allPoints);

        final Point2D[] points = allPoints == null ? null : allPoints.get(this);
        if (points != null) {
            drawCurve(points);
        } else {
            path.getElements().clear();
        }
    }


    /**
     * Draws the curve for the connection.
     *
     * @param points all points that the connection should pass through (both connector and joint positions)
     */
    private void drawCurve(final Point2D[] points) {
        final Point2D startPoint = points[0];
        final double startX = startPoint.getX();
        final double startY = startPoint.getY();

        // points[1] is the old control point which we ignore since the new control point is only decided by the
        // joint position

        final Point2D endPoint = points[points.length - 1];
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
        // a control point like the JavaFX QuadCurve intends it, we have to the calculation below:
        final Point2D controlPoint = jointPosition.subtract(midPoint.subtract(jointPosition));

        // Set the QuadCurveTo parameters
        curve.setControlX(controlPoint.getX());
        curve.setControlY(controlPoint.getY());
        curve.setX(endX);
        curve.setY(endY);

        // Initial MoveTo is needed to start the path
        final MoveTo moveTo = new MoveTo(GeometryUtils.moveOffPixel(startX), GeometryUtils.moveOffPixel(startY));

        path.getElements().clear();
        path.getElements().add(moveTo);
        path.getElements().add(curve);
    }

    @Override
    protected void selectionChanged(boolean isSelected) {
        if(null != jointSkins){
            final TrustBoundaryJointSkin jointSkin = (TrustBoundaryJointSkin) jointSkins.get(0);
            if (isSelected) {
                path.pseudoClassStateChanged(PSEUDO_CLASS_SELECTED, true);
                jointSkin.getRoot().pseudoClassStateChanged(PSEUDO_CLASS_SELECTED, true);
                setNodesStyle(getGraphEditor().getSkinLookup(), this.getItem(), PSEUDO_CLASS_SELECTED, true);
            } else {
                path.pseudoClassStateChanged(PSEUDO_CLASS_SELECTED, false);
                jointSkin.getRoot().pseudoClassStateChanged(PSEUDO_CLASS_SELECTED, false);
                setNodesStyle(getGraphEditor().getSkinLookup(), this.getItem(), PSEUDO_CLASS_SELECTED, false);
            }
        }

    }
}
