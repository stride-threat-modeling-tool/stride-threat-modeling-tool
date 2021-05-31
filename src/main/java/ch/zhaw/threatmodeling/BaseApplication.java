package ch.zhaw.threatmodeling;

import ch.zhaw.threatmodeling.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.logging.Logger;

public class BaseApplication extends Application {

    private static final Logger LOGGER = Logger.getLogger(BaseApplication.class.getName());
    private static final String GENERAL_STYLESHEET = "styles.css";
    private static final String DATA_STORE_STYLESHEET = "DataStore.css";
    private static final String EXTERNAL_ENTITY_STYLESHEET = "ExternalEntity.css";
    private static final String TRUST_BOUNDARY_STYLESHEET = "TrustBoundary.css";
    public static final int MIN_WIDTH = 800;
    public static final int MIN_HEIGHT = 600;
    public static final int PREF_WIDTH = 1200;
    public static final int PREF_HEIGHT = 1000;
    private MainController controller;

    @Override
    public void start(final Stage stage) throws Exception {
        final URL location = getClass().getResource("main.fxml");
        final FXMLLoader loader = new FXMLLoader();
        final Parent root = loader.load(location.openStream());
        Scene scene = new Scene(root, PREF_WIDTH, PREF_HEIGHT);
        addStyleSheetToScene(scene, DATA_STORE_STYLESHEET);
        addStyleSheetToScene(scene, EXTERNAL_ENTITY_STYLESHEET);
        addStyleSheetToScene(scene, TRUST_BOUNDARY_STYLESHEET);
        addStyleSheetToScene(scene, GENERAL_STYLESHEET);

        stage.setScene(scene);
        stage.setMinHeight(MIN_HEIGHT);
        stage.setMinWidth(MIN_WIDTH);
        stage.show();
        controller = loader.getController();
    }

    private void addStyleSheetToScene(Scene scene, String filename) {
        scene.getStylesheets().add(getClass().getResource(filename).toExternalForm());
    }

    public MainController getController() {
        return controller;
    }

    public static void main(String[] args) {
        launch(args);
    }


}
