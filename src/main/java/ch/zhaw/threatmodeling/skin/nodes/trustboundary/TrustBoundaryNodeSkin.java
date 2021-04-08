package ch.zhaw.threatmodeling.skin.nodes.trustboundary;

import ch.zhaw.threatmodeling.skin.DataFlowSkinConstants;
import ch.zhaw.threatmodeling.skin.nodes.generic.GenericNodeSkin;
import de.tesis.dynaware.grapheditor.EditorElement;
import de.tesis.dynaware.grapheditor.GConnectorSkin;
import de.tesis.dynaware.grapheditor.model.GNode;
import de.tesis.dynaware.grapheditor.utils.DraggableBox;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class TrustBoundaryNodeSkin extends GenericNodeSkin {

    protected static final int SIZE = 15;
    private static final String STYLE_CLASS = "trust-boundary-node";

    public TrustBoundaryNodeSkin(GNode node) {
        super(node);
        Rectangle border = new Rectangle();
        typeProperty().set(DataFlowSkinConstants.DFD_TRUST_BOUNDARY_NODE);
        border.setFill(Color.TRANSPARENT);
        border.setWidth(SIZE);
        border.setHeight(SIZE);
        border.setMouseTransparent(false);

        getRoot().resize(SIZE, SIZE);

        border.getStyleClass().setAll(STYLE_CLASS);
        getRoot().getChildren().add(border);
        addSelectionHalo();
    }

    @Override
    public void layoutConnectors() {

    }

    @Override
    protected void createContent() {

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

    @Override
    protected void selectionChanged(final boolean isSelected) {
        if (isSelected) {
            layoutSelectionHalo();
            selectionHalo.setVisible(true);
            contentRoot.pseudoClassStateChanged(PSEUDO_CLASS_SELECTED, true);
            getRoot().toFront();
        } else {
            selectionHalo.setVisible(false);
            contentRoot.pseudoClassStateChanged(PSEUDO_CLASS_SELECTED, false);
        }
    }

    @Override
    public Point2D getConnectorPosition(GConnectorSkin connectorSkin) {

        final Node connectorRoot = connectorSkin.getRoot();

        final double x, y;
        x = connectorRoot.getLayoutX() + connectorSkin.getWidth() / 2;
        y = connectorRoot.getLayoutY() + connectorSkin.getHeight() / 2;

        return new Point2D(x, y);
    }

    /**
     * Creates and returns the {@link DraggableBox} that serves as the root for
     * this node skin.<br>
     * A {@link DraggableBox} will be created and return as Trust Boundary Nodes
     * only need to be draggable and not resizable.
     *
     * @return {@link DraggableBox}
     */
    @Override
    protected DraggableBox createContainer()
    {
        return new DraggableBox(EditorElement.NODE)
        {

            @Override
            protected void layoutChildren()
            {
                super.layoutChildren();
                layoutConnectors();
            }

            @Override
            public void positionMoved()
            {
                super.positionMoved();
                TrustBoundaryNodeSkin.this.impl_positionMoved();
            }
        };
    }

}
