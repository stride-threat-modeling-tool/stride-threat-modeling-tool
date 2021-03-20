package ch.zhaw.skin.externalentity;

import ch.zhaw.skin.datastore.DataStoreNodeSkin;
import de.tesis.dynaware.grapheditor.model.GNode;

public class ExternalEntityNodeSkin extends DataStoreNodeSkin {
    private static final String TITLE_TEXT = "External Entity";
    private static final String STYLE_CLASS_BACKGROUND = "external-entity-node-background";

    /**
     * Creates a new {@link ExternalEntityNodeSkin} instance.
     *
     * @param node the {link GNode} this skin is representing
     */
    public ExternalEntityNodeSkin(GNode node) {
        super(node);
    }

    @Override
    protected void createContent() {
        createGenericContent(STYLE_CLASS_BACKGROUND, TITLE_TEXT);
    }
}
