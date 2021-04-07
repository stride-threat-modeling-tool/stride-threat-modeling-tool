package ch.zhaw.threatmodeling.skin.controller;

import ch.zhaw.threatmodeling.model.Threat;
import ch.zhaw.threatmodeling.model.ThreatGenerator;
import ch.zhaw.threatmodeling.selections.SelectionCopier;
import ch.zhaw.threatmodeling.skin.DataFlowElement;
import ch.zhaw.threatmodeling.skin.DataFlowSkinConstants;
import ch.zhaw.threatmodeling.skin.SkinController;
import ch.zhaw.threatmodeling.skin.connection.DataFlowConnectionSkin;
import ch.zhaw.threatmodeling.skin.connector.DataFlowConnectorSkin;
import ch.zhaw.threatmodeling.skin.joint.DataFlowJointSkin;
import ch.zhaw.threatmodeling.skin.nodes.datastore.DataStoreNodeSkin;
import ch.zhaw.threatmodeling.skin.nodes.externalentity.ExternalEntityNodeSkin;
import ch.zhaw.threatmodeling.skin.nodes.generic.GenericNodeSkin;
import ch.zhaw.threatmodeling.skin.nodes.multipleprocess.MultipleProcessNodeSkin;
import ch.zhaw.threatmodeling.skin.nodes.process.ProcessNodeSkin;
import ch.zhaw.threatmodeling.skin.tail.DataFlowTailSkin;
import ch.zhaw.threatmodeling.skin.utils.DataFlowCommands;
import de.tesis.dynaware.grapheditor.*;
import de.tesis.dynaware.grapheditor.core.view.GraphEditorContainer;
import de.tesis.dynaware.grapheditor.model.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableSet;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import javafx.util.Pair;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.Stack;
import java.util.logging.Logger;

public class DataFlowDiagramSkinController implements SkinController {
    protected static final int NODE_INITIAL_X = 19;
    protected static final int NODE_INITIAL_Y = 19;
    private static final Logger LOGGER = Logger.getLogger("Data Flow Controller");
    protected final GraphEditor graphEditor;
    protected final GraphEditorContainer graphEditorContainer;
    private final ObjectProperty<DataFlowElement> currentElement = new SimpleObjectProperty<>();
    private final ThreatGenerator threatGenerator;
    private final SelectionCopier selectionCopier;
    private final Stack<Pair<String, String>> typeNameDeletedItems = new Stack<>();

    public DataFlowDiagramSkinController(final GraphEditor graphEditor, final GraphEditorContainer container, final ThreatGenerator threatGenerator) {
        this.graphEditor = graphEditor;
        this.selectionCopier = new SelectionCopier(graphEditor.getSkinLookup(), getSelectionManager(), this);
        selectionCopier.initialize(graphEditor.getModel());
        this.graphEditorContainer = container;
        this.threatGenerator = threatGenerator;
        graphEditor.setTailSkinFactory(this::createTailSkin);
        setJointSkinFactory(this::createJointSkin);
        setConnectorSkinFactory(this::createConnectorSkin);
        setConnectionSkinFactory(this::createConnectionSkin);

    }

    public ObjectProperty<DataFlowElement> getCurrentElement() {
        return currentElement;
    }

    @Override
    public void addNode(double currentZoomFactor, String type) {
        final double windowXOffset = graphEditorContainer.getContentX() / currentZoomFactor;
        final double windowYOffset = graphEditorContainer.getContentY() / currentZoomFactor;
        final GNode node = GraphFactory.eINSTANCE.createGNode();
        node.setType(type);

        node.setY(NODE_INITIAL_Y + windowYOffset);


        node.setX(NODE_INITIAL_X + windowXOffset);
        node.setId(allocateNewId());

        for (String currentType : DataFlowSkinConstants.DFD_CONNECTOR_LAYOUT_ORDER) {
            addConnectorToNode(node, currentType);
        }
        Commands.addNode(graphEditor.getModel(), node);
    }

    private void addConnectorToNode(GNode node, String type) {
        final GConnector connector = GraphFactory.eINSTANCE.createGConnector();
        node.getConnectors().add(connector);
        connector.setType(type);
    }


    public void addDataStore(double currentZoomFactor) {
        setNodeSkinFactory(this::createDataStoreSkin);
        addNode(currentZoomFactor, DataStoreNodeSkin.TITLE_TEXT);
    }

