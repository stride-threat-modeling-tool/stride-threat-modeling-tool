package ch.zhaw.threatmodeling.controller;

import ch.zhaw.threatmodeling.connections.DataFlowConnectorValidator;
import ch.zhaw.threatmodeling.model.Threat;
import ch.zhaw.threatmodeling.model.ThreatGenerator;
import ch.zhaw.threatmodeling.model.enums.STRIDECategory;
import ch.zhaw.threatmodeling.model.enums.State;
import ch.zhaw.threatmodeling.model.enums.ThreatPriority;
import ch.zhaw.threatmodeling.persistence.DataFlowPersistence;
import ch.zhaw.threatmodeling.skin.DataFlowGraphEditor;
import ch.zhaw.threatmodeling.skin.controller.DataFlowDiagramSkinController;
import ch.zhaw.threatmodeling.skin.joint.DataFlowJointSkin;
import de.tesis.dynaware.grapheditor.GraphEditor;
import de.tesis.dynaware.grapheditor.core.view.GraphEditorContainer;
import de.tesis.dynaware.grapheditor.model.GModel;
import de.tesis.dynaware.grapheditor.model.GraphFactory;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
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
    private final GraphEditor graphEditor = new DataFlowGraphEditor();
    private final ObjectProperty<Threat> currentThreat = new SimpleObjectProperty<>();
    private final DataFlowPersistence persistence = new DataFlowPersistence();

    @FXML
    private TitledPane expandableThreatPane;
    @FXML
    private VBox graphEditorParent;
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

    private final ChangeListener<STRIDECategory> categoryChangeListener = (o, s, t) -> handleThreatFieldsModified();

    private final ChangeListener<ThreatPriority> threatPriorityChangeListener = (o, s, t) -> handleThreatFieldsModified();

    private final ChangeListener<State> stateChangeListener = (o, s, t) -> handleThreatFieldsModified();

    private final ChangeListener<String> threatTextFieldsChangeListener = (o, s, t) -> handleThreatFieldsModified();

    private final ChangeListener<String> titleTextFieldChangeListener = (observableValue, oldTitle, newTitle) -> {
        currentThreat.get().updateThreatElementNames(oldTitle, newTitle);
    };

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
        setThreatFieldsListeners();
        initThreatListTable();
        initChoiceBoxesValues();
    }

    private void bringConnectionsToForeGround() {
        graphEditor.getView().getChildrenUnmodifiable().forEach(element -> {
                    if (element.getStyleClass().contains(CONNECTION_LAYER_CSS_NAME)) {
                        element.toFront();
                    }
                }
        );
    }

    private void setCurrentThreatModified() {
        Threat threat = currentThreat.get();
        if (threat != null) {
            threat.setModified(true);
        }
    }

    private void setThreatFieldsListeners() {
        descriptionTextArea.textProperty().addListener(threatTextFieldsChangeListener);
        justificationTextArea.textProperty().addListener(threatTextFieldsChangeListener);
        editTitleTextField.textProperty().addListener(threatTextFieldsChangeListener);
        editTitleTextField.textProperty().addListener(titleTextFieldChangeListener);
        editTextTextField.textProperty().addListener(threatTextFieldsChangeListener);

        categoryChoiceBox.valueProperty().addListener(categoryChangeListener);
        stateChoiceBox.valueProperty().addListener(stateChangeListener);
        priorityChoiceBox.valueProperty().addListener(threatPriorityChangeListener);
    }

    private void removeThreatFieldsListeners() {
        descriptionTextArea.textProperty().removeListener(threatTextFieldsChangeListener);
        justificationTextArea.textProperty().removeListener(threatTextFieldsChangeListener);
        editTitleTextField.textProperty().removeListener(threatTextFieldsChangeListener);
        editTitleTextField.textProperty().removeListener(titleTextFieldChangeListener);
        editTextTextField.textProperty().removeListener(threatTextFieldsChangeListener);

        categoryChoiceBox.valueProperty().removeListener(categoryChangeListener);
        stateChoiceBox.valueProperty().removeListener(stateChangeListener);
        priorityChoiceBox.valueProperty().removeListener(threatPriorityChangeListener);
    }

    private void handleThreatFieldsModified() {
        threatTable.refresh();
        setCurrentThreatModified();
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
            removeThreatFieldsListeners();
            if (currentThreat.get() != null) {
                unbindFieldsToCurrentThreat();
            }
            currentThreat.set(newThreat);
            bindFieldsToCurrentThreat();
            setThreatFieldsListeners();
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
        dfdSkinController.addDataStore(getCurrentZoomFactor());
    }

    @FXML
    public void addExternalEntity() {
        dfdSkinController.addExternalEntity(getCurrentZoomFactor());
    }

    @FXML
    public void analyseDiagram() {
        threatGenerator.generateAllThreats();
        LOGGER.info("Generated threats count: " + threatGenerator.getThreats().size());
    }

    @FXML
    public void addProcess() {
        dfdSkinController.addProcess(getCurrentZoomFactor());
    }

    @FXML
    public void addMultipleProcess() {
        dfdSkinController.addMultipleProcess(getCurrentZoomFactor());
    }

    private double getCurrentZoomFactor() {
        return graphEditor.getView().getLocalToSceneTransform().getMxx();
    }

    @FXML
    public void undo() {
        dfdSkinController.getDoController().undo();
    }

    @FXML
    public void redo() {
        dfdSkinController.getDoController().redo();
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

    public void addTrustBoundary() {
        dfdSkinController.addTrustBoundary(getCurrentZoomFactor());
    }

    @FXML
    public void saveDiagram() {
        persistence.saveToFile(graphEditor);
    }

    @FXML
    public void loadDiagram() {
        dfdSkinController.restoreModel(persistence.loadFromFile(graphEditor), getCurrentZoomFactor());
    }

    GraphEditor getGraphEditor() {
        return graphEditor;
    }

    DataFlowDiagramSkinController getDfdSkinController() {
        return dfdSkinController;
    }

    TableView<Threat> getThreatTable() {
        return threatTable;
    }

    TextArea getDescriptionTextArea() {
        return descriptionTextArea;
    }

    ChoiceBox<STRIDECategory> getCategoryChoiceBox() {
        return categoryChoiceBox;
    }

    ChoiceBox<ThreatPriority> getPriorityChoiceBox() {
        return priorityChoiceBox;
    }

    ChoiceBox<State> getStateChoiceBox() {
        return stateChoiceBox;
    }

    TextField getEditTitleTextField() {
        return editTitleTextField;
    }

    TextArea getJustificationTextArea() {
        return justificationTextArea;
    }

    TitledPane getExpandableThreatPane() {
        return expandableThreatPane;
    }
}
