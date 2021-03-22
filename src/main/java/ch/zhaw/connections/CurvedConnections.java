/*
 * Copyright (C) 2005 - 2014 by TESIS DYNAware GmbH
 */
package ch.zhaw.connections;

import de.tesis.dynaware.grapheditor.model.GConnection;


/**
 * Miscellaneous helper methods for curved connections.
 */
public final class CurvedConnections
{

    private CurvedConnections()
    {
        // Auto-generated constructor stub
    }

    /**
     * Checks that the given connection has a workable number of joints.
     *
     * @param connection
     *            a {@link GConnection} that should be curved
     * @return {@code true} if the joint count is correct
     */
    public static boolean checkJointCount(final GConnection connection)
    {
            return connection.getJoints().size() == 1;
    }
}
