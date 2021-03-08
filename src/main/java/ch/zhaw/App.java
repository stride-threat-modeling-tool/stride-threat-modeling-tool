package ch.zhaw;
import ch.zhaw.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

public class App extends Application {


    private static final String TITLED_SKIN_STYLESHEET = "titledskins.css";
    static MainController controller;
    @Override
    public void start(final Stage stage) throws Exception {
        final URL location = getClass().getResource("main.fxml");
        final FXMLLoader loader = new FXMLLoader();
        final Parent root = loader.load(location.openStream());
        final Scene scene = new Scene(root, 830, 630);
        scene.getStylesheets().add(getClass().getResource(TITLED_SKIN_STYLESHEET).toExternalForm());
        stage.setScene(scene);
        stage.show();

       controller = loader.getController();
    }

    public static void main(String[] args) {

        launch(args);
        controller.fillModel();
    }


}
