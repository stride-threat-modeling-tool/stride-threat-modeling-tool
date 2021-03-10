package ch.zhaw.skin;

import de.tesis.dynaware.grapheditor.*;
import de.tesis.dynaware.grapheditor.core.connectors.DefaultConnectorTypes;
import de.tesis.dynaware.grapheditor.core.skins.defaults.DefaultConnectorSkin;
import de.tesis.dynaware.grapheditor.core.skins.defaults.DefaultNodeSkin;
import de.tesis.dynaware.grapheditor.core.skins.defaults.DefaultTailSkin;
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
    }

    @Override
    public void addNode(double currentZoomFactor) {
        final double windowXOffset = graphEditorContainer.getContentX() / currentZoomFactor;
        final double windowYOffset = graphEditorContainer.getContentY() / currentZoomFactor;

        final GNode node = GraphFactory.eINSTANCE.createGNode();
        node.setY(NODE_INITIAL_Y + windowYOffset);

        node.setType(TitledSkinConstants.TITLED_NODE);
        node.setX(NODE_INITIAL_X + windowXOffset);
        node.setId(allocateNewId());

        final GConnector input = GraphFactory.eINSTANCE.createGConnector();
        node.getConnectors().add(input);
        input.setType(TitledSkinConstants.TITLED_INPUT_CONNECTOR);

        final GConnector output = GraphFactory.eINSTANCE.createGConnector();
        node.getConnectors().add(output);
        output.setType(TitledSkinConstants.TITLED_OUTPUT_CONNECTOR);

        Commands.addNode(graphEditor.getModel(), node);
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
        graphEditor.setNodeSkinFactory(this::createNodeSkin);
        graphEditor.setConnectorSkinFactory(this::createConnectorSkin);
        graphEditor.setTailSkinFactory(this::createTailSkin);

    }

    @Override
    public void addConnector(Side position, boolean input) {
        final String type = getType(position, input);

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

    private String getType(Side position, boolean input) {
        switch (position)
        {
            case TOP:
                if (input)
                {
                    return DefaultConnectorTypes.TOP_INPUT;
                }
                return DefaultConnectorTypes.TOP_OUTPUT;
            case RIGHT:
                if (input)
                {
                    return DefaultConnectorTypes.RIGHT_INPUT;
                }
                return DefaultConnectorTypes.RIGHT_OUTPUT;
            case BOTTOM:
                if (input)
                {
                    return DefaultConnectorTypes.BOTTOM_INPUT;
                }
                return DefaultConnectorTypes.BOTTOM_OUTPUT;
            case LEFT:
                if (input)
                {
                    return DefaultConnectorTypes.LEFT_INPUT;
                }
                return DefaultConnectorTypes.LEFT_OUTPUT;
        }
        return null;
    }

    private int countConnectors(final GNode node, final Side side) {

        int count = 0;

        for (final GConnector connector : node.getConnectors()) {
            if (side.equals(DefaultConnectorTypes.getSide(connector.getType()))) {
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

    @Override
    public GNodeSkin createNodeSkin(final GNode node) {
        return TitledSkinConstants.TITLED_NODE.equals(node.getType()) ? new DataStoreNodeSkin(node) : new DefaultNodeSkin(node);
    }

    @Override
    public GConnectorSkin createConnectorSkin(final GConnector connector) {
        return TitledSkinConstants.TITLED_INPUT_CONNECTOR.equals(connector.getType()) || TitledSkinConstants.TITLED_OUTPUT_CONNECTOR.equals(connector.getType()) ?
                new TitledConnectorSkin(connector) : new DefaultConnectorSkin(connector);
    }

    @Override
    public GTailSkin createTailSkin(final GConnector connector) {
        return TitledSkinConstants.TITLED_INPUT_CONNECTOR.equals(connector.getType()) || TitledSkinConstants.TITLED_INPUT_CONNECTOR.equals(connector.getType()) ?
                new TitledTailSkin(connector) : new DefaultTailSkin(connector);
    }
}
