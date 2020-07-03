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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.util.StringConverter;
import org.json.JSONArray;

/**
 * FXML Controller class
 *
 * @author User
 */
public class ComparingDataController implements Initializable {
    
    @FXML private ComboBox<CountriesData> countriesList;
    @FXML private Label firstCont, secondCont, thirdCont;
    @FXML private Button del1, del2, del3, hide1, hide2, hide3;
    @FXML private Button del[] = {del1, del2, del3};
    @FXML private Button hide[] = {hide1, hide2, hide3};
    
    ArrayList<CountriesData> dataCont = new ArrayList<CountriesData>();
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        getCountriesName(0);
        for (Button )
    }
    
    @FXML
    private void addToCompare () {
        try {
            CountriesData selected = countriesList.getValue();
            if (selected.getCountryName() != null && !selected.toString().equals("Countries") && dataCont.size() <=3){
                dataCont.add(selected);
            }
            updateSelectedNames();
        }catch (NullPointerException ex) {
            System.out.println("Error, no country name is selected");
        }
    }
        
    @FXML
    private void removeData (int place) {
        boolean error = false;
        switch (place) {
            case 0:
                dataCont.remove(0);
                break;
            case 1:
                dataCont.remove(1);
                break;
            case 2:
                dataCont.remove(2);
                break;
            default:
                System.out.println("Box location not found");
                error = true;
        }
        if (!error) {
            thirdCont.setText("");
        }
        updateSelectedNames();
    }
    
    private void updateSelectedNames () {
        for (int x = 0;x< dataCont.size();x++) {
            switch (x) {
                case 0:
                    firstCont.setText(dataCont.get(x).getCountryName());
                    break;
                case 1:
                    secondCont.setText(dataCont.get(x).getCountryName());
                    break;
                case 2:
                    thirdCont.setText(dataCont.get(x).getCountryName());
                    break;
                default:
                    System.out.println("error when putting countries name");
            }
        }
    }
    
    private void getCountriesName(int forced) {
        String result = FileManagement.getFromFile("countries");
        final String url = "https://api.covid19api.com/countries";
        try {
            result = FileManagement.getFromFile("countries");
            if (result.equals("") || result == "" || forced == 1) {
                System.out.println("Getting countries data from Server");
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
                    result = sb.toString(); 
                    FileManagement.saveIntoFile(result, "countries");
                }
            }else {
                System.out.println("Using Countries History Data");
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
        
        ObservableList<CountriesData> dataList = FXCollections.observableArrayList();
        for (int x=0;x<countries.size();x++) {
            dataList.add(new CountriesData(countries.get(x), slugs.get(x)));
        }
        
        countriesList.setItems(dataList);
        
        countriesList.setConverter(new StringConverter<CountriesData>() {

            @Override
            public String toString(CountriesData object) {
                return object.getCountryName();
            }

            @Override
            public CountriesData fromString(String string) {
                return countriesList.getItems().stream().filter(ap -> 
                    ap.getCountryName().equals(string)).findFirst().orElse(null);
            }
        });
        
    }
    
}
