/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.covid19_viewer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBHandling {
    Connection conn;
    
    public DBHandling (){
        makeSureTableExist("UsersInfo");
    }
    
    public int login(String username, String password) throws IOException {
        int stats = -1;
        PreparedStatement pstmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:users.db");
            String query = "SELECT USERNAME FROM UsersInfo WHERE USERNAME=? AND PASSWORD=?";
            pstmt = conn.prepareStatement(query);
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
        }catch (ClassNotFoundException ex) {
            showDBErr("Sqlite driver not found!!!\n\n" + ex.getMessage());
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
                Class.forName("org.sqlite.JDBC");
                conn = DriverManager.getConnection("jdbc:sqlite:users.db");
                pstmt = conn.prepareStatement("INSERT INTO UsersInfo"
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
            showDBErr("Unable to insert data into database.\n\n" + ex.getMessage());
            stats = -1;
        }catch (ClassNotFoundException ex) {
            showDBErr("Sqlite driver not found!!!\n\n" + ex.getMessage());
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
   
    public boolean findUser (String username) {
        PreparedStatement pstmt = null;
        boolean stats = false;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:users.db");
            String query = "SELECT USERNAME FROM UsersInfo where USERNAME=?";
            pstmt = conn.prepareStatement(query); 
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if(!rs.next()) {
                System.out.println("username available for sign up");
            }else {
                stats = true;
            }
            
        }catch (SQLException ex) {
            showDBErr("Unable to check username in database.\n\n" + ex.getMessage());
        }catch (ClassNotFoundException ex) {
            showDBErr("Sqlite driver not found!!!\n\n" + ex.getMessage());
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
    
    public void testConnection () {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:users.db");
            System.out.println("database Connected!!!" + conn.toString());
        }catch (SQLException ex) {
            ShowError.error("Database error notice!!!", "Error unable to connect to database.\n\n" + ex.getMessage());
        }catch (ClassNotFoundException ex) {
            showDBErr("Sqlite driver not found!!!\n\n" + ex.getMessage());
        }
    }
    
    //Make sure the table exists in the database to avoid error.
    //If the table doest not exist, create the table.
    //  @param tableName   - the name of the table to check.
    private void makeSureTableExist (String tableName) {
        PreparedStatement pstmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:users.db");
            String creatTableQuery = "CREATE TABLE IF NOT EXISTS " + tableName + 
                           "(ID INTEGER not null primary key AUTOINCREMENT," +
                            "username VARCHAR(100),password VARCHAR(255))";
            Statement stmt = conn.createStatement();
            stmt.execute(creatTableQuery);
        }catch (ClassNotFoundException ex) {
            Logger.getLogger(DBHandling.class.getName()).log(Level.SEVERE, null, ex);
        }catch (SQLException ex) {
            showDBErr("Unable to check username in database.\n\n" + ex.getMessage());
        }finally {
            if(pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(DBHandling.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
