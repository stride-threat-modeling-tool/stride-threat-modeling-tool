package ch.zhaw.threatmodeling.skin.controller;

import ch.zhaw.threatmodeling.model.Threat;
import ch.zhaw.threatmodeling.model.ThreatGenerator;
import ch.zhaw.threatmodeling.persistence.utils.objects.DataFlowConnectionObject;
import ch.zhaw.threatmodeling.persistence.utils.objects.DataFlowNodeObject;
import ch.zhaw.threatmodeling.persistence.utils.objects.DataFlowPositionedObject;
import ch.zhaw.threatmodeling.selections.SelectionCopier;
import ch.zhaw.threatmodeling.skin.DataFlowElement;
import ch.zhaw.threatmodeling.skin.DataFlowGraphEditor;
import ch.zhaw.threatmodeling.skin.DataFlowSkinConstants;
import ch.zhaw.threatmodeling.skin.SkinController;
import ch.zhaw.threatmodeling.skin.connection.DataFlowConnectionSkin;
import ch.zhaw.threatmodeling.skin.connection.TrustBoundaryConnectionSkin;
import ch.zhaw.threatmodeling.skin.connector.DataFlowConnectorSkin;
import ch.zhaw.threatmodeling.skin.connector.TrustBoundaryConnectorSkin;
import ch.zhaw.threatmodeling.skin.joint.DataFlowJointSkin;
import ch.zhaw.threatmodeling.skin.joint.TrustBoundaryJointSkin;
import ch.zhaw.threatmodeling.skin.nodes.datastore.DataStoreNodeSkin;
import ch.zhaw.threatmodeling.skin.nodes.externalentity.ExternalEntityNodeSkin;
import ch.zhaw.threatmodeling.skin.nodes.generic.GenericNodeSkin;
import ch.zhaw.threatmodeling.skin.nodes.multipleprocess.MultipleProcessNodeSkin;
import ch.zhaw.threatmodeling.skin.nodes.process.ProcessNodeSkin;
import ch.zhaw.threatmodeling.skin.nodes.trustboundary.TrustBoundaryNodeSkin;
import ch.zhaw.threatmodeling.skin.tail.DataFlowTailSkin;
import ch.zhaw.threatmodeling.skin.utils.DataFlowCommands;
import ch.zhaw.threatmodeling.skin.utils.DataFlowConnectionCommands;
import ch.zhaw.threatmodeling.skin.utils.DataFlowNodeCommands;
import de.tesis.dynaware.grapheditor.Commands;
import de.tesis.dynaware.grapheditor.GConnectionSkin;
import de.tesis.dynaware.grapheditor.GConnectorSkin;
import de.tesis.dynaware.grapheditor.GJointSkin;
import de.tesis.dynaware.grapheditor.GNodeSkin;
import de.tesis.dynaware.grapheditor.GTailSkin;
import de.tesis.dynaware.grapheditor.GraphEditor;
import de.tesis.dynaware.grapheditor.SelectionManager;
import de.tesis.dynaware.grapheditor.SkinLookup;
import de.tesis.dynaware.grapheditor.core.view.GraphEditorContainer;
import de.tesis.dynaware.grapheditor.model.GConnection;
import de.tesis.dynaware.grapheditor.model.GConnector;
import de.tesis.dynaware.grapheditor.model.GJoint;
import de.tesis.dynaware.grapheditor.model.GModel;
import de.tesis.dynaware.grapheditor.model.GNode;
import de.tesis.dynaware.grapheditor.model.GraphFactory;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableSet;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import javafx.util.Pair;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.logging.Logger;

public class DataFlowDiagramSkinController implements SkinController {
    protected static final int NODE_INITIAL_X = 19;
    protected static final int NODE_INITIAL_Y = 19;
    private static final int TRUST_BOUNDARY_NODE_SIZE = 15;
    private static final int TRUST_BOUNDARY_CONNECTION_OFFSET = 300;
    private static final Logger LOGGER = Logger.getLogger("Data Flow Controller");

