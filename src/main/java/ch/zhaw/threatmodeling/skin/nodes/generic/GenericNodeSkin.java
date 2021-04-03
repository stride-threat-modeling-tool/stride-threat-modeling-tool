package ch.zhaw.threatmodeling.skin.nodes.generic;

import ch.zhaw.threatmodeling.connectors.DataFlowConnectorTypes;
import ch.zhaw.threatmodeling.skin.DataFlowElement;
import de.tesis.dynaware.grapheditor.GConnectorSkin;
import de.tesis.dynaware.grapheditor.GNodeSkin;
import de.tesis.dynaware.grapheditor.GraphEditor;
import de.tesis.dynaware.grapheditor.model.GConnector;
import de.tesis.dynaware.grapheditor.model.GNode;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public abstract class GenericNodeSkin extends GNodeSkin implements DataFlowElement {
    protected static final PseudoClass PSEUDO_CLASS_SELECTED = PseudoClass.getPseudoClass("selected");
    protected static final String STYLE_CLASS_SELECTION_HALO = "node-selection-halo";
    protected static final double MINOR_POSITIVE_OFFSET = 2;
    protected static final double MINOR_NEGATIVE_OFFSET = -3;
    protected static final double HALO_OFFSET = 5;
    protected static final double HALO_CORNER_SIZE = 10;
    protected static final double MIN_WIDTH = 81;
    protected static final double MIN_HEIGHT = 81;
    private static final Logger LOGGER = Logger.getLogger("Generic node");
    protected final List<GConnectorSkin> topConnectorSkins = new ArrayList<>();
    protected final List<GConnectorSkin> rightConnectorSkins = new ArrayList<>();
    protected final List<GConnectorSkin> bottomConnectorSkins = new ArrayList<>();
    protected final List<GConnectorSkin> leftConnectorSkins = new ArrayList<>();
    protected final VBox contentRoot = new VBox();
    protected final Rectangle selectionHalo = new Rectangle();
    private final StringProperty text = new SimpleStringProperty();
    private final StringProperty type = new SimpleStringProperty();


    protected GenericNodeSkin(GNode node) {
        super(node);
        initEventListener();
    }

    private void initEventListener() {
        getRoot().setOnMouseDragOver(mouseDragEvent -> setConnectorsSelected());
        getRoot().setOnMouseDragExited(mouseDragEvent -> setConnectorsUnselected());
    }

    @Override
    public StringProperty typeProperty() {
        return type;
    }

    @Override
    public String getText() {
        return text.get();
    }

    @Override
    public void setText(String text) {
        this.text.set(text);
    }

    @Override
    public StringProperty textProperty() {
        return text;
    }

    @Override
    public void setHasBeenSelectedHandler(EventHandler<MouseEvent> hasBeenSelectedHandler) {
        getRoot().setOnMouseClicked(hasBeenSelectedHandler);
    }

    @Override
    public void setConnectorSkins(List<GConnectorSkin> connectorSkins) {
        removeAllConnectors();

        topConnectorSkins.clear();
        rightConnectorSkins.clear();
        bottomConnectorSkins.clear();
        leftConnectorSkins.clear();

        if (connectorSkins != null) {
            for (final GConnectorSkin connectorSkin : connectorSkins) {

                final String connectorType = connectorSkin.getItem().getType();

                if (DataFlowConnectorTypes.isTop(connectorType)) {
                    topConnectorSkins.add(connectorSkin);
                } else if (DataFlowConnectorTypes.isRight(connectorType)) {
                    rightConnectorSkins.add(connectorSkin);
                } else if (DataFlowConnectorTypes.isBottom(connectorType)) {
                    bottomConnectorSkins.add(connectorSkin);
                } else if (DataFlowConnectorTypes.isLeft(connectorType)) {
                    leftConnectorSkins.add(connectorSkin);
                }

                getRoot().getChildren().add(connectorSkin.getRoot());
            }
        }

        layoutConnectors();
    }

    public void removeAllConnectors() {
        topConnectorSkins.forEach(skin -> getRoot().getChildren().remove(skin.getRoot()));
        rightConnectorSkins.forEach(skin -> getRoot().getChildren().remove(skin.getRoot()));
        bottomConnectorSkins.forEach(skin -> getRoot().getChildren().remove(skin.getRoot()));
        leftConnectorSkins.forEach(skin -> getRoot().getChildren().remove(skin.getRoot()));
    }

    public abstract void layoutConnectors();

    public Point2D getConnectorPosition(GConnectorSkin connectorSkin) {

        final Node connectorRoot = connectorSkin.getRoot();

        final Side side = DataFlowConnectorTypes.getSide(connectorSkin.getItem().getType());

        // The following logic is required because the connectors are offset slightly from the node edges.
        final double x, y;
        if (side.equals(Side.LEFT)) {
            x = 0;
            y = connectorRoot.getLayoutY() + connectorSkin.getHeight() / 2;
        } else if (side.equals(Side.RIGHT)) {
            x = getRoot().getWidth();
            y = connectorRoot.getLayoutY() + connectorSkin.getHeight() / 2;
        } else if (side.equals(Side.TOP)) {
            x = connectorRoot.getLayoutX() + connectorSkin.getWidth() / 2;
            y = 0;
        } else {
            x = connectorRoot.getLayoutX() + connectorSkin.getWidth() / 2;
            y = getRoot().getHeight();
        }

        return new Point2D(x, y);
    }

    @Override
    protected void selectionChanged(final boolean isSelected) {
        if (isSelected) {
            selectionHalo.setVisible(true);
            layoutSelectionHalo();
            contentRoot.pseudoClassStateChanged(PSEUDO_CLASS_SELECTED, true);
            getRoot().toFront();
            setConnectorsSelected();
        } else {
            selectionHalo.setVisible(false);
            contentRoot.pseudoClassStateChanged(PSEUDO_CLASS_SELECTED, false);
        }
    }

    protected void layoutSelectionHalo() {
        if (selectionHalo.isVisible()) {

            selectionHalo.setWidth(getRoot().getWidth() + 2 * HALO_OFFSET);
            selectionHalo.setHeight(getRoot().getHeight() + 2 * HALO_OFFSET);

            final double cornerLength = 2 * HALO_CORNER_SIZE;
            final double xGap = getRoot().getWidth() - 2 * HALO_CORNER_SIZE + 2 * HALO_OFFSET;
            final double yGap = getRoot().getHeight() - 2 * HALO_CORNER_SIZE + 2 * HALO_OFFSET;

            selectionHalo.setStrokeDashOffset(HALO_CORNER_SIZE);
            selectionHalo.getStrokeDashArray().setAll(cornerLength, yGap, cornerLength, xGap);
        }
    }


    protected abstract void createContent();

    public void setConnectorsSelected() {
        final GraphEditor editor = getGraphEditor();
        if (editor != null) {
            topConnectorSkins.forEach(skin -> editor.getSelectionManager().select(skin.getItem()));
            rightConnectorSkins.forEach(skin -> editor.getSelectionManager().select(skin.getItem()));
            leftConnectorSkins.forEach(skin -> editor.getSelectionManager().select(skin.getItem()));
            bottomConnectorSkins.forEach(skin -> editor.getSelectionManager().select(skin.getItem()));
        }
    }

    private void setConnectorsUnselected() {
        final GraphEditor editor = getGraphEditor();
        if (editor != null) {
            topConnectorSkins.forEach(skin -> editor.getSelectionManager().clearSelection(skin.getItem()));
            rightConnectorSkins.forEach(skin -> editor.getSelectionManager().clearSelection(skin.getItem()));
            leftConnectorSkins.forEach(skin -> editor.getSelectionManager().clearSelection(skin.getItem()));
            bottomConnectorSkins.forEach(skin -> editor.getSelectionManager().clearSelection(skin.getItem()));
        }

    }

    protected Label createBoundLabel() {
        Label label = new Label(getText());
        label.textProperty().bindBidirectional(textProperty());
        return label;
    }

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

}
