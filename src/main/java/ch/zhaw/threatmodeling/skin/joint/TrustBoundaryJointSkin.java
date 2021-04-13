package ch.zhaw.threatmodeling.skin.joint;

import ch.zhaw.threatmodeling.skin.DataFlowElement;
import ch.zhaw.threatmodeling.skin.connection.TrustBoundaryConnectionSkin;
import ch.zhaw.threatmodeling.skin.nodes.trustboundary.TrustBoundaryNodeSkin;
import de.tesis.dynaware.grapheditor.GJointSkin;
import de.tesis.dynaware.grapheditor.SkinLookup;
import de.tesis.dynaware.grapheditor.model.GConnection;
import de.tesis.dynaware.grapheditor.model.GConnector;
import de.tesis.dynaware.grapheditor.model.GJoint;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Path;

import java.util.logging.Logger;

import static ch.zhaw.threatmodeling.skin.DataFlowSkinConstants.PSEUDO_CLASS_HOVER;
import static ch.zhaw.threatmodeling.skin.DataFlowSkinConstants.PSEUDO_CLASS_SELECTED;

public class TrustBoundaryJointSkin extends GJointSkin implements DataFlowElement {
    private static final Logger LOGGER = Logger.getLogger("Trust Boundary Joint Skin");

    public static final String TRUST_BOUNDARY_JOINT_CLASS = "trust-boundary-joint";
    public static final int WIDTH_OFFSET = 10;
    public static final int MAX_LENGTH = 45;
    public static final String ELEMENT_TYPE = "Trust Boundary";

    private final StringProperty text = new SimpleStringProperty();
    private final StringProperty type = new SimpleStringProperty();
    private final Label label = new Label();
    private final GJoint joint;

    public StringProperty typeProperty(){
        return type;
    }

    public StringProperty textProperty() {
        return text;
    }

    @Override
    public String getText() {
        return text.get();
    }

    @Override
    public void setText(String newText) {
        text.set(newText);
    }

    /**
     * Creates a new {@link GJointSkin}.
     *
     * @param joint the {@link GJoint} represented by the skin
     */
    public TrustBoundaryJointSkin(GJoint joint) {
        super(joint);
        this.joint = joint;
        setText(ELEMENT_TYPE);
        type.set(ELEMENT_TYPE);
        StackPane pane = new StackPane();
        label.boundsInLocalProperty().addListener((observableValue, bounds, t1) -> {
            getRoot().resize(label.getWidth() + WIDTH_OFFSET, label.getHeight());
        });
        label.textProperty().bindBidirectional(textProperty());

        pane.getChildren().add(label);
        pane.setMouseTransparent(true);

        getRoot().resize(pane.getWidth(), pane.getHeight());
        getRoot().getChildren().add(pane);
        getRoot().getStyleClass().add(TRUST_BOUNDARY_JOINT_CLASS);

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

        // Joint
       getRoot().pseudoClassStateChanged(PSEUDO_CLASS_HOVER, true);

        // Connection
        final GConnection connection = getItem().getConnection();
        setConnectionStyle(skinLookup, connection, PSEUDO_CLASS_HOVER, true);

        // TrustBoundary nodes
        setNodesStyle(skinLookup, connection, PSEUDO_CLASS_HOVER, true);
    }

    private void unhighlightTrustBoundary() {
        // Unhighlight the connection, joint, nodes
        SkinLookup skinLookup = getGraphEditor().getSkinLookup();

        // Joint
        getRoot().pseudoClassStateChanged(PSEUDO_CLASS_HOVER, false);

        // Connection
        final GConnection connection = getItem().getConnection();
        setConnectionStyle(skinLookup, connection, PSEUDO_CLASS_HOVER, false);

        // TrustBoundary nodes
        setNodesStyle(skinLookup, connection, PSEUDO_CLASS_HOVER, false);
    }

    private void setConnectionStyle(SkinLookup skinLookup, GConnection connection, PseudoClass pseudoClass, boolean active) {
        final TrustBoundaryConnectionSkin connectionSkin = (TrustBoundaryConnectionSkin) skinLookup.lookupConnection(connection);
        Group connectionSkinRoot = (Group) connectionSkin.getRoot();
        Path path = (Path) connectionSkinRoot.getChildren().get(0);
        path.pseudoClassStateChanged(pseudoClass, active);
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
    public double getWidth() {
        return MAX_LENGTH;
    }

    @Override
    public double getHeight() {
        return MAX_LENGTH;
    }

    @Override
    protected void selectionChanged(boolean isSelected) {
        final GConnection connection = joint.getConnection();
        final SkinLookup skinLookup = getGraphEditor().getSkinLookup();
        if (isSelected) {
            getRoot().pseudoClassStateChanged(PSEUDO_CLASS_SELECTED, true);
            setConnectionStyle(skinLookup, connection, PSEUDO_CLASS_SELECTED, true);
            setNodesStyle(skinLookup, connection, PSEUDO_CLASS_SELECTED, true);
        } else {
            getRoot().pseudoClassStateChanged(PSEUDO_CLASS_SELECTED, false);
            setConnectionStyle(skinLookup, connection, PSEUDO_CLASS_SELECTED, false);
            setNodesStyle(skinLookup, connection, PSEUDO_CLASS_SELECTED, false);
        }
    }

    @Override
    public void setHasBeenSelectedHandler(EventHandler<MouseEvent> hasBeenSelectedHandler) {
        getRoot().setOnMouseClicked(hasBeenSelectedHandler);
    }

    @Override
    public String toString() {
        return label.getText();
    }
}
