package ch.zhaw.threatmodeling.skin.connection;

import ch.zhaw.threatmodeling.connections.CurvedConnections;
import ch.zhaw.threatmodeling.skin.DataFlowSkinConstants;
import de.tesis.dynaware.grapheditor.GJointSkin;
import de.tesis.dynaware.grapheditor.GraphEditor;
import de.tesis.dynaware.grapheditor.core.skins.defaults.connection.SimpleConnectionSkin;
import de.tesis.dynaware.grapheditor.model.GConnection;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The DataFlow connection skin.
 *
 * <p>
 * Extension of {@link SimpleConnectionSkin} that provides a mechanism for creating and removing joints.
 * </p>
 */
public class DataFlowConnectionSkin extends CurvedConnectionSkin {

    private static final Logger LOGGER = Logger.getLogger("Data Flow Connection Skin");
    private static final String type = DataFlowSkinConstants.DFD_CONNECTION;

    /**
     * Creates a new data flow connection skin instance.
     *
     * @param connection the {@link GConnection} the skin is being created for
     */
    public DataFlowConnectionSkin(final GConnection connection) {

        super(connection);

        performChecks();
    }

    public static String getType() {
        return type;
    }

    @Override
    public void setGraphEditor(final GraphEditor graphEditor) {
        super.setGraphEditor(graphEditor);
    }

    @Override
    public void setJointSkins(final List<GJointSkin> jointSkins) {
        super.setJointSkins(jointSkins);
    }

    /**
     * Checks that the connection has the correct values to be displayed using
     * this skin.
     */
    private void performChecks()
    {
        if (!CurvedConnections.checkJointCount(getItem()))
        {
            LOGGER.log(Level.INFO, "Joint count not compatible with source and target connector types.");
        }
    }
}
