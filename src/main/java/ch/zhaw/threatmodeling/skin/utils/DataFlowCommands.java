package ch.zhaw.threatmodeling.skin.utils;

import de.tesis.dynaware.grapheditor.SkinLookup;
import de.tesis.dynaware.grapheditor.model.GConnection;
import de.tesis.dynaware.grapheditor.model.GModel;
import de.tesis.dynaware.grapheditor.model.GNode;
import de.tesis.dynaware.grapheditor.model.GraphPackage;
import de.tesis.dynaware.grapheditor.utils.RemoveContext;
import javafx.util.Pair;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.domain.EditingDomain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class DataFlowCommands {
    private static final Logger LOGGER = Logger.getLogger("Data Flow commands");

    private DataFlowCommands() {
    }

    public static int orderedRemove(Map<Command, Pair<String, String>> commandToTypeTextMapping,
                                    SkinLookup skinLookup,
                                    Collection<EObject> pToRemove,
                                    EditingDomain editingDomain,
                                    GModel model) {
        List<EObject> toDelete = new ArrayList<>();
        RemoveContext editContext = new RemoveContext();
        model.getConnections().forEach(connection -> {
            if (pToRemove.contains(connection) && editContext.canRemove(connection)) {
                toDelete.add(connection);
            }
        });
        model.getNodes().forEach(node -> {
            if (pToRemove.contains(node) && editContext.canRemove(node)) {
                node.getConnectors().forEach(connector -> {
                    connector.getConnections().forEach(con -> {
                        if (con != null && editContext.canRemove(con)) {
                            toDelete.add(con);
                        }
                    });
                });
                toDelete.add(node);
            }
        });

        return deleteAllElements(toDelete, commandToTypeTextMapping, skinLookup, editingDomain, model);
    }

    private static int deleteAllElements(List<EObject> toDelete, Map<Command, Pair<String, String>> commandToTypeTextMapping,
                                         SkinLookup skinLookup,
                                         EditingDomain editingDomain, GModel model) {
        for (EObject obj : toDelete) {
            deleteElement(obj, commandToTypeTextMapping, skinLookup, editingDomain, model);
        }
        return toDelete.size();
    }

    private static void deleteElement(EObject obj, Map<Command, Pair<String, String>> commandToTypeTextMapping,
                                      SkinLookup skinLookup,
                                      EditingDomain editingDomain, GModel model) {
        if (obj instanceof GNode) {
            Command command = RemoveCommand.create(editingDomain, model, GraphPackage.Literals.GMODEL__NODES, obj);
            commandToTypeTextMapping.put(command, DataFlowNodeCommands.getTypeAndTextOfNode((GNode) obj, skinLookup));
            editingDomain.getCommandStack().execute(command);

        } else if (obj instanceof GConnection) {
            DataFlowConnectionCommands.removeConnection(model, (GConnection) obj, null, commandToTypeTextMapping, skinLookup);
        }
    }

}