    protected final DataFlowGraphEditor graphEditor;
    protected final GraphEditorContainer graphEditorContainer;
    private final ObjectProperty<DataFlowElement> currentElement = new SimpleObjectProperty<>();
    private final ThreatGenerator threatGenerator;
    private final SelectionCopier selectionCopier;
    private final Deque<Integer> lastCommandDeletedCount = new ArrayDeque<>();
    private final Deque<Integer> lastCommandUndoCount = new ArrayDeque<>();
    private final Map<Command, Pair<String, String>> deleteCommandToTypeTextMapping = new HashMap<>();
    private final Map<Command, String> createCommandToTypeMapping = new HashMap<>();

    public DataFlowDiagramSkinController(final GraphEditor graphEditor, final GraphEditorContainer container, final ThreatGenerator threatGenerator) {
        this.graphEditor = (DataFlowGraphEditor) graphEditor;
        this.selectionCopier = new SelectionCopier(graphEditor.getSkinLookup(), getSelectionManager(), this);
        selectionCopier.initialize(graphEditor.getModel());
        this.graphEditorContainer = container;
        this.threatGenerator = threatGenerator;
        setDataFlowSkinFactories();
    }

    private void setDataFlowSkinFactories() {
        graphEditor.setConnectorSkinFactory(this::createConnectorSkin);
        graphEditor.setTailSkinFactory(this::createTailSkin);
        graphEditor.setJointSkinFactory(this::createJointSkin);
        graphEditor.setConnectionSkinFactory(this::createConnectionSkin);
    }

    private void setTrustBoundarySkinFactories() {
        graphEditor.setNodeSkinFactory(this::createTrustBoundaryNodeSkin);
        graphEditor.setConnectionSkinFactory(this::createTrustBoundaryConnectionSkin);
        graphEditor.setJointSkinFactory(this::createTrustBoundaryJointSkin);
        graphEditor.setConnectorSkinFactory(this::createTrustBoundaryConnectorSkin);
    }

    public ObjectProperty<DataFlowElement> getCurrentElement() {
        return currentElement;
    }

    @Override
    public void addNode(double currentZoomFactor, String type) {
        executeAddNodeCommand(initNode(currentZoomFactor, type), type);
    }

    private void executeAddNodeCommand(GNode node, String type){
        Commands.addNode(graphEditor.getModel(), node);
        createCommandToTypeMapping.put(
                AdapterFactoryEditingDomain.getEditingDomainFor(graphEditor.getModel()).getCommandStack().getMostRecentCommand(),
                type);
    }

    private GNode initNode(double currentZoomFactor, String type){
        final double windowXOffset = graphEditorContainer.getContentX() / currentZoomFactor;
        final double windowYOffset = graphEditorContainer.getContentY() / currentZoomFactor;
        final GNode node = GraphFactory.eINSTANCE.createGNode();
        node.setType(type);

        node.setY(NODE_INITIAL_Y + windowYOffset);
        node.setX(NODE_INITIAL_X + windowXOffset);
        node.setId(allocateNewId());

        if (!type.equals(TrustBoundaryNodeSkin.TITLE_TEXT)) {
            for (String currentType : DataFlowSkinConstants.DFD_CONNECTOR_LAYOUT_ORDER) {
                addConnectorToNode(node, currentType);
            }
        } else {
            // Trust Boundary nodes only have one connector
            addConnectorToNode(node, DataFlowSkinConstants.DFD_TRUST_BOUNDARY_CONNECTOR);
        }
        return node;
    }

    private void addConnectorToNode(GNode node, String type) {
        final GConnector connector = GraphFactory.eINSTANCE.createGConnector();
        node.getConnectors().add(connector);
        connector.setType(type);
    }


    public void addDataStore(double currentZoomFactor) {
        setNodeSkinFactory(this::createDataStoreSkin);
        setDataFlowSkinFactories();
        addNode(currentZoomFactor, DataStoreNodeSkin.TITLE_TEXT);
    }

    public void addExternalEntity(double currentZoomFactor) {
        setNodeSkinFactory(this::createExternalEntitySkin);
        setDataFlowSkinFactories();
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
        setDataFlowSkinFactories();
        addNode(currentZoomFactor, ProcessNodeSkin.TITLE_TEXT);

    }

