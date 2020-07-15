/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package covid19_viewer;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * FXML Controller class
 *
 * @author User
 */
public class LoginController implements Initializable {
    
    @FXML private TextField user;
    @FXML private PasswordField pass;
    @FXML private Stage mainStage;
    
    private DBHandling DB;
    
    public void setupData(Stage stage) {
        this.mainStage = stage;
    }
    
    @FXML 
    public void checkLogin () {
        String username = "", password = "", error = "";
        if (!user.getText().equals("")) {
            username = user.getText();
        }else {
            error += "Username are needed to login";
        }
        if (!pass.getText().equals("")){
            password = pass.getText();
        }else {
            error += "Password are needed to login";
        }
        
        if (error.length() > 0) {
            ShowError.error("Error during login process!!!", error);
        }else {
            try {
                int stats = DB.login(username, password);
                if (stats == 1) {
                    showMainPage();
                }
            } catch (IOException ex) {
                ShowError.error("Database error", "Unable to login, " + ex.getMessage());
            }
        }
    }
    
    @FXML
    public void ShowSignUp () throws IOException {
        Stage cont = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("signUp.fxml"));
        Parent root = loader.load();

        SignUpController controller = loader.getController();
        //Pass whatever data you want. You can have multiple method calls here
        controller.setupData(mainStage, cont);
        Scene countryView = new Scene(root);
        cont.setScene(countryView);
        cont.setTitle("New user sign up");
        cont.show();
        cont.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent we) {
                mainStage.show();
            }
            
        });
        mainStage.hide();
    }
    
    private void showMainPage () throws IOException {
        Stage stage = new Stage();
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

        stage.setTitle("Covid-19 Analyser - main page");
        stage.setScene(scene);
        
        mainStage.close();
        stage.show();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        DB = new DBHandling();
        DB.testConnection();
    }
 
    
}
