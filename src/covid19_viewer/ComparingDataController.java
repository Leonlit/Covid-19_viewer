/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package covid19_viewer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import org.json.JSONArray;

/**
 * FXML Controller class
 *
 * @author User
 */
public class ComparingDataController implements Initializable {
    
    @FXML private ComboBox countriesList;
    
    CountryData data[] = new CountryData[3];
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        getCountriesName(0);
    }
    
    @FXML
    private void addToCompare () {
        
    }
    
    private void getCountriesName(int forced) {
        String result ="";
        final String url = "https://api.covid19api.com/countries";
        try {
            result = FileManagement.getFromFile("countries", 0);
            if (result.equals("") || result == "" || forced == 1) {
                System.out.println("Getting data from Server");
                URL website = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) website.openConnection();
                conn.setRequestMethod("GET");
                conn.addRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36");
                conn.connect();

                int responseCode = conn.getResponseCode(); //200 means ok
                if (responseCode != 200) {
                    //remember to make a new custom error window for this refer last project
                    //implement various code response later
                    throw new RuntimeException("HttpResponseCode: " + responseCode);
                }else {
                    BufferedReader r  = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.forName("UTF-8")));

                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = r.readLine()) != null) {
                        sb.append(line);
                    }
                    System.out.println(sb.toString());
                    result = sb.toString(); 
                    FileManagement.saveIntoFile(result, "countries");
                }
            }else {
                System.out.println("Using History Data");
            }
        } catch (MalformedURLException ex) {
            //remember to change to custom error handling
            System.out.println("Access point to the API has been changed. Searching for data history in repository");
        } catch (IOException | RuntimeException ex) {
            System.out.println("Unable to connect with the API's server, searching for history data in directory");
            result = FileManagement.getFromFile("global", 1);
        }finally {
            if (!(result.equals("") || result == "")) {
                setupDropdown(result);
            }else {
                //pop up error
            }
        }
    }
    
    private void setupDropdown(String result) {
        ArrayList<String> countries = new ArrayList<String>();
        ArrayList<String> slugs = new ArrayList<String>();
        
        JSONArray data = new JSONArray(result);
        for (int x = 0; x< data.length();x++) {
            countries.add(data.getJSONObject(x).getString("Country"));
            slugs.add(data.getJSONObject(x).getString("Slug"));
        }
        ObservableList dataList = FXCollections.observableArrayList(countries);
        countriesList.setItems(dataList);
    }
    
}
