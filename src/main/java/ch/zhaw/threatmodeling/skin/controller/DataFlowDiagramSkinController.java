package ch.zhaw.threatmodeling.skin.controller;

import ch.zhaw.threatmodeling.connectors.DataFlowConnectorTypes;
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
import de.tesis.dynaware.grapheditor.model.GraphPackage;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableSet;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.logging.Logger;

public class DataFlowDiagramSkinController implements SkinController {
    protected static final int NODE_INITIAL_X = 19;
    protected static final int NODE_INITIAL_Y = 19;
    private static final int MAX_CONNECTOR_COUNT = 5;
    private static final Logger LOGGER = Logger.getLogger("Data Flow Controller");
    protected final GraphEditor graphEditor;
    protected final GraphEditorContainer graphEditorContainer;
    private final ObjectProperty<DataFlowElement> currentElement = new SimpleObjectProperty<>();
    private final ThreatGenerator threatGenerator;
    private final SelectionCopier selectionCopier;

    public DataFlowDiagramSkinController(final GraphEditor graphEditor, final GraphEditorContainer container, final ThreatGenerator threatGenerator) {
        this.graphEditor = graphEditor;
        this.selectionCopier = new SelectionCopier(graphEditor.getSkinLookup(), getSelectionManager(), this);
        selectionCopier.initialize(graphEditor.getModel());
        this.graphEditorContainer = container;
        this.threatGenerator = threatGenerator;
        graphEditor.setConnectorSkinFactory(this::createConnectorSkin);
        graphEditor.setTailSkinFactory(this::createTailSkin);
        graphEditor.setJointSkinFactory(this::createJointSkin);
        graphEditor.setConnectionSkinFactory(this::createConnectionSkin);

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
        final String type = getType(position);

        final GModel model = graphEditor.getModel();
        final SkinLookup skinLookup = graphEditor.getSkinLookup();
        final CompoundCommand command = new CompoundCommand();
        final EditingDomain editingDomain = AdapterFactoryEditingDomain.getEditingDomainFor(model);

        for (final GNode node : model.getNodes()) {

            if (skinLookup.lookupNode(node).isSelected()) {
                if (countConnectors(node, position) < MAX_CONNECTOR_COUNT) {

                    final GConnector connector = GraphFactory.eINSTANCE.createGConnector();
                    connector.setType(type);

                    final EReference connectors = GraphPackage.Literals.GNODE__CONNECTORS;
                    command.append(AddCommand.create(editingDomain, node, connectors, connector));
                }
            }

        }

        if (command.canExecute()) {
            editingDomain.getCommandStack().execute(command);
        }

    }

    private String getType(Side position) {
        switch (position) {
            case TOP:
                return DataFlowConnectorTypes.TOP;
            case RIGHT:
                return DataFlowConnectorTypes.RIGHT;
            case BOTTOM:
                return DataFlowConnectorTypes.BOTTOM;
            case LEFT:
                return DataFlowConnectorTypes.LEFT;
        }
        return null;
    }

    private int countConnectors(final GNode node, final Side side) {

        int count = 0;

        for (final GConnector connector : node.getConnectors()) {
            if (side.equals(DataFlowConnectorTypes.getSide(connector.getType()))) {
                count++;
            }
        }

        return count;
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
                if(element instanceof DataFlowJointSkin){
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

    private GJointSkin createJointSkin(final GJoint joint) {
        DataFlowJointSkin skin = new DataFlowJointSkin(joint);
        skin.setHasBeenSelectedHandler(createClickDataFlowElementHandler(skin));
        return skin;
    }

    private GConnectorSkin createConnectorSkin(final GConnector connector) {
        return new DataFlowConnectorSkin(connector);
    }

    private GTailSkin createTailSkin(final GConnector connector) {
        return new DataFlowTailSkin(connector);
    }

    private GConnectionSkin createConnectionSkin(GConnection gConnection) {
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
        Commands.undo(graphEditor.getModel());
    }

    public void redo() {
        Commands.redo(graphEditor.getModel());
    }

    public void copy() {
        selectionCopier.copy();
    }

    public void paste() {
        graphEditor.setNodeSkinFactory(this::createMultipleProcessSkin);
        selectionCopier.paste(null);
    }

    public void deleteSelection() {
        List<GConnection> connections = new ArrayList<>();
        ObservableSet<EObject> selectedItems =  getSelectionManager().getSelectedItems();
        selectedItems.forEach(elem -> {
            if (elem instanceof GJoint) {
                connections.add(((GJoint)elem).getConnection());
            }
        });
        selectedItems.addAll(connections);
        graphEditor.delete(getSelectionManager().getSelectedItems());
    }

    public void clearAll() {
        Commands.clear(graphEditor.getModel());
    }

}
