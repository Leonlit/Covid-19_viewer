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
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * FXML Controller class
 *
 * @author User
 */
public class CountryViewController implements Initializable {
    
    private int timeOut = 0;
    private CountryData data;
    private ArrayList<Integer> allCases, allDeaths, allRecovered, allActive;
    private ArrayList<String> dates;
    
    @FXML 
    private Label newCase, newDeath, newRecovered, 
                    totalCase, activeCase, totalDeath, totalRecovered;
    
    @FXML 
    AnchorPane graphPlace;
    
    @FXML
    private AnchorPane mainPane, newChart, totalChart;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }
    
    private void setupValues () {
        newCase.setText(Integer.toString(data.getNewCases()));
        newDeath.setText(Integer.toString(data.getNewDeaths()));
        newRecovered.setText(Integer.toString(data.getNewRecovered()));
        totalCase.setText(Integer.toString(data.getTotalCases()));
        activeCase.setText(Integer.toString(data.getActiveCases()));
        totalDeath.setText(Integer.toString(data.getTotalDeaths()));
        totalRecovered.setText(Integer.toString(data.getTotalRecovered()));
    }
    
    public void setupData (CountryData data) {
        this.data = data;
        setupValues();
        setupCountryGraph();
        showDetailedData(0);
        
    }
    
    private void showDetailedData (int forced) {
        final String apiURL = "https://api.covid19api.com/country/" + data.getSlug() +  "?from=2020-03-01T00:00:00Z&to=2020-04-01T00:00:00Z";
        String result = FileManagement.getFromFile(data.getSlug(), 0);
        
        try {
            if (result.equals("") || result == "" || forced == 1) {
                URL website = new URL(apiURL);
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
                    FileManagement.saveIntoFile(result, data.getSlug());
                }
            }
        } catch (MalformedURLException ex) {
            //remember to change to custom error handling
            System.out.println("Access point to the API has been changed. Searching for data history in repository");
        } catch (IOException ex) {
            System.out.println("Unable to connect with the API's server");
        } finally {
            if (!(result.equals("") || result == "")) {
                setupDetailedGraph(result);
            }else {
                //pop up error
            }
        }
    }
    
    private void setupCountryGraph () {
        String globalLegends[] = {"New Confirmed", "New Deaths", "New Recovered", "Total Confirmed", "Total Active", "Total Deaths", "Total Recovered"};

        //since everthing in JSON is string, we'll need to get the string one by one RIP lel.
        int globalStats[] = { 
                                data.getNewCases(),
                                data.getNewDeaths(),
                                data.getNewRecovered(),
                                data.getTotalCases(),
                                data.getActiveCases(),
                                data.getTotalDeaths(),
                                data.getTotalRecovered()
                                };
        timeOut = 0;
        MainPageController.drawPieGraph(Arrays.copyOfRange(globalStats, 0, 3), 
                                        Arrays.copyOfRange(globalLegends, 0, 3),
                                        newChart, mainPane);
        MainPageController.drawPieGraph(Arrays.copyOfRange(globalStats, 4, 7),
                                        Arrays.copyOfRange(globalLegends, 4, 7),
                                        totalChart, mainPane);
    }
    
    private void setupDetailedGraph (String result) {
        try {
            ArrayList<Integer> totalCases = new ArrayList<Integer>();
            ArrayList<Integer> deaths = new ArrayList<Integer>();
            ArrayList<Integer> recovered = new ArrayList<Integer>();
            ArrayList<Integer> active = new ArrayList<Integer>();
            ArrayList<String> legends = new ArrayList<String>();

            JSONArray data = new JSONArray(result);
            for (int x = 0; x< data.length();x++) {
                totalCases.add(data.getJSONObject(x).getInt("Confirmed"));
                deaths.add(data.getJSONObject(x).getInt("Deaths"));
                recovered.add(data.getJSONObject(x).getInt("Recovered"));
                active.add(data.getJSONObject(x).getInt("Active"));
                legends.add(data.getJSONObject(x).getString("Date").substring(0,10));
            }

            drawInitialAreaChart(totalCases, deaths, recovered, active, legends);
            //dataList.add(new PieChart.Data(legends[x], data[x]));
            
        }catch (JSONException ex) {
            System.out.println("File Integrity changed, requesting new data from server");
            if (timeOut < 4){
                //getGlobalData(1);
                timeOut++;
            }else {
                System.out.println("Couldn't load API data and there's no history data for the selected Item.");
            }
        }
    }
    
    private void drawInitialAreaChart (ArrayList<Integer> totalCases, ArrayList<Integer> deaths, ArrayList<Integer> recovered,
                                        ArrayList<Integer> active, ArrayList<String> legends) {
        allCases = totalCases;
        allDeaths = deaths;
        allRecovered = recovered;
        allActive = active;
        dates = legends;
        ObservableList<String> newLegends = FXCollections.observableArrayList();;
        for (int x = 0; x< legends.size();x++) {
            newLegends.add(legends.get(x));
        }
        
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        
        final AreaChart<String,Number> chart = new AreaChart<String,Number>(xAxis,yAxis);
        chart.setTitle("Country Data - All");
        
        Series TCases= new Series();
        TCases.setName("totalCases");
        for (int x = 0; x < totalCases.size();x++) {
            TCases.getData().add(new XYChart.Data(legends.get(x), totalCases.get(x)));
        }
        
        Series DCases= new Series();
        DCases.setName("totalCases");
        for (int x = 0; x < deaths.size();x++) {
            DCases.getData().add(new XYChart.Data(legends.get(x), deaths.get(x)));
        }
        
        Series RCases= new Series();
        RCases.setName("totalCases");
        for (int x = 0; x < recovered.size();x++) {
            RCases.getData().add(new XYChart.Data(legends.get(x), recovered.get(x)));
        }
        
        Series ACases= new Series();
        ACases.setName("totalCases");
        for (int x = 0; x < active.size();x++) {
            ACases.getData().add(new XYChart.Data(legends.get(x), active.get(x)));
        }
        
        graphPlace.setMaxSize(900.0, 300.0);
        chart.getData().addAll(TCases, DCases, RCases, ACases);
        graphPlace.getChildren().add(chart);
    }
    
}
