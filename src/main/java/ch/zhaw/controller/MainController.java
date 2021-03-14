package ch.zhaw.controller;

import ch.zhaw.connections.DataFlowConnectorValidator;
import ch.zhaw.skin.DataFlowDiagramSkinController;
import de.tesis.dynaware.grapheditor.GraphEditor;
import de.tesis.dynaware.grapheditor.core.DefaultGraphEditor;
import de.tesis.dynaware.grapheditor.core.view.GraphEditorContainer;
import de.tesis.dynaware.grapheditor.model.GModel;
import de.tesis.dynaware.grapheditor.model.GraphFactory;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.AnchorPane;

public class MainController {
    private final GraphEditor graphEditor = new DefaultGraphEditor();
    @FXML
    public GraphEditorContainer graphEditorContainer;

    private static final String STYLE_CLASS_TITLED_SKINS = "data-flow-diagram-skin";

    private DataFlowDiagramSkinController dfdSkinController;

    @FXML
    private AnchorPane root;
    @FXML
    private MenuBar menuBar;

    public void initialize() {
        final GModel model = GraphFactory.eINSTANCE.createGModel();
        graphEditor.setModel(model);
        graphEditorContainer.setGraphEditor(graphEditor);
        dfdSkinController = new DataFlowDiagramSkinController(graphEditor, graphEditorContainer);
        graphEditor.getView().getStyleClass().add(STYLE_CLASS_TITLED_SKINS);
        graphEditor.setConnectorValidator(new DataFlowConnectorValidator());
        graphEditor.getProperties().setGridVisible(true);
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
