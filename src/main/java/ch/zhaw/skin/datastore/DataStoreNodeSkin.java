package ch.zhaw.skin.datastore;

import ch.zhaw.skin.genericrectangle.GenericRectangleNodeSkin;
import de.tesis.dynaware.grapheditor.model.GNode;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

/**
 * A grey node with a navy title-bar for the 'titled-skins' theme.
 */
public class DataStoreNodeSkin extends GenericRectangleNodeSkin {

    private static final String STYLE_CLASS_BACKGROUND = "data-store-node-background";
    private static final String STYLE_CLASS_SELECTION_HALO = "data-store-node-selection-halo";

    private static final double MIN_WIDTH = 81;
    private static final double MIN_HEIGHT = 81;
    private static final String TITLE_TEXT = "Data\nStore";


    /**
     * Creates a new {@link DataStoreNodeSkin} instance.
     *
     * @param node the {link GNode} this skin is representing
     */
    public DataStoreNodeSkin(final GNode node) {

        super(node);

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


        contentRoot.getChildren().add(new Label(TITLE_TEXT));
        getRoot().getChildren().add(contentRoot);

        contentRoot.setAlignment(Pos.CENTER);

        /*
        Does not seem to do anything?
        contentRoot.minWidthProperty().bind(getRoot().widthProperty());
        contentRoot.prefWidthProperty().bind(getRoot().widthProperty());
        contentRoot.maxWidthProperty().bind(getRoot().widthProperty());
        contentRoot.minHeightProperty().bind(getRoot().heightProperty());
        contentRoot.prefHeightProperty().bind(getRoot().heightProperty());
        contentRoot.maxHeightProperty().bind(getRoot().heightProperty());*/

        contentRoot.getStyleClass().setAll(STYLE_CLASS_BACKGROUND);
    }

    /**
     * Adds the selection halo and initializes some of its values.
     */
    private void addSelectionHalo() {

        getRoot().getChildren().add(selectionHalo);

        selectionHalo.setManaged(false);
        selectionHalo.setMouseTransparent(false);
        selectionHalo.setVisible(false);

        selectionHalo.setLayoutX(-HALO_OFFSET);
        selectionHalo.setLayoutY(-HALO_OFFSET);

        selectionHalo.getStyleClass().add(STYLE_CLASS_SELECTION_HALO);
    }


}
