package ch.zhaw.threatmodeling.controller;

import ch.zhaw.connections.DataFlowConnectorValidator;
import ch.zhaw.threatmodeling.model.Threat;
import ch.zhaw.threatmodeling.model.ThreatGenerator;
import ch.zhaw.threatmodeling.model.enums.STRIDECategory;
import ch.zhaw.threatmodeling.model.enums.State;
import ch.zhaw.threatmodeling.skin.controller.DataFlowDiagramSkinController;
import ch.zhaw.threatmodeling.skin.joint.DataFlowJointSkin;
import de.tesis.dynaware.grapheditor.GraphEditor;
import de.tesis.dynaware.grapheditor.core.DefaultGraphEditor;
import de.tesis.dynaware.grapheditor.core.view.GraphEditorContainer;
import de.tesis.dynaware.grapheditor.model.GModel;
import de.tesis.dynaware.grapheditor.model.GraphFactory;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.logging.Logger;

public class MainController {
    private static final Logger LOGGER = Logger.getLogger("Main controller");
    private static final String STYLE_CLASS_SKINS = "data-flow-diagram-skin";
    private final GraphEditor graphEditor = new DefaultGraphEditor();



    private ThreatGenerator threatGenerator;
    private DataFlowDiagramSkinController dfdSkinController;

    private final ObjectProperty<Threat> currentThreat = new SimpleObjectProperty<>();

    @FXML
    public VBox graphEditorParent;
    @FXML
    private StackPane root;
    @FXML
    private MenuBar menuBar;

    @FXML
    private TextField editTextTextField;

    @FXML
    private Label nodeTypeLabel;

    @FXML
    private TableView<Threat> threatTable;

    @FXML
    private TableColumn<Threat, Integer> colID;

    @FXML
    private TableColumn<Threat, State> colState;

    @FXML
    private TableColumn<Threat, String> colTitle;

    @FXML
    private TableColumn<Threat, STRIDECategory> colCategory;

    @FXML
    private TableColumn<Threat, String> colDescription;

    @FXML
    private TableColumn<Threat, String> colPriority;

    @FXML
    private TableColumn<Threat, String> colJustification;

    @FXML
    private TableColumn<Threat, DataFlowJointSkin> colInteraction;

    @FXML
    private TextField descriptionTextField;



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


        descriptionTextField.setOnKeyTyped(keyEvent -> threatTable.refresh());
        editTextTextField.setOnKeyTyped(keyEvent -> threatTable.refresh());

        bindTextFieldsToCurrentElement();
        initThreatListTable();

    }

    private void bindTextFieldsToCurrentThreat() {
        descriptionTextField.textProperty().bindBidirectional(currentThreat.get().getDescriptionProperty());
    }
    private void unbindTextFieldsToCurrentThreat() {
        descriptionTextField.textProperty().unbindBidirectional(currentThreat.get().getDescriptionProperty());
    }


    private void initThreatListTable() {
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colState.setCellValueFactory(new PropertyValueFactory<>("state"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colPriority.setCellValueFactory(new PropertyValueFactory<>("priority"));
        colJustification.setCellValueFactory(new PropertyValueFactory<>("justification"));
        colInteraction.setCellValueFactory(new PropertyValueFactory<>("interaction"));

        threatTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldThreat, newThreat) -> {
            LOGGER.info("changed selected item " + newThreat.getTitle());
            if(currentThreat.get() != null){
                unbindTextFieldsToCurrentThreat();
            }
            currentThreat.set(newThreat);
            bindTextFieldsToCurrentThreat();
        });

        threatTable.itemsProperty().bindBidirectional(threatGenerator.getThreatsProperty());
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
        LOGGER.info("Generated threats count: " + threatGenerator.getThreats().size());
    }
}
