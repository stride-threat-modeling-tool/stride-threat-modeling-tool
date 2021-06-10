package ch.zhaw.threatmodeling.skin.controller;

import ch.zhaw.threatmodeling.skin.utils.DataFlowConnectionCommands;
import ch.zhaw.threatmodeling.skin.utils.DataFlowNodeCommands;
import de.tesis.dynaware.grapheditor.GraphEditor;
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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class DoController {
    private final Deque<Pair<Integer, Boolean>> lastCommandDeletedCount = new ArrayDeque<>();
    private final Deque<Pair<Integer, Boolean>> lastCommandUndoCount = new ArrayDeque<>();
    private final Map<Command, Pair<String, String>> deleteCommandToTypeTextMapping = new HashMap<>();
    private final Map<Command, Pair<String, String>> createCommandToTypeTextMapping = new HashMap<>();
    private final GModel model;
    private final EditingDomain editingDomain;
    private final CommandStack commandStack;
    private final DataFlowDiagramSkinController skinController;
    private static final Logger LOGGER = Logger.getLogger("docontroller");

    public DoController(GModel model, DataFlowDiagramSkinController skinController) {
        this.model = model;
        this.editingDomain = AdapterFactoryEditingDomain.getEditingDomainFor(model);
        this.commandStack = editingDomain.getCommandStack();
        this.skinController = skinController;

    }

    public void mapCreateCommand(Command command, String type, String text) {
        createCommandToTypeTextMapping.put(
                command,
                new Pair<>(type, text));
    }

    public void undo() {
        boolean isRemoveCommand;
        boolean isAddCommand;
        int toUndoCount = -1;
        Pair<Integer, Boolean> commandsToUndoAndRedoAllowedPair = null;
        do {
            if (commandStack.canUndo()) {
                Command currentCommand = commandStack.getUndoCommand();
                isRemoveCommand = undoSingleCommand(currentCommand, commandStack);
                isAddCommand = currentCommand instanceof AddCommand;
                if ((isRemoveCommand || isAddCommand) &&
                        toUndoCount == -1 &&
                        null != deleteCommandToTypeTextMapping.get(currentCommand) &&
                        commandsToUndoAndRedoAllowedPair == null) {
                    commandsToUndoAndRedoAllowedPair = lastCommandDeletedCount.pop();
                    toUndoCount = commandsToUndoAndRedoAllowedPair.getKey();
                    lastCommandUndoCount.push(commandsToUndoAndRedoAllowedPair);
                }
            }
            toUndoCount = toUndoCount - 1;
        } while (toUndoCount > 0);


    }

    public void redo() {
        int toRedoCount = -1;
        Pair<Integer, Boolean> commandsToRedoAndRedoAllowedPair = null;
        do {
            if (commandStack.canRedo()) {
                Command currentCommand = commandStack.getRedoCommand();

                if (toRedoCount == -1 && null != deleteCommandToTypeTextMapping.get(currentCommand) && !lastCommandUndoCount.isEmpty() && commandsToRedoAndRedoAllowedPair == null) {
                    commandsToRedoAndRedoAllowedPair = lastCommandUndoCount.pop();
                    toRedoCount = commandsToRedoAndRedoAllowedPair.getKey();
                    lastCommandDeletedCount.push(commandsToRedoAndRedoAllowedPair);
                }
                if(commandsToRedoAndRedoAllowedPair == null || !commandsToRedoAndRedoAllowedPair.getValue()){
                    redoSingleCommand(currentCommand, commandStack);
                } else {
                    LOGGER.info("redone prevented, you tried to redo a paste with selection with data flows and trust boundaries, this cannot be handled");
                }
            }
            toRedoCount = toRedoCount - 1;
        } while (toRedoCount > 0);
        if(commandsToRedoAndRedoAllowedPair != null && commandsToRedoAndRedoAllowedPair.getValue()){
            lastCommandUndoCount.push(lastCommandDeletedCount.pop());
        }
    }

    private void redoSingleCommand(Command command, CommandStack stack) {
        final List<GConnection> oldConnections = new ArrayList<>(skinController.getGraphEditor().getModel().getConnections());
        final List<GNode> oldNodes = new ArrayList<>(skinController.getGraphEditor().getModel().getNodes());
        Pair<String, String> typeText = createCommandToTypeTextMapping.get(command);
        if (null != typeText) {
            String type = typeText.getKey();
            if (command instanceof CompoundCommand) {
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
        if(command instanceof AddCommand && null != typeText){
            skinController.resetNodeAndConnectionNames(typeText.getValue(), oldNodes, oldConnections);
        }
        skinController.setDataFlowSkinFactories();
    }

    private boolean undoSingleCommand(Command command, CommandStack stack) {
        boolean isRemove = command instanceof RemoveCommand;
        final List<GNode> oldNodes = new ArrayList<>(model.getNodes());
        final List<GConnection> oldConnections = new ArrayList<>(model.getConnections());
        Pair<String, String> typeTextPair = deleteCommandToTypeTextMapping.get(command);
        if (null != typeTextPair) {
            String type = typeTextPair.getKey();
            if (DataFlowConnectionCommands.isConnectionType(type)) {
                skinController.activateCorrespondingConnectionFactory(type);
            } else {
                skinController.activateCorrespondingNodeFactory(type);
            }
        }
        handleUndoAddCommand(command);
        stack.undo();
        if (null != typeTextPair) {
            skinController.resetNodeAndConnectionNames(typeTextPair.getValue(), oldNodes, oldConnections);

        }
        skinController.setDataFlowSkinFactories();
        return isRemove;
    }

    private void handleUndoAddCommand(Command command) {
        if(command instanceof AddCommand) {
            GraphEditor editor = skinController.getGraphEditor();
            List<GNode> nodes = editor.getModel().getNodes();
            createCommandToTypeTextMapping.put(command, DataFlowNodeCommands.getTypeAndTextOfNode(nodes.get(nodes.size() - 1), editor.getSkinLookup()));
        }
    }

    public void stackDeletedCount(Pair<Integer, Boolean> count) {
        lastCommandDeletedCount.push(count);
    }

    public Map<Command, Pair<String, String>> getDeleteCommandToTypeTextMapping() {
        return deleteCommandToTypeTextMapping;
    }

    public Map<Command, Pair<String, String>> getCreateCommandToTypeMapping() {
        return createCommandToTypeTextMapping;
    }

    public Command getMostRecentCommand() {
        return commandStack.getMostRecentCommand();
    }


    public void flushCommandStack() {
        commandStack.flush();
        createCommandToTypeTextMapping.clear();
        deleteCommandToTypeTextMapping.clear();
        lastCommandDeletedCount.clear();
        lastCommandUndoCount.clear();
    }
}
