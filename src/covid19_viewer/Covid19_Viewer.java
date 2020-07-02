/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package covid19_viewer;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author User
 */
public class Covid19_Viewer extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainPage.fxml"));
        Parent root = loader.load();
        
        ToolBar toolBar = new ToolBar();

        Label compareData = new Label("Compare countries");
        compareData.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
            try {
                Stage compare = new Stage();
                FXMLLoader Cloader = new FXMLLoader(getClass().getResource("ComparingData.fxml"));
                Parent Croot = Cloader.load();
                
                Scene compareView = new Scene(Croot);
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

        stage.setTitle("Covid-19 Analyser");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
