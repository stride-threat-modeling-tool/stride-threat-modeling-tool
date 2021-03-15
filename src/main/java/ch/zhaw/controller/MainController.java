package ch.zhaw.controller;

import ch.zhaw.connections.DataFlowConnectorValidator;
import ch.zhaw.skin.DataFlowDiagramSkinController;
import de.tesis.dynaware.grapheditor.GraphEditor;
import de.tesis.dynaware.grapheditor.core.DefaultGraphEditor;
import de.tesis.dynaware.grapheditor.core.view.GraphEditorContainer;
import de.tesis.dynaware.grapheditor.model.GModel;
import de.tesis.dynaware.grapheditor.model.GraphFactory;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MainController {
    private final GraphEditor graphEditor = new DefaultGraphEditor();

    private static final String STYLE_CLASS_TITLED_SKINS = "data-flow-diagram-skin";
    @FXML
    public VBox graphEditorParent;

    private DataFlowDiagramSkinController dfdSkinController;

    @FXML
    private AnchorPane root;
    @FXML
    private MenuBar menuBar;

    public void initialize() {
        final GModel model = GraphFactory.eINSTANCE.createGModel();
        graphEditor.setModel(model);
        GraphEditorContainer graphEditorContainer = new GraphEditorContainer();
        setMaxSizeToInfinity(graphEditorContainer);
        setMaxSizeToInfinity(graphEditorParent);
        setGrowth(graphEditorContainer);
        setGrowth(graphEditorParent);
        graphEditorParent.getChildren().add(graphEditorContainer);

        graphEditorContainer.setGraphEditor(graphEditor);
        dfdSkinController = new DataFlowDiagramSkinController(graphEditor, graphEditorContainer);
        graphEditor.getView().getStyleClass().add(STYLE_CLASS_TITLED_SKINS);
        graphEditor.setConnectorValidator(new DataFlowConnectorValidator());
        graphEditor.getProperties().setGridVisible(true);
    }

    private void setGrowth(Node node) {
        HBox.setHgrow(node, Priority.ALWAYS);
        VBox.setVgrow(node, Priority.ALWAYS);
    }

    private void setMaxSizeToInfinity(Node node) {
        node.maxHeight(Double.POSITIVE_INFINITY);
        node.maxWidth(Double.POSITIVE_INFINITY);
    }


    @FXML
    public void addDataStore() {
        dfdSkinController.addDataStore(graphEditor.getView().getLocalToSceneTransform().getMxx());
    }

    @FXML
    public void addExternalEntity() {
        dfdSkinController.addExternalEntity(graphEditor.getView().getLocalToSceneTransform().getMxx());
    }
}
