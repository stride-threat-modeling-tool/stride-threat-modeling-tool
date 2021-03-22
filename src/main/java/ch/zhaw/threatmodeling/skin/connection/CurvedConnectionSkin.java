package ch.zhaw.threatmodeling.skin.connection;

import de.tesis.dynaware.grapheditor.GConnectionSkin;
import de.tesis.dynaware.grapheditor.GJointSkin;
import de.tesis.dynaware.grapheditor.model.GConnection;
import de.tesis.dynaware.grapheditor.utils.DraggableBox;
import de.tesis.dynaware.grapheditor.utils.GeometryUtils;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

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
    protected final Path backgroundPath = new Path();

    private static final String STYLE_CLASS = "curved-connection";
    private static final String STYLE_CLASS_BACKGROUND = "curved-connection-background";

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

        // Background path is invisible and used only to capture hover events.
        root.getChildren().add(backgroundPath);
        root.getChildren().add(path);
        path.setMouseTransparent(true);

        backgroundPath.getStyleClass().setAll(STYLE_CLASS_BACKGROUND);
        path.getStyleClass().setAll(STYLE_CLASS);
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

        // points[1] is the old control point which we ignore since the new control point decided by the joint position

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

        backgroundPath.getElements().clear();
        backgroundPath.getElements().addAll(path.getElements());
    }

    @Override
    protected void selectionChanged(boolean isSelected)
    {
        // Not implemented
    }
}
