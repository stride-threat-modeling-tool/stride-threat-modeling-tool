package ch.zhaw.threatmodeling.skin.nodes.generic.rectangle;

import ch.zhaw.threatmodeling.connectors.DataFlowConnectorTypes;
import ch.zhaw.threatmodeling.skin.DataFlowElement;
import ch.zhaw.threatmodeling.skin.nodes.generic.GenericNodeSkin;
import de.tesis.dynaware.grapheditor.GConnectorSkin;
import de.tesis.dynaware.grapheditor.GNodeSkin;
import de.tesis.dynaware.grapheditor.model.GConnector;
import de.tesis.dynaware.grapheditor.model.GNode;
import de.tesis.dynaware.grapheditor.utils.GeometryUtils;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import java.util.List;
import java.util.logging.Logger;

public abstract class GenericRectangleNodeSkin extends GenericNodeSkin implements DataFlowElement {
    protected static final double MINOR_POSITIVE_OFFSET = 2;
    protected static final double MINOR_NEGATIVE_OFFSET = -3;
    private static final Logger LOGGER = Logger.getLogger("Generic Rectangle");


    /**
     * Creates a new {@link GNodeSkin}.
     *
     * @param node the {@link GNode} represented by the skin
     */
    protected GenericRectangleNodeSkin(GNode node) {
        super(node);
    }


    @Override
    public void layoutConnectors() {
        layoutAllConnectors();
        layoutSelectionHalo();
    }

    /**
     * Lays out the node's connectors.
     */
    private void layoutAllConnectors() {

        layoutConnectors(topConnectorSkins, false, 0);
        layoutConnectors(rightConnectorSkins, true, getRoot().getWidth());
        layoutConnectors(bottomConnectorSkins, false, getRoot().getHeight());
        layoutConnectors(leftConnectorSkins, true, 0);
    }

    /**
     * Lays out the given connector skins in a horizontal or vertical direction at the given offset.
     *
     * @param connectorSkins the skins to lay out
     * @param vertical       {@code true} to lay out vertically, {@code false} to lay out horizontally
     * @param offset         the offset in the other dimension that the skins are layed out in
     */
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

    /**
     * Gets a minor x-offset of a few pixels in order that the connector's area is distributed more evenly on either
     * side of the node border.
     *
     * @param connector the connector to be positioned
     * @return an x-offset of a few pixels
     */
    protected double getMinorOffsetX(final GConnector connector) {

        final String connectorType = connector.getType();

        if (connectorType.equals(DataFlowConnectorTypes.LEFT) || connectorType.equals(DataFlowConnectorTypes.RIGHT)) {
            return MINOR_POSITIVE_OFFSET;
        } else {
            return MINOR_NEGATIVE_OFFSET;
        }
    }

    /**
     * Gets a minor y-offset of a few pixels in order that the connector's area is distributed more evenly on either
     * side of the node border.
     *
     * @param connector the connector to be positioned
     * @return a y-offset of a few pixels
     */
    protected double getMinorOffsetY(final GConnector connector) {

        final String connectorType = connector.getType();

        if (connectorType.equals(DataFlowConnectorTypes.TOP) || connectorType.equals(DataFlowConnectorTypes.BOTTOM)) {
            return MINOR_POSITIVE_OFFSET;
        } else {
            return MINOR_NEGATIVE_OFFSET;
        }
    }

    /**
     * Stops the node being dragged if it isn't selected.
     *
     * @param event a mouse-dragged event on the node
     */
    protected void filterMouseDragged(final MouseEvent event) {
        if (event.isPrimaryButtonDown() && !isSelected()) {
            event.consume();
        }
    }

    /**
     * Adds the selection halo and initializes some of its values.
     */
    protected void addSelectionHalo() {

        getRoot().getChildren().add(selectionHalo);

        selectionHalo.setManaged(false);
        selectionHalo.setMouseTransparent(false);
        selectionHalo.setVisible(false);

        selectionHalo.setLayoutX(-HALO_OFFSET);
        selectionHalo.setLayoutY(-HALO_OFFSET);

        selectionHalo.getStyleClass().add(STYLE_CLASS_SELECTION_HALO);
    }

    protected void createGenericRectangleContent(String styleClass, String text) {
        setText(text);
        contentRoot.getChildren().add(createBoundLabel());
        getRoot().getChildren().add(contentRoot);

        contentRoot.setAlignment(Pos.CENTER);
        contentRoot.getStyleClass().setAll(styleClass);
    }
}
