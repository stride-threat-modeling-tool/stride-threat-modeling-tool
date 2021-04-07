package ch.zhaw.threatmodeling.skin.utils;

import de.tesis.dynaware.grapheditor.model.*;
import de.tesis.dynaware.grapheditor.utils.RemoveContext;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.domain.EditingDomain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class DataFlowCommands {

    private DataFlowCommands(){}

    public static void remove(Collection<EObject> pToRemove, EditingDomain editingDomain, GModel model) {
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
                        editingDomain.getCommandStack().execute(RemoveCommand.create(editingDomain, model, GraphPackage.Literals.GMODEL__NODES, obj));
                    } else if (obj instanceof GConnection) {
                        remove((GConnection)obj, editingDomain, model);
                    }
                }
                return;
            }
        }
    }

    private static void remove( final GConnection pToDelete, EditingDomain editingDomain, GModel model)
    {
        final GConnector source = pToDelete.getSource();
        final GConnector target = pToDelete.getTarget();

        CompoundCommand command = new CompoundCommand();

        command.append(RemoveCommand.create(editingDomain, model, GraphPackage.Literals.GMODEL__CONNECTIONS, pToDelete));
        command.append(RemoveCommand.create(editingDomain, source, GraphPackage.Literals.GCONNECTOR__CONNECTIONS, pToDelete));
        command.append(RemoveCommand.create(editingDomain, target, GraphPackage.Literals.GCONNECTOR__CONNECTIONS, pToDelete));
        editingDomain.getCommandStack().execute(command);

    }
}
