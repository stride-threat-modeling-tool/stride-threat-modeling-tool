package ch.zhaw.threatmodeling.skin.utils;

import ch.zhaw.threatmodeling.skin.nodes.generic.GenericNodeSkin;
import ch.zhaw.threatmodeling.skin.nodes.trustboundary.TrustBoundaryNodeSkin;
import de.tesis.dynaware.grapheditor.SkinLookup;
import de.tesis.dynaware.grapheditor.core.connections.ConnectionEventManager;
import de.tesis.dynaware.grapheditor.model.*;
import javafx.util.Pair;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;

import java.util.List;

public class DataFlowNodeCommands {
    private static final EReference NODES = GraphPackage.Literals.GMODEL__NODES;

    private DataFlowNodeCommands() {
    }

    public static String getTypeOfNode(GNode node, SkinLookup skinLookup) {
        return ((GenericNodeSkin) skinLookup.lookupNode(node)).getType();
    }

    public static String getTextOfNode(GNode node, SkinLookup skinLookup) {
        return ((GenericNodeSkin) skinLookup.lookupNode(node)).getText();
    }

    public static Pair<String, String> getTypeAndTextOfNode(GNode node, SkinLookup skinLookup) {
        return new Pair<>(getTypeOfNode(node, skinLookup), getTextOfNode(node, skinLookup));
    }

    public static void setTextOfNode(GNode node, SkinLookup skinLookup, String text){
        ((GenericNodeSkin) skinLookup.lookupNode(node)).setText(text);
    }

    public static boolean isTrustBoundaryNode(GNode node, SkinLookup skinLookup) {
        return  getTypeOfNode(node, skinLookup).equals(TrustBoundaryNodeSkin.TITLE_TEXT);
    }


    public static CompoundCommand addTrustBoundary(
            final GNode node1,
            final GNode node2,
            final GModel model,
            final GConnector source,
            final GConnector target,
            final String type,
            final List<GJoint> joints,
            final ConnectionEventManager connectionEventManager) {
        EditingDomain editingDomain = AdapterFactoryEditingDomain.getEditingDomainFor(model);
        CompoundCommand addTrustBoundaryCommand = new CompoundCommand();
        if (editingDomain != null) {

            addTrustBoundaryCommand.append(AddCommand.create(editingDomain, model, NODES, node1));
            addTrustBoundaryCommand.append(AddCommand.create(editingDomain, model, NODES, node2));

            final GConnection connection = GraphFactory.eINSTANCE.createGConnection();
            connection.setType(type);
            connection.setSource(source);
            connection.setTarget(target);
            connection.getJoints().addAll(joints);

            // attributes that involve other members of the model, are modified through commands:
            addTrustBoundaryCommand.append(AddCommand.create(editingDomain, model, GraphPackage.Literals.GMODEL__CONNECTIONS, connection));
            addTrustBoundaryCommand.append(AddCommand.create(editingDomain, source, GraphPackage.Literals.GCONNECTOR__CONNECTIONS, connection));
            addTrustBoundaryCommand.append(AddCommand.create(editingDomain, target, GraphPackage.Literals.GCONNECTOR__CONNECTIONS, connection));

            final Command onCreate;
            if (connectionEventManager != null && (onCreate = connectionEventManager.notifyConnectionAdded(connection)) != null) {
                addTrustBoundaryCommand.append(onCreate);
            }

            if (addTrustBoundaryCommand.canExecute()) {
                editingDomain.getCommandStack().execute(addTrustBoundaryCommand);
            }
        }

        return addTrustBoundaryCommand;
    }
}
