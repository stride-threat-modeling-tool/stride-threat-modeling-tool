package ch.zhaw.skin;

import de.tesis.dynaware.grapheditor.GConnectorSkin;
import de.tesis.dynaware.grapheditor.GNodeSkin;
import de.tesis.dynaware.grapheditor.GTailSkin;
import de.tesis.dynaware.grapheditor.model.GConnector;
import de.tesis.dynaware.grapheditor.model.GNode;
import javafx.geometry.Side;

/**
 * Responsible for skin-specific logic in the graph editor demo.
 */
public interface SkinController {

    /**
     * Adds a node to the graph.
     *
     * @param currentZoomFactor the current zoom factor (1 for 100%)
     */
    void addNode(final double currentZoomFactor);

    /**
     * activates this skin
     */
    void activate();

    /**
     * Adds a connector of the given type to all selected nodes.
     *
     * @param position the currently selected connector position
     * @param input {@code true} for input, {@code false} for output
     */
    void addConnector(Side position, boolean input);

    /**
     * Clears all connectors from all selected nodes.
     */
    void clearConnectors();

    /**
     * Handles the paste operation.
     * @param selectionCopier {@link SelectionCopier}

    void handlePaste(SelectionCopier selectionCopier);*/

    /**
     * Handles the select-all operation.
     */
    void handleSelectAll();

    GNodeSkin createNodeSkin(final GNode node);

    GConnectorSkin createConnectorSkin(final GConnector connector);

    GTailSkin createTailSkin(final GConnector connector);

}
