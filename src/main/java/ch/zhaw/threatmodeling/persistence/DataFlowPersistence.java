package ch.zhaw.threatmodeling.persistence;

import ch.zhaw.threatmodeling.persistence.utils.JSONPreparatory;
import ch.zhaw.threatmodeling.persistence.utils.objects.STRIDEModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import de.tesis.dynaware.grapheditor.GraphEditor;
import de.tesis.dynaware.grapheditor.model.GModel;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

public class DataFlowPersistence {
    public static final String EXTENSION = ".STRIDE.json";
    private static final String CHOOSER_TEXT = "Data flow diagrams (*" + EXTENSION + ")";

    private static final Logger LOGGER = Logger.getLogger("Data Flow Persistence");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();


    public void saveToFile(GraphEditor graphEditor) {
        handlePersistence(graphEditor, true);

    }

    public STRIDEModel loadFromFile(GraphEditor graphEditor) {
        return handlePersistence(graphEditor, false);
    }

    private STRIDEModel handlePersistence(GraphEditor graphEditor, boolean save) {
        final Scene scene = graphEditor.getView().getScene();
        STRIDEModel result = null;
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

    private STRIDEModel loadModelFromFile(File file) {
        final String absolutePath = file.getAbsolutePath();
        STRIDEModel loadedModel = null;
        if (absolutePath.endsWith(EXTENSION)) {
            try {
                loadedModel = GSON.fromJson(new BufferedReader(new FileReader(absolutePath)), STRIDEModel.class);
            } catch (JsonIOException | IOException e) {
                LOGGER.warning("Could not load diagram " + e.getMessage());
            }
        }
        return loadedModel;
    }

    File showFileChooser(Window window, boolean save) {
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
        STRIDEModel model = new STRIDEModel(
                JSONPreparatory.createSavableNodes(graphEditor.getModel(), graphEditor.getSkinLookup()),
                JSONPreparatory.createSavableConnections(graphEditor.getModel(), graphEditor.getSkinLookup())
        );

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(absolutePath, false))) {
            GSON.toJson(model, writer);
        } catch (JsonIOException | IOException e) {
            LOGGER.warning("Could not save model" + e.getMessage());
        }

    }
}
