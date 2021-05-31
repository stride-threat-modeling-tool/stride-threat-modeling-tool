package ch.zhaw.threatmodeling.skin.nodes.externalentity;

import ch.zhaw.threatmodeling.skin.nodes.generic.rectangle.GenericRectangleNodeSkin;
import de.tesis.dynaware.grapheditor.model.GNode;

import java.util.logging.Logger;

public class ExternalEntityNodeSkin extends GenericRectangleNodeSkin {
    private static final Logger LOGGER = Logger.getLogger("External entity skin");
    public static final String TITLE_TEXT = "External Entity";
    private static final String STYLE_CLASS_BACKGROUND = "external-entity-node-background";

    /**
     * Creates a new {@link ExternalEntityNodeSkin} instance.
     *
     * @param node the {link GNode} this skin is representing
     */
    public ExternalEntityNodeSkin(GNode node) {
        super(node);
        typeProperty().set(TITLE_TEXT);
        getRoot().setMinSize(MIN_WIDTH, MIN_HEIGHT);
        addSelectionHalo();
        createContent();
    }

    @Override
    protected void createContent() {
        createGenericRectangleContent(STYLE_CLASS_BACKGROUND, TITLE_TEXT);
    }
}
