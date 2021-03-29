package ch.zhaw.threatmodeling.skin.nodes.generic.circle;

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


public abstract class GenericEllipseNodeSkin extends GenericNodeSkin {
    protected static final Paint ELLIPSE_FILL_COLOR = Paint.valueOf("white");
    protected static final Paint ELLIPSE_STROKE_COLOR = Paint.valueOf("black");
    protected static final double STROKE_WIDTH = 1.5;

    protected GenericEllipseNodeSkin(GNode node) {
        super(node);
    }

    @Override
    public void layoutConnectors() {
        layoutConnectors(topConnectorSkins, false, 0);
        layoutConnectors(rightConnectorSkins, true, getRoot().getWidth());
        layoutConnectors(bottomConnectorSkins, false, getRoot().getHeight());
        layoutConnectors(leftConnectorSkins, true, 0);
    }

    private void layoutConnectors(final List<GConnectorSkin> connectorSkins, final boolean vertical, final double offset) {

        final int count = connectorSkins.size();
        final float yOffsetVertical = -7.5f;

        for (int i = 0; i < count; i++) {

            final GConnectorSkin skin = connectorSkins.get(i);
            final Node root = skin.getRoot();
            if (vertical) {

                final double offsetX = getMinorOffsetX(skin.getItem());

                root.setLayoutX(GeometryUtils.moveOnPixel(offset - skin.getWidth() / 2 + offsetX));
                root.setLayoutY((getRoot().getHeight() / (count - 1) * i) + yOffsetVertical);

            } else {

                final double offsetX = getRoot().getWidth() / (count + 1);
                final double offsetY = getMinorOffsetY(skin.getItem());

                root.setLayoutX(GeometryUtils.moveOnPixel((i + 1) * offsetX - skin.getWidth() / 2));
                root.setLayoutY(GeometryUtils.moveOnPixel(offset - skin.getHeight() / 2 + offsetY));
            }
        }
    }

    @Override
    public Point2D getConnectorPosition(GConnectorSkin gConnectorSkin) {
        return null;
    }

    @Override
    protected void createContent() {

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
