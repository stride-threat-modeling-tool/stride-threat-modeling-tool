package ch.zhaw.controller;

import de.tesis.dynaware.grapheditor.Commands;
import de.tesis.dynaware.grapheditor.GraphEditor;
import de.tesis.dynaware.grapheditor.core.DefaultGraphEditor;
import de.tesis.dynaware.grapheditor.core.view.GraphEditorContainer;
import de.tesis.dynaware.grapheditor.model.GConnector;
import de.tesis.dynaware.grapheditor.model.GModel;
import de.tesis.dynaware.grapheditor.model.GNode;
import de.tesis.dynaware.grapheditor.model.GraphFactory;

public class MainController {
    private final GraphEditor graphEditor = new DefaultGraphEditor();
    public GraphEditorContainer graphEditorContainer;


    public void initialize() {

        GModel model = GraphFactory.eINSTANCE.createGModel();
        graphEditor.setModel(model);
        addNodes(model);

        graphEditorContainer.setGraphEditor(graphEditor);


    }

    private GNode createNode()
    {
        GNode node = GraphFactory.eINSTANCE.createGNode();

        GConnector input = GraphFactory.eINSTANCE.createGConnector();
        GConnector output = GraphFactory.eINSTANCE.createGConnector();

        input.setType("left-input");
        output.setType("right-output");

        node.getConnectors().add(input);
        node.getConnectors().add(output);

        return node;
    }

    private void addNodes(GModel model)
    {
        GNode firstNode = createNode();
        GNode secondNode = createNode();

        firstNode.setX(150);
        firstNode.setY(150);

        secondNode.setX(400);
        secondNode.setY(200);
        secondNode.setWidth(200);
        secondNode.setHeight(150);

        Commands.addNode(model, firstNode);
        Commands.addNode(model, secondNode);
    }

}
