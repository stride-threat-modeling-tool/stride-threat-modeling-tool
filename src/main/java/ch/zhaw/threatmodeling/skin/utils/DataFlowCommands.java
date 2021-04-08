package ch.zhaw.threatmodeling.skin.utils;

import de.tesis.dynaware.grapheditor.SkinLookup;
import de.tesis.dynaware.grapheditor.model.GConnection;
import de.tesis.dynaware.grapheditor.model.GConnector;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DataFlowCommands {

    private DataFlowCommands(){}

    public static int remove(
            Map<Command, Pair<String, String>> commandToTypeTextMapping,
            SkinLookup skinLookup,
            Collection<EObject> pToRemove,
            EditingDomain editingDomain,
            GModel model) {
        int deleteCommandCount = 0;
        if (pToRemove != null && !pToRemove.isEmpty()) {
            RemoveContext editContext = new RemoveContext();
            List<EObject> delete = new ArrayList<>(pToRemove.size());
            Iterator<EObject> var5 = pToRemove.iterator();
            while(true) {

                EObject obj;
                while(var5.hasNext()) {
                    obj = var5.next();
                    if (obj instanceof GNode && editContext.canRemove(obj)) {
                        delete.add(obj);
                        Iterator<GConnector> var7 = ((GNode)obj).getConnectors().iterator();

                        while(var7.hasNext()) {
                            GConnector connector = var7.next();
                            Iterator<GConnection> var9 = connector.getConnections().iterator();

                            while(var9.hasNext()) {
                                GConnection connection = var9.next();
                                if (connection != null && editContext.canRemove(connection)) {
                                    delete.add(connection);
                                }
                            }
                        }
                    } else if (obj instanceof GConnection && editContext.canRemove(obj)) {
                        delete.add(obj);
                    }
                }

                var5 = delete.iterator();

                while(var5.hasNext()) {
                    obj = var5.next();
                    if (obj instanceof GNode) {
                        Command command = RemoveCommand.create(editingDomain, model, GraphPackage.Literals.GMODEL__NODES, obj);
                        commandToTypeTextMapping.put(command,DataFlowNodeCommands.getTypeAndTextOfNode((GNode) obj, skinLookup));
                        editingDomain.getCommandStack().execute(command);
                        deleteCommandCount++;

                    } else if (obj instanceof GConnection) {
                        remove(commandToTypeTextMapping, skinLookup, (GConnection)obj, editingDomain, model);
                        deleteCommandCount += 3;
                    }
                }
                return deleteCommandCount;
            }
        }
        return -1;
    }

    private static void remove( Map<Command, Pair<String, String>> commandToTypeTextMapping,
                                SkinLookup skinLookup,
                                final GConnection pToDelete,
                                EditingDomain editingDomain,
                                GModel model)
    {
        final GConnector source = pToDelete.getSource();
        final GConnector target = pToDelete.getTarget();

        Command removeConnectionCommand = RemoveCommand.create(editingDomain, model, GraphPackage.Literals.GMODEL__CONNECTIONS, pToDelete);
        commandToTypeTextMapping.put(removeConnectionCommand, DataFlowConnectionCommands.getTypeAndJointLabel(pToDelete, skinLookup));

        editingDomain.getCommandStack().execute(removeConnectionCommand);
        editingDomain.getCommandStack().execute(RemoveCommand.create(editingDomain, source, GraphPackage.Literals.GCONNECTOR__CONNECTIONS, pToDelete));
        editingDomain.getCommandStack().execute(RemoveCommand.create(editingDomain, target, GraphPackage.Literals.GCONNECTOR__CONNECTIONS, pToDelete));

    }
}
