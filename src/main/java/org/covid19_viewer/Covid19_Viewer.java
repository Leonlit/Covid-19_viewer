
package org.covid19_viewer;

import java.net.URL;
import static javafx.application.Application.launch;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Covid19_Viewer extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainPage.fxml"));
        Parent root = loader.load();

        VBox vBox = new VBox();
        vBox.getChildren().add(root);
        Scene scene = new Scene(vBox);
        
        URL url = getClass().getResource("index.css");
        String css = url.toExternalForm(); 
        scene.getStylesheets().add(css);

        stage.setTitle("Covid-19 Viewer - main page");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.close();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
