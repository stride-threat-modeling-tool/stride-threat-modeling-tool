package ch.zhaw.threatmodeling.skin.connector;

import ch.zhaw.threatmodeling.skin.DataFlowSkinConstants;
import de.tesis.dynaware.grapheditor.GConnectorSkin;
import de.tesis.dynaware.grapheditor.GConnectorStyle;
import de.tesis.dynaware.grapheditor.model.GConnector;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

import java.util.logging.Logger;

/**
 * A square-shaped connector skin for Dataflow nodes.
 */
public class DataFlowConnectorSkin extends GConnectorSkin {

    private static final Logger LOGGER = Logger.getLogger("Data Flow Connector skin");
    private static final String STYLE_CLASS = "data-flow-connector";
    private static final String STYLE_CLASS_FORBIDDEN_GRAPHIC = "data-flow-forbidden-graphic";

    public static final double SIZE = 15;

    private final Pane root = new Pane();

    private final Group forbiddenGraphic;

    /**
     * Creates a new {@link DataFlowConnectorSkin} instance.
     *
     * @param connector the {@link GConnector} that this skin is representing
     */
    public DataFlowConnectorSkin(final GConnector connector) {

        super(connector);

        root.setMinSize(SIZE, SIZE);
        root.setPrefSize(SIZE, SIZE);
        root.setMaxSize(SIZE, SIZE);
        root.getStyleClass().setAll(STYLE_CLASS);
        root.setPickOnBounds(false);

        forbiddenGraphic = createForbiddenGraphic();
        root.getChildren().addAll(forbiddenGraphic);
    }

    @Override
    public Node getRoot() {
        return root;
    }

    @Override
    public double getWidth() {
        return SIZE;
    }

    @Override
    public double getHeight() {
        return SIZE;
    }

    @Override
    public void applyStyle(final GConnectorStyle style) {

        switch (style) {

            case DEFAULT:
                root.pseudoClassStateChanged(DataFlowSkinConstants.PSEUDO_CLASS_FORBIDDEN, false);
                root.pseudoClassStateChanged(DataFlowSkinConstants.PSEUDO_CLASS_ALLOWED, false);
                forbiddenGraphic.setVisible(false);
                break;

            case DRAG_OVER_ALLOWED:
                root.pseudoClassStateChanged(DataFlowSkinConstants.PSEUDO_CLASS_FORBIDDEN, false);
                root.pseudoClassStateChanged(DataFlowSkinConstants.PSEUDO_CLASS_ALLOWED, true);
                forbiddenGraphic.setVisible(false);
                break;

            case DRAG_OVER_FORBIDDEN:
                root.pseudoClassStateChanged(DataFlowSkinConstants.PSEUDO_CLASS_FORBIDDEN, true);
                root.pseudoClassStateChanged(DataFlowSkinConstants.PSEUDO_CLASS_ALLOWED, false);
                forbiddenGraphic.setVisible(true);
                break;
        }
    }

    @Override
    protected void selectionChanged(boolean isSelected) {
        if (isSelected) {
            root.pseudoClassStateChanged(DataFlowSkinConstants.PSEUDO_CLASS_SELECTED, true);
        } else {
            root.pseudoClassStateChanged(DataFlowSkinConstants.PSEUDO_CLASS_SELECTED, false);
        }
    }

    /**
     * Creates a graphic to display a 'forbidden' effect in the connector.
     *
     * @return the new graphic
     */
    private Group createForbiddenGraphic() {

        final Group group = new Group();
        final Line firstLine = new Line(1, 1, SIZE - 1, SIZE - 1);
        final Line secondLine = new Line(1, SIZE - 1, SIZE - 1, 1);

        firstLine.getStyleClass().add(STYLE_CLASS_FORBIDDEN_GRAPHIC);
        secondLine.getStyleClass().add(STYLE_CLASS_FORBIDDEN_GRAPHIC);

        group.getChildren().addAll(firstLine, secondLine);
        group.setVisible(false);

        return group;
    }
}