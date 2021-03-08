package ch.zhaw.controller;

import ch.zhaw.skin.*;
import de.tesis.dynaware.grapheditor.*;
import de.tesis.dynaware.grapheditor.core.DefaultGraphEditor;
import de.tesis.dynaware.grapheditor.core.view.GraphEditorContainer;
import de.tesis.dynaware.grapheditor.model.GModel;
import de.tesis.dynaware.grapheditor.model.GraphFactory;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.AnchorPane;

public class MainController {
    private final GraphEditor graphEditor = new DefaultGraphEditor();
    private static final String STYLE_CLASS_TITLED_SKINS = "titled-skins";
    @FXML
    public GraphEditorContainer graphEditorContainer;

    @FXML
    private AnchorPane root;
    @FXML
    private MenuBar menuBar;

    public void initialize() {

        final GModel model = GraphFactory.eINSTANCE.createGModel();
        graphEditor.setModel(model);
        graphEditorContainer.setGraphEditor(graphEditor);
        activeSkinController.set(new DataFlowDiagramSkinController(graphEditor, graphEditorContainer));
        graphEditor.getView().getStyleClass().add(STYLE_CLASS_TITLED_SKINS);
        graphEditor.setConnectorValidator(null);

    }

    private final ObjectProperty<SkinController> activeSkinController = new SimpleObjectProperty<>()
    {

        @Override
        protected void invalidated() {
            super.invalidated();
            if(get() != null) {
                get().activate();
            }
        }

    };

    @FXML
    public void fillModel() {
        activeSkinController.get().addNode(graphEditor.getView().getLocalToSceneTransform().getMxx());
    }
}
