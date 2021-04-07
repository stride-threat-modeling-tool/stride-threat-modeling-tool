package ch.zhaw.threatmodeling.skin.connector;

import ch.zhaw.threatmodeling.skin.DataFlowSkinConstants;
import de.tesis.dynaware.grapheditor.GConnectorSkin;
import de.tesis.dynaware.grapheditor.GConnectorStyle;
import de.tesis.dynaware.grapheditor.model.GConnector;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

import java.util.logging.Logger;

/**
 * A square-shaped connector skin for Trust Boundary nodes.
 */
public class TrustBoundaryConnectorSkin extends GConnectorSkin {

    private static final Logger LOGGER = Logger.getLogger("Trust Boundary Connector skin");
    private static final String STYLE_CLASS = "connector";

    public static final double SIZE = 0; // Make connector invisible

    private final Pane root = new Pane();


    /**
     * Creates a new {@link TrustBoundaryConnectorSkin} instance.
     *
     * @param connector the {@link GConnector} that this skin is representing
     */
    public TrustBoundaryConnectorSkin(final GConnector connector) {

        super(connector);

        root.setMinSize(SIZE, SIZE);
        root.setPrefSize(SIZE, SIZE);
        root.setMaxSize(SIZE, SIZE);
        root.getStyleClass().setAll(STYLE_CLASS);
        root.setPickOnBounds(false);

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
    public void applyStyle(final GConnectorStyle style) { }

    @Override
    protected void selectionChanged(boolean isSelected) {
        if (isSelected) {
            root.pseudoClassStateChanged(DataFlowSkinConstants.PSEUDO_CLASS_SELECTED, true);
        } else {
            root.pseudoClassStateChanged(DataFlowSkinConstants.PSEUDO_CLASS_SELECTED, false);
        }
    }
}
