package ch.zhaw.skin;

import de.tesis.dynaware.grapheditor.GraphEditor;
import de.tesis.dynaware.grapheditor.core.view.GraphEditorContainer;
import javafx.geometry.Side;

public class DataFlowDiagramSkin implements SkinController {

    protected final GraphEditor graphEditor;
    protected final GraphEditorContainer graphEditorContainer;


    public  DataFlowDiagramSkin(final GraphEditor graphEditor, final GraphEditorContainer container) {
       this.graphEditor = graphEditor;
       this.graphEditorContainer = container;
    }

    @Override
    public void addNode(double currentZoomFactor) {

    }

    @Override
    public void activate() {

    }

    @Override
    public void addConnector(Side position, boolean input) {

    }

    @Override
    public void clearConnectors() {

    }

    @Override
    public void handleSelectAll() {

    }
}
