/*
 * Copyright (C) 2005 - 2014 by TESIS DYNAware GmbH
 */
package ch.zhaw.threatmodeling.skin.utils;

import ch.zhaw.threatmodeling.skin.DataFlowSkinConstants;
import ch.zhaw.threatmodeling.skin.joint.DataFlowJointSkin;
import ch.zhaw.threatmodeling.skin.joint.TrustBoundaryJointSkin;
import de.tesis.dynaware.grapheditor.GJointSkin;
import de.tesis.dynaware.grapheditor.SkinLookup;
import de.tesis.dynaware.grapheditor.core.connections.ConnectionEventManager;
import de.tesis.dynaware.grapheditor.model.*;
import javafx.util.Pair;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;

import java.util.List;
import java.util.Map;

public class DataFlowConnectionCommands {
    private DataFlowConnectionCommands() {
    }

    public static String getJointLabel(GConnection connection, SkinLookup skinLookup) {
        String result = "";
        GJointSkin skin = skinLookup.lookupJoint(connection.getJoints().get(0));
        if (skin instanceof DataFlowJointSkin) {
            DataFlowJointSkin jointSkin = (DataFlowJointSkin) skin;
            result = jointSkin.getText();
        }
        if (skin instanceof TrustBoundaryJointSkin) {
            TrustBoundaryJointSkin jointSkin = (TrustBoundaryJointSkin) skin;
            result = jointSkin.getText();
        }
        return result;
    }

    public static String getType(GConnection connection, SkinLookup skinLookup) {
        String result = "";
        GJointSkin skin = skinLookup.lookupJoint(connection.getJoints().get(0));
        if (skin instanceof DataFlowJointSkin) {
            result = DataFlowJointSkin.ELEMENT_TYPE;
        }
        if (skin instanceof TrustBoundaryJointSkin) {
            result = TrustBoundaryJointSkin.ELEMENT_TYPE;
        }
        return result;
    }

    public static Pair<String, String> getTypeAndJointLabel(GConnection connection, SkinLookup skinLookup) {
        return new Pair<>(getType(connection, skinLookup), getJointLabel(connection, skinLookup));
    }

    public static void setJointLabel(GConnection connection, String text, SkinLookup skinLookup) {
        GJointSkin skin = skinLookup.lookupJoint(connection.getJoints().get(0));
        if (skin instanceof DataFlowJointSkin) {
            ((DataFlowJointSkin) skin).setText(text);
        } else if (skin instanceof TrustBoundaryJointSkin) {
            ((TrustBoundaryJointSkin) skin).setText(text);
        }

    }

    public static boolean isConnectionType(String type) {
        return DataFlowSkinConstants.DFD_CONNECTION_TYPES.contains(type);
    }

    /**
     * Adds a connection to the model.
     *
     * @param model  the {@link GModel} to which the connection should be added
     * @param source the source {@link GConnector} of the new connection
     * @param target the target {@link GConnector} of the new connection
     * @param type   the type attribute for the new connection
     * @param joints the list of {@link GJoint} instances to be added inside the
     *               new connection
     */
    public static void addConnection(final GModel model,
                                     final GConnector source,
                                     final GConnector target,
                                     final String type,
                                     final List<GJoint> joints,
                                     final ConnectionEventManager connectionEventManager,
                                     final Map<Command, Pair<String, String>> createCommandToTypeTextMapping) {
        final EditingDomain editingDomain = AdapterFactoryEditingDomain.getEditingDomainFor(model);

        if (editingDomain != null) {

            // prepare new connection:
            final GConnection connection = GraphFactory.eINSTANCE.createGConnection();
            connection.setType(type);
            connection.setSource(source);
            connection.setTarget(target);
            connection.getJoints().addAll(joints);

            // attributes that involve other members of the model, are modified through commands:
            Command creationCommand = AddCommand.create(editingDomain, model, GraphPackage.Literals.GMODEL__CONNECTIONS, connection);
            editingDomain.getCommandStack().execute(creationCommand);
            if (createCommandToTypeTextMapping != null) {
                createCommandToTypeTextMapping.put(creationCommand, new Pair<>(type, type));
            }
            editingDomain.getCommandStack().execute(AddCommand.create(editingDomain, source, GraphPackage.Literals.GCONNECTOR__CONNECTIONS, connection));
            editingDomain.getCommandStack().execute(AddCommand.create(editingDomain, target, GraphPackage.Literals.GCONNECTOR__CONNECTIONS, connection));

            final Command onCreate;
            if (connectionEventManager != null && (onCreate = connectionEventManager.notifyConnectionAdded(connection)) != null) {
                editingDomain.getCommandStack().execute(onCreate);
            }
        }
    }

    /**
     * Removes a connection from the model.
     *
     * @param model                  the {@link GModel} from which the connection should be removed
     * @param connection             the {@link GConnection} to be removed
     * @param connectionEventManager
     */
    public static void removeConnection(final GModel model, final GConnection connection, ConnectionEventManager connectionEventManager) {
        final EditingDomain editingDomain = AdapterFactoryEditingDomain.getEditingDomainFor(model);

        if (editingDomain != null) {
            final CompoundCommand command = new CompoundCommand();

            final GConnector source = connection.getSource();
            final GConnector target = connection.getTarget();

            command.append(RemoveCommand.create(editingDomain, model, GraphPackage.Literals.GMODEL__CONNECTIONS, connection));
            command.append(RemoveCommand.create(editingDomain, source, GraphPackage.Literals.GCONNECTOR__CONNECTIONS, connection));
            command.append(RemoveCommand.create(editingDomain, target, GraphPackage.Literals.GCONNECTOR__CONNECTIONS, connection));

            final Command onRemove;
            if (connectionEventManager != null && (onRemove = connectionEventManager.notifyConnectionRemoved(connection)) != null) {
                command.append(onRemove);
            }

            if (command.canExecute()) {
                editingDomain.getCommandStack().execute(command);
            }

        }
    }
}
