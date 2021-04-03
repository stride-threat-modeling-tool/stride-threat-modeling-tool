package ch.zhaw.threatmodeling.skin.nodes.generic.rectangle;

import ch.zhaw.threatmodeling.skin.DataFlowElement;
import ch.zhaw.threatmodeling.skin.nodes.generic.GenericNodeSkin;
import de.tesis.dynaware.grapheditor.GConnectorSkin;
import de.tesis.dynaware.grapheditor.GNodeSkin;
import de.tesis.dynaware.grapheditor.model.GNode;
import de.tesis.dynaware.grapheditor.utils.GeometryUtils;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import java.util.List;
import java.util.logging.Logger;

public abstract class GenericRectangleNodeSkin extends GenericNodeSkin implements DataFlowElement {
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
        for (int i = 0; i < count; i++) {

            final GConnectorSkin skin = connectorSkins.get(i);
            final Node skinRoot = skin.getRoot();
            final double connectorOffset = -skin.getWidth() /2 ;
            if (vertical) {
                //left and right connectors
                skinRoot.setLayoutX(offset + connectorOffset);
                skinRoot.setLayoutY((getRoot().getHeight() / (count - 1) * i) + connectorOffset);

            } else {
                /*
                top and bottom connectors
                usage of MoveOnPixel is discouraged because it messes up the placements and gives the connectors a
                asymmetrical look
                */
                skinRoot.setLayoutX(getRoot().getWidth() / 2 + connectorOffset);
                skinRoot.setLayoutY(offset + connectorOffset);
            }
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
