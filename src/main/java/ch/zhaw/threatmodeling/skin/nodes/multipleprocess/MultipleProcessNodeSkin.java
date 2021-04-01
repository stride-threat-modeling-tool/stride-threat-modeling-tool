package ch.zhaw.threatmodeling.skin.nodes.multipleprocess;

import ch.zhaw.threatmodeling.skin.nodes.process.ProcessNodeSkin;
import de.tesis.dynaware.grapheditor.model.GNode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

public class MultipleProcessNodeSkin extends ProcessNodeSkin {
    public static final String TITLE_TEXT = "Multiple Process";
    public static final double INNER_ELLIPSE_OFFSET = 10;

    public MultipleProcessNodeSkin(GNode node) {
        super(node);
    }

    @Override
    protected void createContent() {
        super.createContent();
        setText(TITLE_TEXT);
        typeProperty().set(TITLE_TEXT);
        Ellipse ellipse = new Ellipse();
        ellipse.radiusYProperty().bind(ellipseWithConnectors.radiusYProperty().subtract(INNER_ELLIPSE_OFFSET));
        ellipse.radiusXProperty().bind(ellipseWithConnectors.radiusXProperty().subtract(INNER_ELLIPSE_OFFSET));
        ellipse.strokeWidthProperty().bind(ellipseWithConnectors.strokeWidthProperty());
        ellipse.strokeTypeProperty().bind(ellipseWithConnectors.strokeTypeProperty());
        ellipse.strokeProperty().bind(ellipseWithConnectors.strokeProperty());
        ellipse.setFill(Color.rgb(0,0,0,0));
        getRoot().setMinWidth(getRoot().getMinWidth() + INNER_ELLIPSE_OFFSET * 2);
        getRoot().setMinHeight(getRoot().getMinHeight() + INNER_ELLIPSE_OFFSET * 2);
        pane.getChildren().add(ellipse);

    }
}
