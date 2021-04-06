package ch.zhaw.threatmodeling.controller;

import ch.zhaw.threatmodeling.connections.DataFlowConnectorValidator;
import ch.zhaw.threatmodeling.model.Threat;
import ch.zhaw.threatmodeling.model.ThreatGenerator;
import ch.zhaw.threatmodeling.model.enums.STRIDECategory;
import ch.zhaw.threatmodeling.model.enums.State;
import ch.zhaw.threatmodeling.model.enums.ThreatPriority;
import ch.zhaw.threatmodeling.skin.controller.DataFlowDiagramSkinController;
import ch.zhaw.threatmodeling.skin.joint.DataFlowJointSkin;
import de.tesis.dynaware.grapheditor.GraphEditor;
import de.tesis.dynaware.grapheditor.core.DefaultGraphEditor;
import de.tesis.dynaware.grapheditor.core.view.GraphEditorContainer;
import de.tesis.dynaware.grapheditor.model.GModel;
import de.tesis.dynaware.grapheditor.model.GraphFactory;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.logging.Logger;

public class MainController {
    public static final String CONNECTION_LAYER_CSS_NAME = "graph-editor-connection-layer";
    private static final Logger LOGGER = Logger.getLogger("Main controller");
    private static final String STYLE_CLASS_SKINS = "data-flow-diagram-skin";
    private final GraphEditor graphEditor = new DefaultGraphEditor();
    private final ObjectProperty<Threat> currentThreat = new SimpleObjectProperty<>();
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
    private TextArea descriptionTextArea;

    @FXML
    private ChoiceBox<STRIDECategory> categoryChoiceBox;

    @FXML
    private ChoiceBox<ThreatPriority> priorityChoiceBox;

    @FXML
    private ChoiceBox<State> stateChoiceBox;

    @FXML
    private TextField editTitleTextField;

    @FXML
    private TextArea justificationTextArea;

    public void initialize() {
        final GModel model = GraphFactory.eINSTANCE.createGModel();
        threatGenerator = new ThreatGenerator(model, graphEditor.getSkinLookup());
        graphEditor.setModel(model);
        GraphEditorContainer graphEditorContainer = new GraphEditorContainer();
        setMaxSizeToInfinity(graphEditorContainer);
        setMaxSizeToInfinity(graphEditorParent);
        setGrowth(graphEditorContainer);
        setGrowth(graphEditorParent);
        graphEditorParent.getChildren().add(graphEditorContainer);

        graphEditorContainer.setGraphEditor(graphEditor);
        dfdSkinController = new DataFlowDiagramSkinController(graphEditor, graphEditorContainer, threatGenerator);
        graphEditor.getView().getStyleClass().add(STYLE_CLASS_SKINS);
        graphEditor.setConnectorValidator(new DataFlowConnectorValidator());
        graphEditor.getProperties().setGridVisible(true);

        bringConnectionsToForeGround();
        bindTextFieldsToCurrentElement();
        initThreatTableUpdates();
        initThreatListTable();
        initChoiceBoxesValues();
        initThreatModifiedListener();

    }

    private void bringConnectionsToForeGround() {
        graphEditor.getView().getChildrenUnmodifiable().forEach(element -> {
                    if (element.getStyleClass().contains(CONNECTION_LAYER_CSS_NAME)) {
                        element.toFront();
                    }
                }
        );
    }

    private void initThreatModifiedListener() {
        descriptionTextArea.textProperty().addListener((observableValue, oldVal, newVal) -> setCurrentThreatModified());
        editTitleTextField.textProperty().addListener((observableValue, s, t1) -> setCurrentThreatModified());
        justificationTextArea.textProperty().addListener((observableValue, s, t1) -> setCurrentThreatModified());
    }

    private void setCurrentThreatModified() {
        Threat threat = currentThreat.get();
        if (threat != null) {
            threat.setModified(true);
        }
    }

