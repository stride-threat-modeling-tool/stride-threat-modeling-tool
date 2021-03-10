package ch.zhaw;
import ch.zhaw.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

public class App extends Application {


    private static final String GENERAL_STYLESHEET = "styles.css";
    private static final String DATA_STORE_STYLESHEET = "DataStore.css";
    private static final String EXTERNAL_ENTITY_STYLESHEET = "ExternalEntity.css";
    static MainController controller;
    @Override
    public void start(final Stage stage) throws Exception {
        final URL location = getClass().getResource("main.fxml");
        final FXMLLoader loader = new FXMLLoader();
        final Parent root = loader.load(location.openStream());
        Scene scene = new Scene(root, 830, 630);
        addStyleSheetToScene(scene, DATA_STORE_STYLESHEET);
        addStyleSheetToScene(scene, EXTERNAL_ENTITY_STYLESHEET);
        addStyleSheetToScene(scene, GENERAL_STYLESHEET);
        stage.setScene(scene);
        stage.show();
       controller = loader.getController();
    }

    private void addStyleSheetToScene(Scene scene, String filename){
        scene.getStylesheets().add(getClass().getResource(filename).toExternalForm());
    }

    public static void main(String[] args) {
        launch(args);
    }


}
