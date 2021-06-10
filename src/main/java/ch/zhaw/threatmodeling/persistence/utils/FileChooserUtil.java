package ch.zhaw.threatmodeling.persistence.utils;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

public class FileChooserUtil {

    public String getPath(Window window, boolean save, String chooserText, String extension) {
        File chosenFile;
        String path = "";
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(chooserText, "*" + extension));
        if (save) {
            chosenFile = fileChooser.showSaveDialog(window);
        } else {
            chosenFile = fileChooser.showOpenDialog(window);
        }
        if(null != chosenFile){
            path = chosenFile.getAbsolutePath();
            if(!path.endsWith(extension)) {
                path = path + extension;
            }
        }
        return path;
    }
}
