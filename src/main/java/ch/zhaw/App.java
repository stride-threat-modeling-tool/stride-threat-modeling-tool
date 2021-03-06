package ch.zhaw;
import ch.zhaw.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class App extends Application {


    @Override
    public void start(final Stage stage) throws Exception {
        final URL location = getClass().getResource("main.fxml"); //$NON-NLS-1$
        final FXMLLoader loader = new FXMLLoader();
        final Parent root = loader.load(location.openStream());
        final Scene scene = new Scene(root, 830, 630);
        stage.setScene(scene);
        stage.show();

        final MainController controller = loader.getController();
    }


    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch(args);
    }


}
