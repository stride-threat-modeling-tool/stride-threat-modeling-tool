package ch.zhaw.threatmodeling.persistence;

import ch.zhaw.threatmodeling.persistence.utils.FileChooserUtil;
import javafx.scene.layout.Region;

import java.util.logging.Logger;

public class HTMLReportPersistence {
    private static final Logger LOGGER = Logger.getLogger("report persistence");
    private static final String EXTENSION = ".html";
    private static final String CHOOSER_TEXT = "HTML report";

    private final Region view;
    private final FileChooserUtil fileChooserUtil;

    public HTMLReportPersistence(Region view, FileChooserUtil chooserUtil) {
        this.view = view;
        this.fileChooserUtil = chooserUtil;
        if(view == null){
            LOGGER.info("scene is null");
        }
    }

    public String getFilePath() {
        return fileChooserUtil.getPath(view.getScene().getWindow(), true, CHOOSER_TEXT, EXTENSION);
    }
}
