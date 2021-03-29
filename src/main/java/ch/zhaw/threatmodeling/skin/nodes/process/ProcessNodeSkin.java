package ch.zhaw.threatmodeling.skin.nodes.process;

import ch.zhaw.threatmodeling.skin.nodes.generic.circle.GenericEllipseNodeSkin;
import de.tesis.dynaware.grapheditor.model.GNode;
import de.tesis.dynaware.grapheditor.utils.DraggableBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Ellipse;

public class ProcessNodeSkin extends GenericEllipseNodeSkin {
    public static final String TITLE_TEXT = "Process";

    public ProcessNodeSkin(GNode node) {
        super(node);
        createContent();
    }

    @Override
    protected void createContent() {
        setText(TITLE_TEXT);
        typeProperty().set(TITLE_TEXT);
        DraggableBox root = getRoot();
        root.setMinHeight(MIN_HEIGHT);
        root.setMinWidth(MIN_WIDTH);
        Ellipse ellipse = new Ellipse();
        setEllipseProperties(ellipse);
        bindEllipseToRoot(ellipse, root);

        StackPane pane = new StackPane(ellipse, createBoundLabel());
        root.getChildren().add(pane);
    }
}
