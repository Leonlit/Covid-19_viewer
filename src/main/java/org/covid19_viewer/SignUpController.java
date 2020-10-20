/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.covid19_viewer;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author User
 */
public class SignUpController implements Initializable {
    private Stage mainStage, currStage;
    @FXML private TextField user;
    @FXML private PasswordField pass;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    
    @FXML 
    private void signUp (){
        DBHandling DB = new DBHandling();
        
        String username = "", password = "", error = "";
        if (!user.getText().equals("")) {
            username = user.getText();
        }else {
            error += "Username are needed to login";
        }
        if (!pass.getText().equals("")){
            password = pass.getText();
        }else{
            error += "Password are needed to login";
        }
        
        if (error.length() > 0) {
            ShowError.error("Error during login process!!!", error);
        }else {
            int stats = DB.signUp(username, password);
            if (stats == 1) {
                mainStage.show();
                currStage.close();
            }
        }
    }
    
    public void setupData (Stage mainStage, Stage currStage) {
        this.mainStage = mainStage;
        this.currStage = currStage;
    }
    
}
