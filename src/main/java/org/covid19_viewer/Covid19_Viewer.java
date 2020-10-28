
package org.covid19_viewer;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.application.Application.launch;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Covid19_Viewer extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainPage.fxml"));
        Parent root = loader.load();

        ToolBar toolBar = new ToolBar();
        toolBar.setId("cmprBtn");

        Label compareData = new Label("Compare countries");
        compareData.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
            try {
                Stage compare = new Stage();
                FXMLLoader Cloader = new FXMLLoader(getClass().getResource("ComparingData.fxml"));
                Parent Croot = Cloader.load();
                ComparingDataController controller = Cloader.getController();

                Scene compareView = new Scene(Croot);
                URL cssFile = getClass().getResource("index.css");
                String css = cssFile.toExternalForm(); 
                compareView.getStylesheets().add(css);
                controller.storeScene(compareView);
                compare.setScene(compareView);
                compare.setTitle("Countries Data Comparer");
                compare.show();
            } catch (IOException ex) {
                Logger.getLogger(MainPageController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        toolBar.getItems().add(compareData);

        VBox vBox = new VBox(toolBar);
        vBox.getChildren().add(root);
        Scene scene = new Scene(vBox);
        
        URL url = getClass().getResource("index.css");
        String css = url.toExternalForm(); 
        scene.getStylesheets().add(css);

        stage.setTitle("Covid-19 Analyser - main page");
        stage.setScene(scene);
        
        stage.close();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
