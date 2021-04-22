package ch.zhaw.threatmodeling.skin.nodes.trustboundary;

import ch.zhaw.threatmodeling.skin.connection.TrustBoundaryConnectionSkin;
import ch.zhaw.threatmodeling.skin.joint.TrustBoundaryJointSkin;
import ch.zhaw.threatmodeling.skin.nodes.generic.GenericNodeSkin;
import de.tesis.dynaware.grapheditor.EditorElement;
import de.tesis.dynaware.grapheditor.GConnectorSkin;
import de.tesis.dynaware.grapheditor.SkinLookup;
import de.tesis.dynaware.grapheditor.model.GConnection;
import de.tesis.dynaware.grapheditor.model.GConnector;
import de.tesis.dynaware.grapheditor.model.GJoint;
import de.tesis.dynaware.grapheditor.model.GNode;
import de.tesis.dynaware.grapheditor.utils.DraggableBox;
import javafx.css.PseudoClass;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;

import java.util.logging.Logger;

import static ch.zhaw.threatmodeling.skin.DataFlowSkinConstants.PSEUDO_CLASS_HOVER;

public class TrustBoundaryNodeSkin extends GenericNodeSkin {

    protected static final int SIZE = 15;
    private static final String STYLE_CLASS = "trust-boundary-node";
    public static final String TITLE_TEXT = "Trust Boundary";
    private static final Logger LOGGER = Logger.getLogger("Trust boundary node skin");

    public TrustBoundaryNodeSkin(GNode node) {
        super(node);
        typeProperty().set(TITLE_TEXT);

        Rectangle border = new Rectangle();
        border.setFill(Color.TRANSPARENT);
        border.setWidth(SIZE);
        border.setHeight(SIZE);
        border.setMouseTransparent(false);

        getRoot().resize(SIZE, SIZE);

        border.getStyleClass().setAll(STYLE_CLASS);
        getRoot().getChildren().add(border);
        addSelectionHalo();

        initEventListener();
    }

    private void initEventListener() {
        // Change style of TrustBoundary on mouseover
        getRoot().setOnMouseEntered(mouseEvent -> highlightTrustBoundary());
        getRoot().setOnMouseExited(mouseEvent -> unhighlightTrustBoundary());
    }

    private void highlightTrustBoundary() {
        // Highlight the connection, joint, nodes
        SkinLookup skinLookup = getGraphEditor().getSkinLookup();

        // Connection
        try {
            final GConnection connection = getItem().getConnectors().get(0).getConnections().get(0);
            setConnectionStyle(skinLookup, connection, PSEUDO_CLASS_HOVER, true);

            // Joint
            final GJoint joint = connection.getJoints().get(0);
            final TrustBoundaryJointSkin jointSkin = (TrustBoundaryJointSkin) skinLookup.lookupJoint(joint);
            jointSkin.getRoot().pseudoClassStateChanged(PSEUDO_CLASS_HOVER, true);

            // TrustBoundary nodes
            setNodesStyle(skinLookup, connection, PSEUDO_CLASS_HOVER, true);
        } catch (IndexOutOfBoundsException ignored) {
            LOGGER.info("ignored exception occurred");
        }
    }

    private void unhighlightTrustBoundary() {
        // Unhighlight the connection, joint, nodes
        SkinLookup skinLookup = getGraphEditor().getSkinLookup();
        try{
            // Connection
            final GConnection connection = getItem().getConnectors().get(0).getConnections().get(0);
            setConnectionStyle(skinLookup, connection, PSEUDO_CLASS_HOVER, false);

            // Joint
            final GJoint joint = connection.getJoints().get(0);
            final TrustBoundaryJointSkin jointSkin = (TrustBoundaryJointSkin) skinLookup.lookupJoint(joint);
            jointSkin.getRoot().pseudoClassStateChanged(PSEUDO_CLASS_HOVER, false);

            // TrustBoundary nodes
            setNodesStyle(skinLookup, connection, PSEUDO_CLASS_HOVER, false);
        } catch (IndexOutOfBoundsException ignored) {
            LOGGER.info("ignored exception occurred");
        }

    }


    private void setConnectionStyle(SkinLookup skinLookup, GConnection connection, PseudoClass pseudoClass, boolean active) {
        final TrustBoundaryConnectionSkin connectionSkin = (TrustBoundaryConnectionSkin) skinLookup.lookupConnection(connection);
        Group connectionSkinRoot = (Group) connectionSkin.getRoot();
        Path path = (Path) connectionSkinRoot.getChildren().get(0);
        path.pseudoClassStateChanged(pseudoClass, active);
    }

    private void setJointStyle(SkinLookup skinLookup, GConnection connection, PseudoClass pseudoClass, boolean active) {
        final GJoint joint = connection.getJoints().get(0);
        final TrustBoundaryJointSkin jointSkin = (TrustBoundaryJointSkin) skinLookup.lookupJoint(joint);
        jointSkin.getRoot().pseudoClassStateChanged(pseudoClass, active);
    }

    private void setNodesStyle(SkinLookup skinLookup, GConnection connection, final PseudoClass pseudoClass, boolean active) {
        final GConnector sourceConnector = connection.getSource();
        final GConnector targetConnector = connection.getTarget();
        final TrustBoundaryNodeSkin sourceNode = (TrustBoundaryNodeSkin) skinLookup.lookupNode(sourceConnector.getParent());
        final TrustBoundaryNodeSkin targetNode = (TrustBoundaryNodeSkin) skinLookup.lookupNode(targetConnector.getParent());
        sourceNode.getRoot().getChildren().get(0).pseudoClassStateChanged(pseudoClass, active);
        targetNode.getRoot().getChildren().get(0).pseudoClassStateChanged(pseudoClass, active);
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
        try {
            final GConnection connection = getItem().getConnectors().get(0).getConnections().get(0);
            final SkinLookup skinLookup = getGraphEditor().getSkinLookup();
            if (isSelected) {
                setJointStyle(skinLookup, connection, PSEUDO_CLASS_SELECTED, true);
                setConnectionStyle(skinLookup, connection, PSEUDO_CLASS_SELECTED, true);
                setNodesStyle(skinLookup, connection, PSEUDO_CLASS_SELECTED, true);
            } else {
                setJointStyle(skinLookup, connection, PSEUDO_CLASS_SELECTED, false);
                setConnectionStyle(skinLookup, connection, PSEUDO_CLASS_SELECTED, false);
                setNodesStyle(skinLookup, connection, PSEUDO_CLASS_SELECTED, false);
            }
        } catch (IndexOutOfBoundsException ignored){
            LOGGER.info("An ignored exception has occurred");
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
    protected DraggableBox createContainer() {
        return new DraggableBox(EditorElement.NODE) {

            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                layoutConnectors();
            }

            @Override
            public void positionMoved() {
                super.positionMoved();
                TrustBoundaryNodeSkin.this.impl_positionMoved();
            }
        };
    }

}
