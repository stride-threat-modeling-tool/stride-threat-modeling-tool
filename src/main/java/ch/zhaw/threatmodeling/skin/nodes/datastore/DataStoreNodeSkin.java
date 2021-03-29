package ch.zhaw.threatmodeling.skin.nodes.datastore;

import ch.zhaw.threatmodeling.skin.nodes.generic.rectangle.GenericRectangleNodeSkin;
import de.tesis.dynaware.grapheditor.model.GNode;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

public class DataStoreNodeSkin extends GenericRectangleNodeSkin {

    private static final String STYLE_CLASS_BACKGROUND = "data-store-node-background";


    private static final double MIN_WIDTH = 81;
    private static final double MIN_HEIGHT = 81;
    public static final String TITLE_TEXT = "Data Store";


    /**
     * Creates a new {@link DataStoreNodeSkin} instance.
     *
     * @param node the {link GNode} this skin is representing
     */
    public DataStoreNodeSkin(final GNode node) {

        super(node);
        typeProperty().set(TITLE_TEXT);
        Rectangle border = new Rectangle();
        border.widthProperty().bind(getRoot().widthProperty());
        border.heightProperty().bind(getRoot().heightProperty());

        getRoot().getChildren().add(border);
        getRoot().setMinSize(MIN_WIDTH, MIN_HEIGHT);

        addSelectionHalo();
        createContent();

        contentRoot.addEventFilter(MouseEvent.MOUSE_DRAGGED, super::filterMouseDragged);
    }



    /**
     * Creates the content of the node skin - header, title, close button, etc.
     */
    @Override
    protected void createContent() {
        createGenericContent(STYLE_CLASS_BACKGROUND, TITLE_TEXT);
    }
}
