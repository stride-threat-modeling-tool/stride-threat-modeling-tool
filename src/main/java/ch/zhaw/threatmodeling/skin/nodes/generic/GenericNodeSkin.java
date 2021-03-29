package ch.zhaw.threatmodeling.skin.nodes.generic;

import ch.zhaw.threatmodeling.skin.DataFlowElement;
import de.tesis.dynaware.grapheditor.GConnectorSkin;
import de.tesis.dynaware.grapheditor.GNodeSkin;
import de.tesis.dynaware.grapheditor.GraphEditor;
import de.tesis.dynaware.grapheditor.model.GNode;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public abstract class GenericNodeSkin extends GNodeSkin implements DataFlowElement {
    protected static final PseudoClass PSEUDO_CLASS_SELECTED = PseudoClass.getPseudoClass("selected");
    protected static final String STYLE_CLASS_SELECTION_HALO = "node-selection-halo";
    protected final List<GConnectorSkin> topConnectorSkins = new ArrayList<>();
    protected final List<GConnectorSkin> rightConnectorSkins = new ArrayList<>();
    protected final List<GConnectorSkin> bottomConnectorSkins = new ArrayList<>();
    protected final List<GConnectorSkin> leftConnectorSkins = new ArrayList<>();
    protected final VBox contentRoot = new VBox();
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

                if (ch.zhaw.connectors.DataFlowConnectorTypes.isTop(connectorType)) {
                    topConnectorSkins.add(connectorSkin);
                } else if (ch.zhaw.connectors.DataFlowConnectorTypes.isRight(connectorType)) {
                    rightConnectorSkins.add(connectorSkin);
                } else if (ch.zhaw.connectors.DataFlowConnectorTypes.isBottom(connectorType)) {
                    bottomConnectorSkins.add(connectorSkin);
                } else if (ch.zhaw.connectors.DataFlowConnectorTypes.isLeft(connectorType)) {
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

    public abstract Point2D getConnectorPosition(GConnectorSkin gConnectorSkin);

    protected abstract void selectionChanged(boolean b);


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

    protected void createGenericContent(String styleClass, String text) {
        setText(text);
        Label label = new Label(getText());
        label.textProperty().bindBidirectional(textProperty());
        contentRoot.getChildren().add(label);
        getRoot().getChildren().add(contentRoot);

        contentRoot.setAlignment(Pos.CENTER);
        contentRoot.getStyleClass().setAll(styleClass);
    }
}
