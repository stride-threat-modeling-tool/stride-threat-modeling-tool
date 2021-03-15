package ch.zhaw.skin;

import ch.zhaw.skin.datastore.DataStoreNodeSkin;
import ch.zhaw.skin.externalentity.ExternalEntityNodeSkin;
import de.tesis.dynaware.grapheditor.*;
import ch.zhaw.connectors.DataFlowConnectorTypes;
import de.tesis.dynaware.grapheditor.core.view.GraphEditorContainer;
import de.tesis.dynaware.grapheditor.model.*;
import javafx.geometry.Side;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;

import java.util.List;
import java.util.OptionalInt;

public class DataFlowDiagramSkinController implements SkinController {

    protected final GraphEditor graphEditor;
    protected final GraphEditorContainer graphEditorContainer;
    protected static final int NODE_INITIAL_X = 19;
    protected static final int NODE_INITIAL_Y = 19;
    private static final int MAX_CONNECTOR_COUNT = 5;


    public DataFlowDiagramSkinController(final GraphEditor graphEditor, final GraphEditorContainer container) {
        this.graphEditor = graphEditor;
        this.graphEditorContainer = container;
        graphEditor.setConnectorSkinFactory(this::createConnectorSkin);
        graphEditor.setTailSkinFactory(this::createTailSkin);
        graphEditor.setJointSkinFactory(this::createJointSkin);
    }

    @Override
    public void addNode(double currentZoomFactor) {
        final double windowXOffset = graphEditorContainer.getContentX() / currentZoomFactor;
        final double windowYOffset = graphEditorContainer.getContentY() / currentZoomFactor;

        final GNode node = GraphFactory.eINSTANCE.createGNode();
        node.setY(NODE_INITIAL_Y + windowYOffset);

        node.setType(DataFlowSkinConstants.DFD_NODE);
        node.setX(NODE_INITIAL_X + windowXOffset);
        node.setId(allocateNewId());

        // A node has 4 bidirectional connectors
        final GConnector left = GraphFactory.eINSTANCE.createGConnector();
        node.getConnectors().add(left);
        left.setType(DataFlowSkinConstants.DFD_LEFT_CONNECTOR);

        final GConnector top = GraphFactory.eINSTANCE.createGConnector();
        node.getConnectors().add(top);
        top.setType(DataFlowSkinConstants.DFD_TOP_CONNECTOR);

        final GConnector right = GraphFactory.eINSTANCE.createGConnector();
        node.getConnectors().add(right);
        right.setType(DataFlowSkinConstants.DFD_RIGHT_CONNECTOR);

        final GConnector bottom = GraphFactory.eINSTANCE.createGConnector();
        node.getConnectors().add(bottom);
        bottom.setType(DataFlowSkinConstants.DFD_BOTTOM_CONNECTOR);

        Commands.addNode(graphEditor.getModel(), node);
    }


    public void addDataStore(double currentZoomFactor) {
        graphEditor.setNodeSkinFactory(this::createDataStoreSkin);
        addNode(currentZoomFactor);
    }

    public void addExternalEntity(double currentZoomFactor) {
        graphEditor.setNodeSkinFactory(this::createExternalEntitySkin);
        addNode(currentZoomFactor);

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

    private GNodeSkin createDataStoreSkin(final GNode node) {
        return new DataStoreNodeSkin(node);
    }

    private GNodeSkin createExternalEntitySkin(final GNode node) {
        return new ExternalEntityNodeSkin(node);
    }

    private GJointSkin createJointSkin(final GJoint joint){
        return new DataFlowJointSkin(joint);
    }

    private GConnectorSkin createConnectorSkin(final GConnector connector) {
        return new DataFlowConnectorSkin(connector);
    }

    private GTailSkin createTailSkin(final GConnector connector) {
        return new DataFlowTailSkin(connector);
    }

}
