package ch.zhaw.skin.externalentity;

import ch.zhaw.skin.datastore.DataStoreNodeSkin;
import de.tesis.dynaware.grapheditor.model.GNode;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ExternalEntityNodeSkin extends DataStoreNodeSkin {
    private static final String TITLE_TEXT_UPPER = "External";
    private static final String TITLE_TEXT_LOWER = "Entity";
    private static final String STYLE_CLASS_BACKGROUND = "external-entity-node-background";

    /**
     * Creates a new {@link DataStoreNodeSkin} instance.
     *
     * @param node the {link GNode} this skin is representing
     */
    public ExternalEntityNodeSkin(GNode node) {
        super(node);
    }

    @Override
    protected void createContent() {
        VBox contentRoot = super.getContentRoot();
        contentRoot.getChildren().add(new Label(TITLE_TEXT_UPPER));
        contentRoot.getChildren().add(new Label(TITLE_TEXT_LOWER));
        getRoot().getChildren().add(contentRoot);

        contentRoot.setAlignment(Pos.CENTER);
        contentRoot.getStyleClass().setAll(STYLE_CLASS_BACKGROUND);
    }
}
