package ch.zhaw.threatmodeling.persistence;

import ch.zhaw.threatmodeling.persistence.utils.JSONPreparatory;
import ch.zhaw.threatmodeling.persistence.utils.objects.DataFlowConnectionObject;
import ch.zhaw.threatmodeling.persistence.utils.objects.DataFlowNodeObject;
import ch.zhaw.threatmodeling.persistence.utils.objects.DataFlowObject;
import com.google.gson.Gson;
import de.tesis.dynaware.grapheditor.GraphEditor;
import de.tesis.dynaware.grapheditor.model.GModel;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Pair;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class DataFlowPersistence {
    private static final String EXTENSION = ".dfd-stride";
    private static final String CHOOSER_TEXT = "Data flow diagrams (*" + EXTENSION + ")";

    private static final Logger LOGGER = Logger.getLogger("Data Flow Persistence");
    private static final Gson GSON = new Gson();
    public static final String CONNECTIONS_START = "CONNECTIONS:";
    public static final String NODES_START = "NODES:";


    public void saveToFile(GraphEditor graphEditor) {
        handlePersistence(graphEditor, true);

    }

    public Pair<List<DataFlowNodeObject>,List<DataFlowConnectionObject>> loadFromFile(GraphEditor graphEditor) {
        return  handlePersistence(graphEditor, false);
    }

    private Pair<List<DataFlowNodeObject>,List<DataFlowConnectionObject>> handlePersistence(GraphEditor graphEditor, boolean save) {
        final Scene scene = graphEditor.getView().getScene();
        Pair<List<DataFlowNodeObject>,List<DataFlowConnectionObject>> result = null;
        if (scene != null) {
            final File file = showFileChooser(scene.getWindow(), save);
            final GModel model = graphEditor.getModel();
            if (file != null && model != null) {
                if (save) {
                    saveModelToFile(file, graphEditor);
                } else {
                    result = loadModelFromFile(file);
                }

            }
        }
        return result;
    }

    private Pair<List<DataFlowNodeObject>,List<DataFlowConnectionObject>> loadModelFromFile(File file) {
        final String absolutePath = file.getAbsolutePath();
        final List<DataFlowNodeObject> loadedNodes = new ArrayList<>();
        final List<DataFlowConnectionObject> loadedConnections = new ArrayList<>();
        boolean loadingNodes = false;
        boolean loadingConnections = false;
        if (absolutePath.endsWith(EXTENSION)) {
            try {
                for (String line : Files.readAllLines(Paths.get(absolutePath))) {
                    if(loadingConnections){
                        loadedConnections.add(GSON.fromJson(line, DataFlowConnectionObject.class));
                    } else {
                        loadingConnections = line.equals(CONNECTIONS_START);
                        if(loadingNodes && ! loadingConnections) {
                            loadedNodes.add(GSON.fromJson(line, DataFlowNodeObject.class));
                        }
                        loadingNodes = line.equals(NODES_START) || loadingNodes;
                        LOGGER.info(line);

                    }
                }
            } catch (IOException e) {
                LOGGER.warning("Could not load diagram " + e.getMessage());
            }
        }
        return new Pair<>(loadedNodes, loadedConnections);
    }

    private File showFileChooser(Window window, boolean save) {
        File chosenFile;
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(CHOOSER_TEXT, "*" + EXTENSION));
        if (save) {
            chosenFile = fileChooser.showSaveDialog(window);
        } else {
            chosenFile = fileChooser.showOpenDialog(window);
        }

        return chosenFile;
    }

    private void saveModelToFile(File file, GraphEditor graphEditor) {
        String absolutePath = file.getAbsolutePath();
        if (!absolutePath.endsWith(EXTENSION)) {
            absolutePath += EXTENSION;
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(absolutePath, false))) {
            writer.append(NODES_START + "\n");
            for (DataFlowObject object : JSONPreparatory.createSavableNodes(graphEditor.getModel(), graphEditor.getSkinLookup())) {
                writer.append(GSON.toJson(object));
                writer.append("\n");
            }
            writer.append(CONNECTIONS_START + "\n");
            for (DataFlowObject object : JSONPreparatory.createSavableConnections(graphEditor.getModel(), graphEditor.getSkinLookup())) {
                writer.append(GSON.toJson(object));
                writer.append("\n");
            }
        } catch (IOException e) {
            LOGGER.warning("Could not save model" + e.getMessage());
        }

    }
}