    public void addExternalEntity(double currentZoomFactor) {
        setNodeSkinFactory(this::createExternalEntitySkin);
        addNode(currentZoomFactor, ExternalEntityNodeSkin.TITLE_TEXT);

    }

    public void setNodeSkinFactory(Callback<GNode, GNodeSkin> callback) {
        graphEditor.setNodeSkinFactory(callback);
    }

    public void setConnectionSkinFactory(Callback<GConnection, GConnectionSkin> callback) {
        graphEditor.setConnectionSkinFactory(callback);
    }

    public void setConnectorSkinFactory(Callback<GConnector, GConnectorSkin> callback) {
        graphEditor.setConnectorSkinFactory(callback);
    }

    public void setJointSkinFactory(Callback<GJoint, GJointSkin> callback) {
        graphEditor.setJointSkinFactory(callback);
    }

    public void addProcess(double currentZoomFactor) {
        setNodeSkinFactory(this::createProcessSkin);
        addNode(currentZoomFactor, ProcessNodeSkin.TITLE_TEXT);

    }

    public void addMultipleProcess(double currentZoomFactor) {
        setNodeSkinFactory(this::createMultipleProcessSkin);
        addNode(currentZoomFactor, MultipleProcessNodeSkin.TITLE_TEXT);

    }


    private String allocateNewId() {

        final List<GNode> nodes = graphEditor.getModel().getNodes();
        final OptionalInt max = nodes.stream().mapToInt(node -> Integer.parseInt(node.getId())).max();

        if (max.isPresent()) {
            return Integer.toString(max.getAsInt() + 1);
        }
        return "1";
    }

    @Override
    public void activate() {
    }


    @Override
    public void addConnector(Side position, boolean input) {
    }


    public GraphEditor getGraphEditor() {
        return graphEditor;
    }

    @Override
    public void clearConnectors() {

    }

    @Override
    public void handleSelectAll() {

    }

    private EventHandler<MouseEvent> createClickDataFlowElementHandler(DataFlowElement element) {
        return mouseEvent -> {
            if (MouseButton.PRIMARY.equals(mouseEvent.getButton())) {
                this.currentElement.set(element);
                if (element instanceof DataFlowJointSkin) {
                    getSelectionManager().clearSelection();
                    getSelectionManager().select(((DataFlowJointSkin) element).getJoint());
                }
            }
            mouseEvent.consume();
        };
    }

    private SelectionManager getSelectionManager() {
        return getGraphEditor().getSelectionManager();
    }

    public GNodeSkin createDataStoreSkin(final GNode node) {
        DataStoreNodeSkin skin = new DataStoreNodeSkin(node);
        return initNodeEventListeners(node, skin);
    }

    public GNodeSkin createExternalEntitySkin(final GNode node) {
        ExternalEntityNodeSkin skin = new ExternalEntityNodeSkin(node);
        return initNodeEventListeners(node, skin);
    }


    public GNodeSkin createProcessSkin(GNode gNode) {
        ProcessNodeSkin skin = new ProcessNodeSkin(gNode);
        return initNodeEventListeners(gNode, skin);
    }


    public GNodeSkin createMultipleProcessSkin(GNode gNode) {
        MultipleProcessNodeSkin skin = new MultipleProcessNodeSkin(gNode);
        return initNodeEventListeners(gNode, skin);
    }

    public GJointSkin createJointSkin(final GJoint joint) {
        DataFlowJointSkin skin = new DataFlowJointSkin(joint);
        skin.setHasBeenSelectedHandler(createClickDataFlowElementHandler(skin));
        return skin;
    }

    public GConnectorSkin createConnectorSkin(final GConnector connector) {
        return new DataFlowConnectorSkin(connector);
    }

    private GTailSkin createTailSkin(final GConnector connector) {
        return new DataFlowTailSkin(connector);
    }

    public GConnectionSkin createConnectionSkin(GConnection gConnection) {
        return new DataFlowConnectionSkin(gConnection);
    }


    private GenericNodeSkin initNodeEventListeners(GNode gNode, GenericNodeSkin skin) {
        skin.setHasBeenSelectedHandler(createClickDataFlowElementHandler(skin));
        addTextPropertyChangeListener(skin, gNode);
        return skin;
    }

    private void addTextPropertyChangeListener(DataFlowElement element, GNode node) {
        element.textProperty().addListener((observableValue, oldVal, newVal) -> {
            if (!oldVal.isBlank() && !newVal.isBlank()) {
                for (GConnector connector : node.getConnectors()) {
                    for (GConnection connection : connector.getConnections()) {
                        for (Threat threat : threatGenerator.getAllThreatsForConnection(connection))
                            if (!threat.isModified()) {
                                threat.updateThreatElementNames(oldVal.trim(), newVal.trim());
                            }
                    }
                }
            }
        });
    }

