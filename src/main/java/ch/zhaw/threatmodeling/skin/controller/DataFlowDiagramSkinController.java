package ch.zhaw.threatmodeling.skin.controller;

import ch.zhaw.threatmodeling.skin.DataFlowElement;
import ch.zhaw.threatmodeling.skin.DataFlowSkinConstants;
import ch.zhaw.threatmodeling.skin.SkinController;
import ch.zhaw.threatmodeling.skin.connection.DataFlowConnectionSkin;
import ch.zhaw.threatmodeling.skin.connector.DataFlowConnectorSkin;
import ch.zhaw.connectors.DataFlowConnectorTypes;
import ch.zhaw.threatmodeling.skin.joint.DataFlowJointSkin;
import ch.zhaw.threatmodeling.skin.nodes.datastore.DataStoreNodeSkin;
import ch.zhaw.threatmodeling.skin.nodes.externalentity.ExternalEntityNodeSkin;
import ch.zhaw.threatmodeling.skin.tail.DataFlowTailSkin;
import de.tesis.dynaware.grapheditor.*;
import de.tesis.dynaware.grapheditor.core.view.GraphEditorContainer;
import de.tesis.dynaware.grapheditor.model.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;

import java.util.List;
import java.util.OptionalInt;
import java.util.logging.Logger;

public class DataFlowDiagramSkinController implements SkinController {
    protected final GraphEditor graphEditor;
    protected final GraphEditorContainer graphEditorContainer;
    protected static final int NODE_INITIAL_X = 19;
    protected static final int NODE_INITIAL_Y = 19;
    private static final int MAX_CONNECTOR_COUNT = 5;
    private static final Logger LOGGER = Logger.getLogger("Data Flow Controller");

    private final ObjectProperty<DataFlowElement> currentElement = new SimpleObjectProperty<>();

    public ObjectProperty<DataFlowElement> getCurrentElement(){
        return currentElement;
    }

    public DataFlowDiagramSkinController(final GraphEditor graphEditor, final GraphEditorContainer container) {
        this.graphEditor = graphEditor;
        this.graphEditorContainer = container;
        graphEditor.setConnectorSkinFactory(this::createConnectorSkin);
        graphEditor.setTailSkinFactory(this::createTailSkin);
        graphEditor.setJointSkinFactory(this::createJointSkin);
        graphEditor.setConnectionSkinFactory(this::createConnectionSkin);
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

        // A node has 4 bidirectional connectors
        addConnectorToNode(node, DataFlowSkinConstants.DFD_BOTTOM_CONNECTOR);
        addConnectorToNode(node, DataFlowSkinConstants.DFD_TOP_CONNECTOR);

        for(int i = 0; i < 3; i++){
            addConnectorToNode(node, DataFlowSkinConstants.DFD_LEFT_CONNECTOR);
            addConnectorToNode(node, DataFlowSkinConstants.DFD_RIGHT_CONNECTOR);
        }

        Commands.addNode(graphEditor.getModel(), node);
    }

    private void addConnectorToNode(GNode node, String type){
        final GConnector connector = GraphFactory.eINSTANCE.createGConnector();
        node.getConnectors().add(connector);
        connector.setType(type);
    }


    public void addDataStore(double currentZoomFactor) {
        graphEditor.setNodeSkinFactory(this::createDataStoreSkin);
        addNode(currentZoomFactor, DataStoreNodeSkin.TITLE_TEXT);
    }

    public void addExternalEntity(double currentZoomFactor) {
        graphEditor.setNodeSkinFactory(this::createExternalEntitySkin);
        addNode(currentZoomFactor, ExternalEntityNodeSkin.TITLE_TEXT);

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

    @Override
    public void clearConnectors() {

    }

    @Override
    public void handleSelectAll() {

    }

    private EventHandler<MouseEvent> createClickDataFlowElementHandler(DataFlowElement element){
        return mouseEvent -> {
            if(MouseButton.PRIMARY.equals(mouseEvent.getButton())){
                this.currentElement.set(element);
            }
            mouseEvent.consume();
        };
    }

    private GNodeSkin createDataStoreSkin(final GNode node) {
        DataStoreNodeSkin skin = new DataStoreNodeSkin(node);
        skin.setHasBeenSelectedHandler(createClickDataFlowElementHandler(skin));
        return skin;
    }

    private GNodeSkin createExternalEntitySkin(final GNode node) {
        ExternalEntityNodeSkin skin = new ExternalEntityNodeSkin(node);
        skin.setHasBeenSelectedHandler(createClickDataFlowElementHandler(skin));
        return skin;
    }

    private GJointSkin createJointSkin(final GJoint joint){

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
}
