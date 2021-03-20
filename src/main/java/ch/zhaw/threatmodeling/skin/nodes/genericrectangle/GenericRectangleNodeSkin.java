package ch.zhaw.threatmodeling.skin.nodes.genericrectangle;

import ch.zhaw.threatmodeling.skin.DataFlowElement;
import ch.zhaw.threatmodeling.skin.connectors.DataFlowConnectorTypes;
import de.tesis.dynaware.grapheditor.GConnectorSkin;
import de.tesis.dynaware.grapheditor.GNodeSkin;
import de.tesis.dynaware.grapheditor.GraphEditor;
import de.tesis.dynaware.grapheditor.model.GConnector;
import de.tesis.dynaware.grapheditor.model.GNode;
import de.tesis.dynaware.grapheditor.utils.GeometryUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public abstract class GenericRectangleNodeSkin extends GNodeSkin implements DataFlowElement {
    private static final Logger LOGGER = Logger.getLogger("Generic Rectangle");

    private static final String STYLE_CLASS_SELECTION_HALO = "node-selection-halo";
    protected static final PseudoClass PSEUDO_CLASS_SELECTED = PseudoClass.getPseudoClass("selected");

    protected static final double HALO_OFFSET = 5;
    protected static final double HALO_CORNER_SIZE = 10;

    protected static final double MINOR_POSITIVE_OFFSET = 2;
    protected static final double MINOR_NEGATIVE_OFFSET = -3;


    protected final Rectangle selectionHalo = new Rectangle();

    protected final List<GConnectorSkin> topConnectorSkins = new ArrayList<>();
    protected final List<GConnectorSkin> rightConnectorSkins = new ArrayList<>();
    protected final List<GConnectorSkin> bottomConnectorSkins = new ArrayList<>();
    protected final List<GConnectorSkin> leftConnectorSkins = new ArrayList<>();


    protected final VBox contentRoot = new VBox();

    private final StringProperty text = new SimpleStringProperty();
    private final StringProperty type = new SimpleStringProperty();


    @Override
    public String getText() {
        return text.get();
    }

    @Override
    public StringProperty textProperty(){
        return text;
    }

    @Override
    public StringProperty typeProperty(){
        return type;
    }


    @Override
    public void setText(String newText) {
        text.set(newText);
    }

    /**
     * Creates a new {@link GNodeSkin}.
     *
     * @param node the {@link GNode} represented by the skin
     */
    protected GenericRectangleNodeSkin(GNode node) {
        super(node);
        initEventListener();
    }


    private void initEventListener(){
        getRoot().setOnMouseDragOver(mouseDragEvent -> setConnectorsSelected());
        getRoot().setOnMouseDragExited(mouseDragEvent -> setConnectorsUnselected());
    }

    @Override
    public void setConnectorSkins(final List<GConnectorSkin> connectorSkins) {

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

    @Override
    public void layoutConnectors() {
        layoutAllConnectors();
        layoutSelectionHalo();
    }

    @Override
    public Point2D getConnectorPosition(final GConnectorSkin connectorSkin) {

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
     * @param vertical {@code true} to lay out vertically, {@code false} to lay out horizontally
     * @param offset the offset in the other dimension that the skins are layed out in
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
     * Lays out the selection halo based on the current width and height of the node skin region.
     */
    private void layoutSelectionHalo() {

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

    public void setConnectorsSelected()
    {
        final GraphEditor editor = getGraphEditor();
        if(editor != null) {
           topConnectorSkins.forEach(skin -> editor.getSelectionManager().select(skin.getItem()));
           rightConnectorSkins.forEach(skin -> editor.getSelectionManager().select(skin.getItem()));
           leftConnectorSkins.forEach(skin -> editor.getSelectionManager().select(skin.getItem()));
           bottomConnectorSkins.forEach(skin -> editor.getSelectionManager().select(skin.getItem()));
        }
    }

    private void setConnectorsUnselected(){
        final GraphEditor editor = getGraphEditor();
        if(editor != null) {
            topConnectorSkins.forEach(skin -> editor.getSelectionManager().clearSelection(skin.getItem()));
            rightConnectorSkins.forEach(skin -> editor.getSelectionManager().clearSelection(skin.getItem()));
            leftConnectorSkins.forEach(skin -> editor.getSelectionManager().clearSelection(skin.getItem()));
            bottomConnectorSkins.forEach(skin -> editor.getSelectionManager().clearSelection(skin.getItem()));
        }

    }

    /**
     * Removes all connectors from the list of children.
     */
    private void removeAllConnectors() {

        topConnectorSkins.stream().forEach(skin -> getRoot().getChildren().remove(skin.getRoot()));
        rightConnectorSkins.stream().forEach(skin -> getRoot().getChildren().remove(skin.getRoot()));
        bottomConnectorSkins.stream().forEach(skin -> getRoot().getChildren().remove(skin.getRoot()));
        leftConnectorSkins.stream().forEach(skin -> getRoot().getChildren().remove(skin.getRoot()));
    }

    /**
     * Gets a minor x-offset of a few pixels in order that the connector's area is distributed more evenly on either
     * side of the node border.
     *
     * @param connector the connector to be positioned
     * @return an x-offset of a few pixels
     */
    private double getMinorOffsetX(final GConnector connector) {

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
    private double getMinorOffsetY(final GConnector connector) {

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

    protected abstract void createContent();

    protected void createGenericContent(String styleClass, String text) {
        setText(text);
        Label label = new Label(getText());
        label.textProperty().bindBidirectional(textProperty());
        contentRoot.getChildren().add(label);
        getRoot().getChildren().add(contentRoot);

        contentRoot.setAlignment(Pos.CENTER);
        contentRoot.getStyleClass().setAll(styleClass);
    }

    public void setHasBeenSelectedHandler(EventHandler<MouseEvent> hasBeenSelectedHandler) {
        getRoot().setOnMouseClicked(hasBeenSelectedHandler);
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
}
