/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package covid19_viewer;

/**
 *
 * @author User
 */
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBHandling {
    private final String HOST = "jdbc:derby://localhost:1527/Assignment_manager";
    private final String USER = "leonlit";
    private final String PASSWORD = "Nw>u)tp\\tvu4$Sb_";
    
    public int login(String username, String password) throws IOException {
        int stats = -1;
        PreparedStatement pstmt = null;
        try {
            Connection con = DriverManager.getConnection(HOST, USER, PASSWORD);
            String query = "SELECT * FROM LEONLIT.\"UsersInfo\" WHERE USERNAME=? AND PASSWORD=?";
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet results = pstmt.executeQuery();
            
            if(!results.next()) {
                ShowError.error("Account Not Found", "There's no account found with the credential provided");
            }else {
                stats = 1;
            }
        }catch (SQLException ex) {
            showDBErr("Unable to login!!!\n\n" + ex.getMessage());
        }finally {
            if(pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(DBHandling.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return stats;
    }
    
    public int  signUp (String username, String password) {
        PreparedStatement pstmt = null;
        int stats = 0;
        try {
            boolean exists = findUser(username);
            if (exists) {
                ShowError.error(username + " is already exists in database!!!", "The username " + username + 
                                            " already exists in the database, please use another one");
            }else {
                Connection con = DriverManager.getConnection(HOST, USER, PASSWORD);
                pstmt = con.prepareStatement("INSERT INTO LEONLIT.\"UsersInfo\""
                                           + "(USERNAME, PASSWORD) VALUES (?, ?)");
                
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                int affectedRows = pstmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Creating Recocrd failed, no rows affected.");
                }else {
                    ShowError.error("Succesfully Created account", "Successfully created a account, with username of " + username);
                    stats = 1;
                }
            }
            
        }catch (SQLException ex) {
            showDBErr("Database operation error.\n\n" + ex.getMessage());
            stats = -1;
        }
        finally {
            if(pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(DBHandling.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return stats;
    }
   
    public boolean findUser (String username) {
        PreparedStatement pstmt = null;
        boolean stats = false;
        try {
            Connection con = DriverManager.getConnection(HOST, USER, PASSWORD);
            String query = "SELECT USERNAME FROM LEONLIT.\"UsersInfo\" where USERNAME=?";
            pstmt = con.prepareStatement(query); 
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if(!rs.next()) {
                System.out.println("username available for sign up");
            }else {
                stats = true;
            }
            
        }catch (SQLException ex) {
            showDBErr("Database operation error.\n\n" + ex.getMessage());
        }finally {
            if(pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(DBHandling.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return stats;
    }
    
    private void showDBErr (String message) {
        ShowError.error("Database error notice!!!", message);
    }
}