    public void undo() {
        GModel model = graphEditor.getModel();
        EditingDomain editingDomain = AdapterFactoryEditingDomain.getEditingDomainFor(model);
        CommandStack commandStack = editingDomain.getCommandStack();
        if (commandStack.canUndo()) {
            Command command = commandStack.getUndoCommand();
            if (command instanceof CompoundCommand) {
                LOGGER.info("cmd amount " + ((CompoundCommand) command).getCommandList().size());
                ((CompoundCommand) command).getCommandList().forEach(cmd -> {
                    LOGGER.info("one cmd");
                    undoCommand(cmd, commandStack);
                    cmd.undo();
                });
            }
            commandStack.undo();
        }


    }

    private void undoCommand(Command cmd, CommandStack commandStack) {
        if (cmd instanceof RemoveCommand && !(cmd.getAffectedObjects().toArray()[0] instanceof GConnector)) {
            Pair<String, String> pair = typeNameDeletedItems.pop();
            String type = pair.getKey();
            String name = pair.getValue();
            LOGGER.info("type" + type);
            if (type.equals(DataFlowJointSkin.ELEMENT_TYPE)) {
                activateCorrespondingConnectionFactory(type);
            } else {
                activateCorrespondingNodeFactory(type);
            }
        }
    }

    public void redo() {
        Commands.redo(graphEditor.getModel());
    }

    public void copy() {
        selectionCopier.copy();
    }

    public void paste() {
        selectionCopier.paste(null);
    }

    public void deleteSelection() {
        List<GConnection> connections = new ArrayList<>();
        GModel model = graphEditor.getModel();
        EditingDomain editingDomain = AdapterFactoryEditingDomain.getEditingDomainFor(model);
        ObservableSet<EObject> selectedItems = getSelectionManager().getSelectedItems();
        selectedItems.forEach(elem -> {
            if (elem instanceof GJoint) {
                connections.add(((GJoint) elem).getConnection());
            }
        });
        selectedItems.addAll(connections);
        SkinLookup skinLookup = graphEditor.getSkinLookup();
        for (final GNode node : model.getNodes()) {
            if (graphEditor.getSelectionManager().isSelected(node)) {
                GenericNodeSkin nodeSkin = (GenericNodeSkin) skinLookup.lookupNode(node);
                typeNameDeletedItems.push(new Pair<>(nodeSkin.getType(), nodeSkin.getText()));
            }
        }
        for (final GConnection con : model.getConnections()) {
            if (graphEditor.getSelectionManager().isSelected(con)) {
                DataFlowJointSkin jointSkin = (DataFlowJointSkin) skinLookup.lookupJoint(con.getJoints().get(0));
                typeNameDeletedItems.push(new Pair<>(DataFlowJointSkin.ELEMENT_TYPE, jointSkin.getText()));
            }
        }
        DataFlowCommands.remove(graphEditor.getSelectionManager().getSelectedItems(), editingDomain, model);
    }

    public void clearAll() {
        Commands.clear(graphEditor.getModel());
    }

    public void activateCorrespondingNodeFactory(String type) {
        switch (type) {
            case DataStoreNodeSkin
                    .TITLE_TEXT:
                setNodeSkinFactory(this::createDataStoreSkin);
                break;
            case ExternalEntityNodeSkin
                    .TITLE_TEXT:
                setNodeSkinFactory(this::createExternalEntitySkin);
                break;
            case ProcessNodeSkin
                    .TITLE_TEXT:
                setNodeSkinFactory(this::createProcessSkin);
                break;
            case MultipleProcessNodeSkin
                    .TITLE_TEXT:
                setNodeSkinFactory(this::createMultipleProcessSkin);
                break;
            default:
                LOGGER.warning("Could not find type of node, fall back to default");
                setNodeSkinFactory(this::createExternalEntitySkin);
                break;

        }
    }

    public void activateCorrespondingConnectionFactory(String type) {
        //should be expanded if more connection types exist
        //do not forget tail skin if that changed as well
        switch (type) {
            default:
                setConnectionSkinFactory(this::createConnectionSkin);
                setConnectorSkinFactory(this::createConnectorSkin);
                setJointSkinFactory(this::createJointSkin);
                break;

        }
    }

}