    private void initThreatTableUpdates() {
        descriptionTextArea.setOnKeyTyped(keyEvent -> threatTable.refresh());
        justificationTextArea.setOnKeyTyped(keyEvent -> threatTable.refresh());

        editTextTextField.setOnKeyTyped(keyEvent -> threatTable.refresh());
        editTitleTextField.setOnKeyTyped(keyEvent -> threatTable.refresh());

        categoryChoiceBox.getSelectionModel().selectedItemProperty().addListener((v, s, t) -> threatTable.refresh());
        stateChoiceBox.getSelectionModel().selectedItemProperty().addListener((v, s, t) -> threatTable.refresh());
        priorityChoiceBox.getSelectionModel().selectedItemProperty().addListener((v, p, t) -> threatTable.refresh());
    }

    private void initChoiceBoxesValues() {
        ObservableList<STRIDECategory> categories = FXCollections.observableArrayList();
        ObservableList<ThreatPriority> priorities = FXCollections.observableArrayList();
        ObservableList<State> states = FXCollections.observableArrayList();

        categories.addAll(STRIDECategory.values());
        priorities.addAll(ThreatPriority.values());
        states.addAll(State.values());

        categoryChoiceBox.setItems(categories);
        priorityChoiceBox.setItems(priorities);
        stateChoiceBox.setItems(states);
    }

    private void bindFieldsToCurrentThreat() {
        descriptionTextArea.textProperty().bindBidirectional(currentThreat.get().getDescriptionProperty());
        justificationTextArea.textProperty().bindBidirectional(currentThreat.get().getJustificationProperty());
        editTitleTextField.textProperty().bindBidirectional(currentThreat.get().getTitleProperty());

        categoryChoiceBox.valueProperty().bindBidirectional(currentThreat.get().getCategoryProperty());
        priorityChoiceBox.valueProperty().bindBidirectional(currentThreat.get().getPriorityProperty());
        stateChoiceBox.valueProperty().bindBidirectional(currentThreat.get().getStateProperty());

    }

    private void unbindFieldsToCurrentThreat() {
        descriptionTextArea.textProperty().unbindBidirectional(currentThreat.get().getDescriptionProperty());
        justificationTextArea.textProperty().unbindBidirectional(currentThreat.get().getJustificationProperty());
        editTitleTextField.textProperty().unbindBidirectional(currentThreat.get().getTitleProperty());

        categoryChoiceBox.valueProperty().unbindBidirectional(currentThreat.get().getCategoryProperty());
        priorityChoiceBox.valueProperty().unbindBidirectional(currentThreat.get().getPriorityProperty());
        stateChoiceBox.valueProperty().unbindBidirectional(currentThreat.get().getStateProperty());
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
            if (currentThreat.get() != null) {
                unbindFieldsToCurrentThreat();
            }
            currentThreat.set(newThreat);
            bindFieldsToCurrentThreat();
        });

        threatTable.itemsProperty().bindBidirectional(threatGenerator.getThreatsProperty());
    }

    private void bindTextFieldsToCurrentElement() {
        dfdSkinController.getCurrentElement().addListener((observableValue, oldVal, newVal) -> {
            if (oldVal != null) {
                editTextTextField.textProperty().unbindBidirectional(oldVal.textProperty());
                nodeTypeLabel.textProperty().unbindBidirectional(oldVal.typeProperty());
            }
            if (newVal != null) {
                editTextTextField.textProperty().bindBidirectional(newVal.textProperty());
                nodeTypeLabel.textProperty().bindBidirectional(newVal.typeProperty());
            }
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

    @FXML
    public void addProcess() {
        dfdSkinController.addProcess(graphEditor.getView().getLocalToSceneTransform().getMxx());
    }

    @FXML
    public void addMultipleProcess() {
        dfdSkinController.addMultipleProcess(graphEditor.getView().getLocalToSceneTransform().getMxx());
    }

    @FXML
    public void undo() {
        dfdSkinController.undo();
    }

    @FXML
    public void redo() {
        dfdSkinController.redo();
    }

    @FXML
    public void copy() {
        dfdSkinController.copy();
    }

    @FXML
    public void paste() {
        dfdSkinController.paste();
    }

    @FXML
    public void deleteSelection() {
        dfdSkinController.deleteSelection();
    }

    @FXML
    public void clearAll() {
        dfdSkinController.clearAll();
    }
}
