package ch.zhaw.threatmodeling.persistence;

import ch.zhaw.threatmodeling.model.threats.Threat;
import ch.zhaw.threatmodeling.persistence.utils.JSONPreparatory;
import ch.zhaw.threatmodeling.persistence.utils.objects.STRIDEModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.tesis.dynaware.grapheditor.GraphEditor;
import de.tesis.dynaware.grapheditor.model.GModel;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.*;
import java.util.List;
import java.util.logging.Logger;

public class DataFlowPersistence {
    public static final String EXTENSION = ".STRIDE.json";
    private static final String CHOOSER_TEXT = "Data flow diagrams (*" + EXTENSION + ")";

    private static final Logger LOGGER = Logger.getLogger("Data Flow Persistence");
    private final ObjectMapper mapper = new ObjectMapper();


    public void saveToFile(GraphEditor graphEditor, List<Threat> threats) {
        handlePersistence(graphEditor, threats, true);

    }

    public STRIDEModel loadFromFile(GraphEditor graphEditor) {
        return handlePersistence(graphEditor, null, false);
    }

    private STRIDEModel handlePersistence(GraphEditor graphEditor, List<Threat> threats, boolean save) {
        final Scene scene = graphEditor.getView().getScene();
        STRIDEModel result = null;
        if (scene != null) {
            final File file = showFileChooser(scene.getWindow(), save);
            final GModel model = graphEditor.getModel();
            if (file != null && model != null) {
                if (save) {
                    saveModelToFile(file, graphEditor, threats);
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
                loadedModel = mapper.readValue(new BufferedReader(new FileReader(absolutePath)), STRIDEModel.class);
            } catch (IOException e) {
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

    private void saveModelToFile(File file, GraphEditor graphEditor, List<Threat> threats) {
        String absolutePath = file.getAbsolutePath();
        if (!absolutePath.endsWith(EXTENSION)) {
            absolutePath += EXTENSION;
        }
        STRIDEModel model = new STRIDEModel(
                JSONPreparatory.createSavableNodes(graphEditor.getModel(), graphEditor.getSkinLookup()),
                JSONPreparatory.createSavableConnections(graphEditor.getModel(), graphEditor.getSkinLookup()),
                JSONPreparatory.createSavableThreats(graphEditor.getModel(), threats));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(absolutePath, false))) {
            mapper.writerWithDefaultPrettyPrinter().writeValue(writer, model);
        } catch (IOException e) {
            LOGGER.warning("Could not save model" + e.getMessage());
        }

    }
}