    public void addMultipleProcess(double currentZoomFactor) {
        setNodeSkinFactory(this::createMultipleProcessSkin);
        setDataFlowSkinFactories();
        addNode(currentZoomFactor, MultipleProcessNodeSkin.TITLE_TEXT);

    }

    /**
     * Adds a new Trust Boundary to the Scene consisting of two {@link TrustBoundaryNodeSkin} with each
     * one {@link TrustBoundaryConnectorSkin} connected through a {@link TrustBoundaryConnectionSkin} and a
     * {@link TrustBoundaryJointSkin}.
     *
     * @param currentZoomFactor
     */
    public void addTrustBoundary(double currentZoomFactor) {
        // Set SkinFactories to the TrustBoundary skins
        setTrustBoundarySkinFactories();

        // The last two added nodes have to be retrieved to create a connection between them
        GNode startNode = initNode(currentZoomFactor, TrustBoundaryNodeSkin.TITLE_TEXT);
        GNode endNode = initNode(currentZoomFactor, TrustBoundaryNodeSkin.TITLE_TEXT);

        GConnector startConnector = startNode.getConnectors().get(0);
        GConnector endConnector = endNode.getConnectors().get(0);

        // Offset second node to the right so that the trust boundary becomes visible
        endNode.setX(endNode.getX() + TRUST_BOUNDARY_CONNECTION_OFFSET);

        // Change default size of nodes (151 px / 100 px)
        startNode.setHeight(TRUST_BOUNDARY_NODE_SIZE);
        startNode.setWidth(TRUST_BOUNDARY_NODE_SIZE);
        endNode.setHeight(TRUST_BOUNDARY_NODE_SIZE);
        endNode.setWidth(TRUST_BOUNDARY_NODE_SIZE);
        final List<GJoint> joints = new ArrayList<>();
        final Point2D startPoint = new Point2D(NODE_INITIAL_X + startNode.getWidth() / 2, NODE_INITIAL_Y + startNode.getHeight() / 2);
        final Point2D endPoint = new Point2D(NODE_INITIAL_X + endNode.getWidth() / 2 + TRUST_BOUNDARY_CONNECTION_OFFSET, NODE_INITIAL_Y + endNode.getHeight() / 2);
        final Point2D jointPosition = startPoint.midpoint(endPoint).add(DataFlowSkinConstants.DFD_JOINT_SPAWN_OFFSET, DataFlowSkinConstants.DFD_JOINT_SPAWN_OFFSET);

        initTrustBoundaryJoints(jointPosition, joints);

        CompoundCommand addTrustBoundaryCommand = DataFlowNodeCommands.addTrustBoundary(
                startNode,
                endNode,
                graphEditor.getModel(),
                startConnector,
                endConnector,
                TrustBoundaryNodeSkin.TITLE_TEXT,
                joints,
                graphEditor.getConnectionEventManager()
                );

        createCommandToTypeMapping.put(addTrustBoundaryCommand, TrustBoundaryNodeSkin.TITLE_TEXT);

        // Set SkinFactories back to the normal DataFlow element skins
        setDataFlowSkinFactories();
    }

