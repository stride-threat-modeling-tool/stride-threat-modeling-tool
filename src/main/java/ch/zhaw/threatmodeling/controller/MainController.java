package ch.zhaw.threatmodeling.controller;

import ch.zhaw.threatmodeling.model.ThreatGenerator;
import ch.zhaw.threatmodeling.skin.connections.DataFlowConnectorValidator;
import ch.zhaw.threatmodeling.skin.controller.DataFlowDiagramSkinController;
import de.tesis.dynaware.grapheditor.GraphEditor;
import de.tesis.dynaware.grapheditor.core.DefaultGraphEditor;
import de.tesis.dynaware.grapheditor.core.view.GraphEditorContainer;
import de.tesis.dynaware.grapheditor.model.GModel;
import de.tesis.dynaware.grapheditor.model.GraphFactory;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.logging.Logger;

public class MainController {
    private static final Logger LOGGER = Logger.getLogger("Main controller");
    private static final String STYLE_CLASS_SKINS = "data-flow-diagram-skin";
    private final GraphEditor graphEditor = new DefaultGraphEditor();
    @FXML
    public VBox graphEditorParent;
    private ThreatGenerator threatGenerator;
    private DataFlowDiagramSkinController dfdSkinController;

    @FXML
    private StackPane root;
    @FXML
    private MenuBar menuBar;

    @FXML
    private TextField editTextTextField;

    @FXML
    private Label nodeTypeLabel;

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
        graphEditor.getView().getStyleClass().add(STYLE_CLASS_SKINS);
        graphEditor.setConnectorValidator(new DataFlowConnectorValidator());
        graphEditor.getProperties().setGridVisible(true);

        threatGenerator = new ThreatGenerator(model, graphEditor.getSkinLookup());


        bindTextFieldsToCurrentElement();

    }

    private void bindTextFieldsToCurrentElement() {
        dfdSkinController.getCurrentElement().addListener((observableValue, oldVal, newVal) -> {
            if (oldVal != null) {
                editTextTextField.textProperty().unbindBidirectional(oldVal.textProperty());
                nodeTypeLabel.textProperty().unbindBidirectional(oldVal.typeProperty());
            }
            editTextTextField.textProperty().bindBidirectional(newVal.textProperty());
            nodeTypeLabel.textProperty().bindBidirectional(newVal.typeProperty());
        });
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

    @FXML
    public void analyseDiagram() {
        threatGenerator.generateAllThreats();
    }
}
