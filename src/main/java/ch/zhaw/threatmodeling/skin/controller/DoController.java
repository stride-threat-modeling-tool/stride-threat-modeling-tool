package ch.zhaw.threatmodeling.skin.controller;

import ch.zhaw.threatmodeling.skin.utils.DataFlowConnectionCommands;
import de.tesis.dynaware.grapheditor.model.GConnection;
import de.tesis.dynaware.grapheditor.model.GModel;
import de.tesis.dynaware.grapheditor.model.GNode;
import javafx.util.Pair;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;

import java.util.*;

public class DoController {
    private final Deque<Integer> lastCommandDeletedCount = new ArrayDeque<>();
    private final Deque<Integer> lastCommandUndoCount = new ArrayDeque<>();
    private final Map<Command, Pair<String, String>> deleteCommandToTypeTextMapping = new HashMap<>();
    private final Map<Command, String> createCommandToTypeMapping = new HashMap<>();
    private final GModel model;
    private final EditingDomain editingDomain;
    private final CommandStack commandStack;
    private final DataFlowDiagramSkinController skinController;

    public DoController(GModel model, DataFlowDiagramSkinController skinController)
    {
        this.model = model;
        this.editingDomain = AdapterFactoryEditingDomain.getEditingDomainFor(model);
        this.commandStack = editingDomain.getCommandStack();
        this.skinController = skinController;

    }
    public void mapCreateCommand(Command command, String type) {
        createCommandToTypeMapping.put(
                command,
                type);
    }

    public void undo() {
        boolean isRemoveCommand = false;
        boolean isAddCommand = false;
        int toUndoCount = -1;
        do {
            if (commandStack.canUndo()) {
                Command currentCommand = commandStack.getUndoCommand();
                isRemoveCommand =  undoSingleCommand(currentCommand, commandStack);
                isAddCommand = currentCommand instanceof AddCommand;

                if ((isRemoveCommand || isAddCommand)&& toUndoCount == -1 && null != deleteCommandToTypeTextMapping.get(currentCommand)) {
                    toUndoCount = lastCommandDeletedCount.pop();
                    lastCommandUndoCount.push(toUndoCount);
                }
            }
            toUndoCount = toUndoCount - 1;
        } while (toUndoCount > 0 && (isRemoveCommand || isAddCommand));


    }

    public void redo(){
        boolean isRemoveCommand = false;
        int toRedoCount = -1;
        do {
            if (commandStack.canRedo()) {
                Command currentCommand = commandStack.getRedoCommand();
                isRemoveCommand =  redoSingleCommand(currentCommand, commandStack);

                if (isRemoveCommand && toRedoCount == -1 && null != deleteCommandToTypeTextMapping.get(currentCommand)) {
                    toRedoCount = lastCommandUndoCount.pop();
                    lastCommandDeletedCount.push(toRedoCount);

                }
            }
            toRedoCount = toRedoCount - 1;
        } while (toRedoCount > 0 && isRemoveCommand);
    }

    private boolean redoSingleCommand(Command command, CommandStack stack){
        boolean isRemove = command instanceof RemoveCommand;
        String type = createCommandToTypeMapping.get(command);
        if(null != type){
            if(command instanceof CompoundCommand){
                //redo trust boundary
                skinController.setTrustBoundarySkinFactories();
            } else {
                if (DataFlowConnectionCommands.isConnectionType(type)) {
                    skinController.activateCorrespondingConnectionFactory(type);
                } else {
                    skinController.activateCorrespondingNodeFactory(type);
                }
            }
        }
        stack.redo();
        skinController.setDataFlowSkinFactories();
        return isRemove;
    }

    private boolean undoSingleCommand(Command command, CommandStack stack) {
        boolean isRemove = command instanceof RemoveCommand;
        final List<GNode> oldNodes = new ArrayList<>(model.getNodes());
        final List<GConnection> oldConnections = new ArrayList<>(model.getConnections());
        Pair<String, String> typeTextPair = deleteCommandToTypeTextMapping.get(command);
        if (isRemove && null != typeTextPair) {
            String type = typeTextPair.getKey();
            if (DataFlowConnectionCommands.isConnectionType(type)) {
                skinController.activateCorrespondingConnectionFactory(type);
            } else {
                skinController.activateCorrespondingNodeFactory(type);
            }

        }
        stack.undo();
        if (isRemove && null != typeTextPair) {
            skinController.resetNodeAndConnectionNames(typeTextPair.getValue(), oldNodes, oldConnections);

        }
        return isRemove;
    }

    public void stackDeletedCount(int count){
        lastCommandDeletedCount.push(count);
    }

    public Map<Command, Pair<String, String>> getDeleteCommandToTypeTextMapping() {
        return deleteCommandToTypeTextMapping;
    }

    public Map<Command, String> getCreateCommandToTypeMapping() {
        return createCommandToTypeMapping;
    }

    public Command getMostRecentCommand(){
       return commandStack.getMostRecentCommand();
    }


    public void flushCommandStack(){
        commandStack.flush();
        createCommandToTypeMapping.clear();
        deleteCommandToTypeTextMapping.clear();
        lastCommandDeletedCount.clear();
        lastCommandUndoCount.clear();
    }
}