    private void initTrustBoundaryJoints(Point2D jointPosition, List<GJoint> joints) {
        final GJoint joint = createJoint();
        joint.setX(jointPosition.getX());
        joint.setY(jointPosition.getY());
        joint.setType(TrustBoundaryJointSkin.ELEMENT_TYPE);
        joints.add(joint);
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

    private GNodeSkin createTrustBoundaryNodeSkin(GNode gNode) {
        return new TrustBoundaryNodeSkin(gNode);
    }

    private GJointSkin createTrustBoundaryJointSkin(final GJoint joint) {
        TrustBoundaryJointSkin skin = new TrustBoundaryJointSkin(joint);
        skin.setHasBeenSelectedHandler(createClickDataFlowElementHandler(skin));
        return skin;
    }

    private GConnectorSkin createTrustBoundaryConnectorSkin(final GConnector connector) {
        return new TrustBoundaryConnectorSkin(connector);
    }

    private GConnectionSkin createTrustBoundaryConnectionSkin(GConnection gConnection) {
        return new TrustBoundaryConnectionSkin(gConnection);
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
        EditingDomain editingDomain = AdapterFactoryEditingDomain.getEditingDomainFor(graphEditor.getModel());
        CommandStack commandStack = editingDomain.getCommandStack();
        boolean isRemoveCommand = false;
        boolean isAddCommand = false;
        int toUndoCount = -1;
        do {
            if (commandStack.canUndo()) {
                Command currentCommand = commandStack.getUndoCommand();
                isRemoveCommand =  undoSingleCommand(currentCommand, commandStack);
                isAddCommand = currentCommand instanceof AddCommand;

                if ((isRemoveCommand || isAddCommand)&& toUndoCount == -1 && null != deleteCommandToTypeTextMapping.get(currentCommand)) {
                    toUndoCount = lastCommandDeletedCount.pop();
                    lastCommandUndoCount.push(toUndoCount);
                }
            }
            toUndoCount = toUndoCount - 1;
        } while (toUndoCount > 0 && (isRemoveCommand || isAddCommand));


    }

    public void redo(){
        EditingDomain editingDomain = AdapterFactoryEditingDomain.getEditingDomainFor(graphEditor.getModel());
        CommandStack commandStack = editingDomain.getCommandStack();
        boolean isRemoveCommand = false;
        int toRedoCount = -1;
        do {
            if (commandStack.canRedo()) {
                Command currentCommand = commandStack.getRedoCommand();
                isRemoveCommand =  redoSingleCommand(currentCommand, commandStack);

                if (isRemoveCommand && toRedoCount == -1 && null != deleteCommandToTypeTextMapping.get(currentCommand)) {
                    toRedoCount = lastCommandUndoCount.pop();
                    lastCommandDeletedCount.push(toRedoCount);

                }
            }
            toRedoCount = toRedoCount - 1;
        } while (toRedoCount > 0 && isRemoveCommand);
    }

    private boolean redoSingleCommand(Command command, CommandStack stack){
        boolean isRemove = command instanceof RemoveCommand;
        String type = createCommandToTypeMapping.get(command);
        if(null != type){
            if(command instanceof CompoundCommand){
                //redo trust boundary
                setTrustBoundarySkinFactories();
            } else {
                if (DataFlowConnectionCommands.isConnectionType(type)) {
                    activateCorrespondingConnectionFactory(type);
                } else {
                    activateCorrespondingNodeFactory(type);
                }
            }
        }
        stack.redo();
        setDataFlowSkinFactories();
        return isRemove;
    }

    private void resetNodeAndConnectionNames(String text,  List<GNode> oldNodes, List<GConnection> oldConnections) {
        GModel model = graphEditor.getModel();
        model.getConnections().forEach(connection -> {
            if (!oldConnections.contains(connection)) {
                resetRemovedJointName(text, connection);
            }
        });
        model.getNodes().forEach(gNode ->
        {
            if (!oldNodes.contains(gNode)) {
                resetRemoveNodeName(text, gNode);
            }
        });
    }

    private boolean undoSingleCommand(Command command, CommandStack stack) {
        boolean isRemove = command instanceof RemoveCommand;
        GModel model = graphEditor.getModel();
        final List<GNode> oldNodes = new ArrayList<>(model.getNodes());
        final List<GConnection> oldConnections = new ArrayList<>(model.getConnections());
        Pair<String, String> typeTextPair = deleteCommandToTypeTextMapping.get(command);
        if (isRemove && null != typeTextPair) {
            String type = typeTextPair.getKey();
            if (DataFlowConnectionCommands.isConnectionType(type)) {
                activateCorrespondingConnectionFactory(type);
            } else {
                activateCorrespondingNodeFactory(type);
            }

        }
        stack.undo();
        if (isRemove && null != typeTextPair) {
           resetNodeAndConnectionNames(typeTextPair.getValue(), oldNodes, oldConnections);

        }
        return isRemove;
    }

    private void resetRemoveNodeName(String name, GNode node) {
        SkinLookup skinLookup = graphEditor.getSkinLookup();
        GenericNodeSkin nodeSkin = (GenericNodeSkin) skinLookup.lookupNode(node);
        nodeSkin.setText(name);

    }

    private void resetRemovedJointName(String name, GConnection connection) {
        //expand for other connection types
       DataFlowConnectionCommands.setJointLabel(connection, name, graphEditor.getSkinLookup());

    }

    private void flushCommandStack(){
        AdapterFactoryEditingDomain.getEditingDomainFor(graphEditor.getModel()).getCommandStack().flush();
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

        lastCommandDeletedCount.push(
                DataFlowCommands.remove(
                        deleteCommandToTypeTextMapping,
                        skinLookup,
                        graphEditor.getSelectionManager().getSelectedItems(),
                        editingDomain,
                        model));
    }

    public void clearAll() {
        Commands.clear(graphEditor.getModel());
        flushCommandStack();
    }

    public void activateCorrespondingNodeFactory(String type) {
        setDataFlowSkinFactories();
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
            case TrustBoundaryNodeSkin
                    .TITLE_TEXT:
                setTrustBoundarySkinFactories();
                break;
            default:
                LOGGER.warning("Could not find type of node, fall back to default: " + type);
                setNodeSkinFactory(this::createExternalEntitySkin);
                break;

        }
    }

