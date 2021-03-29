package ch.zhaw.threatmodeling.skin.nodes.externalentity;

import ch.zhaw.threatmodeling.skin.nodes.datastore.DataStoreNodeSkin;
import de.tesis.dynaware.grapheditor.model.GNode;

public class ExternalEntityNodeSkin extends DataStoreNodeSkin {
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
    }

    @Override
    protected void createContent() {
        createGenericRectangleContent(STYLE_CLASS_BACKGROUND, TITLE_TEXT);
    }
}
