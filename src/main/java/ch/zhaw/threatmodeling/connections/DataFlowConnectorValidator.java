package ch.zhaw.threatmodeling.connections;

import de.tesis.dynaware.grapheditor.GConnectorValidator;
import de.tesis.dynaware.grapheditor.model.GConnector;

/**
 * Default validation rules that determine which connectors can be connected to each other.
 */
public class DataFlowConnectorValidator implements GConnectorValidator {

    @Override
    public boolean prevalidate(final GConnector source, final GConnector target) {

        if (source == null || target == null) {
            return false;
        } else if (source.equals(target)) {
            return false;
        }

        return true;
    }

    @Override
    public boolean validate(final GConnector source, final GConnector target) {
        /*
         * Compared to the DefaultConnectorValidator we don't check whether source and target are input or output since
         * since the connectors are bidirectional.
         */
        if (source.getType() == null || target.getType() == null) {
            return false;
        } else if (!source.getConnections().isEmpty() || !target.getConnections().isEmpty()) {
            return false;
        }

        return true;
    }

    @Override
    public String createConnectionType(final GConnector source, final GConnector target) {
        return null;
    }

    @Override
    public String createJointType(final GConnector source, final GConnector target) {
        return null;
    }
}