    public void activateCorrespondingConnectionFactory(String type) {
        switch (type) {
            case TrustBoundaryJointSkin.ELEMENT_TYPE:
                setTrustBoundarySkinFactories();
                break;
            default:
                setDataFlowSkinFactories();
                break;

        }
    }

    public void restoreModel(Pair<List<DataFlowNodeObject>, List<DataFlowConnectionObject>> loadedObjects, double currentZoomFactor) {
        clearAll();
        loadedObjects.getKey().forEach(dataFlowNodeObject -> restoreNode(dataFlowNodeObject, currentZoomFactor));
        loadedObjects.getValue().forEach(this::restoreConnection);
        flushCommandStack();
    }

    private void restoreConnection(DataFlowConnectionObject connectionObject) {
        activateCorrespondingConnectionFactory(connectionObject.getType());
        final GModel model = graphEditor.getModel();
        final List<GNode> nodes = model.getNodes();

        final GNode srcNode = nodes.get(connectionObject.getSourceNodeIndex());
        final GNode destNode =  nodes.get(connectionObject.getTargetNodeIndex());
        final GConnector srcConnector = srcNode.getConnectors().get(connectionObject.getSourceConnectorIndex());
        final GConnector destConnector = destNode.getConnectors().get(connectionObject.getTargetConnectorIndex());

        final List<GJoint> joints = new ArrayList<>();
        final String type = connectionObject.getType();
        final DataFlowPositionedObject jointObject = connectionObject.getJoint();
        joints.add(restoreJoint(jointObject, type));

        DataFlowConnectionCommands.addConnection(
                graphEditor.getModel(),
                srcConnector,
                destConnector,
                type,
                joints,
                graphEditor.getConnectionEventManager(),
                null);

       DataFlowConnectionCommands.setJointLabel(joints.get(0).getConnection(), jointObject.getText(), graphEditor.getSkinLookup());
    }

    private GJoint restoreJoint(DataFlowPositionedObject jointObject, String type){
        final GJoint joint = createJoint();
        joint.setType(type);
        joint.setX(jointObject.getX());
        joint.setY(jointObject.getY());
        return joint;
    }


    private GJoint createJoint() {
        return GraphFactory.eINSTANCE.createGJoint();
    }

    private void restoreNode(DataFlowNodeObject nodeObject, double currentZoomFactor) {
        String type = nodeObject.getType();
        String text = nodeObject.getText();
        activateCorrespondingNodeFactory(type);
        GNode newNode = initNode(currentZoomFactor, type);
        newNode.setX(nodeObject.getX());
        newNode.setY(nodeObject.getY());
        newNode.setHeight(nodeObject.getHeight());
        newNode.setWidth(nodeObject.getWidth());
        executeAddNodeCommand(newNode, type);

        GenericNodeSkin skin = (GenericNodeSkin) graphEditor.getSkinLookup().lookupNode(newNode);
        if(null != text){
            skin.setText(text);
        }
    }
}
