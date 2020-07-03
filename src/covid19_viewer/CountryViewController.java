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
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
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
    
    @FXML private CheckBox casesT, deathsT, recoveredT, activeT;
    
    @FXML 
    private Label newCase, newDeath, newRecovered, 
                    totalCase, activeCase, totalDeath, totalRecovered, countryName, date;
    
    @FXML AnchorPane graphPlace, subPane;
    
    @FXML ScrollPane graphCont;
    
    @FXML
    private AnchorPane mainPane, newChart, totalChart;
    
    @FXML
    private void drawSpecificGraph() {
        graphPlace.getChildren().clear();
        String checked = "";
        String legends[] = {"Total Cases", "Total Deaths", "Total Recovered", "Total Active"};
        
        if (casesT.isSelected()) {
            checked += "0";
        }
        if (deathsT.isSelected()) {
            checked += "1";
        }
        if (recoveredT.isSelected()) {
            checked += "2";
        }
        if(activeT.isSelected()) {
            checked += "3";
        }
        if (checked.length() > 0) {
            String checks[] = checked.split("");
            int option[] = new int[checks.length];
            int counter = 0;
            for (String i : checks) {
                option[counter++] = Integer.parseInt(i);
            }
            ArrayList<ArrayList<Integer>> options = new ArrayList<ArrayList<Integer>>();
            for (int x = 0 ; x< option.length;x++) {
               switch (option[x]) {
                    case 0:
                        options.add(allCases);
                        break;
                    case 1:
                        options.add(allDeaths);
                        break;
                    case 2:
                        options.add(allRecovered);
                        break;
                    case 3:
                        options.add(allActive);
                        break;
                }
            }

            final CategoryAxis xAxis = new CategoryAxis();
            final NumberAxis yAxis = new NumberAxis();

            LineChart<String,Number> chart = new LineChart<String,Number>(xAxis,yAxis);
            String title = "";
            for (int x = 0;x<options.size();x++) {
                title += legends[Integer.parseInt(checks[x])] + ", ";
            }
            title = title.substring(0, title.length() - 2);
            chart.setTitle("Country Data - " + title);

            for (int line = 0; line < options.size();line++) {
                Series data = new Series();
                data.setName(legends[Integer.parseInt(checks[line])]);
                for (int x = 0; x < options.get(line).size();x++) {
                    data.getData().add(new XYChart.Data(dates.get(x), options.get(line).get(x)));
                }
                chart.getData().addAll(data);
            }
            setupLineChart(chart);
        }
    }
    
    @FXML
    private void showAllBack () {
        drawInitialLineChart(allCases, allDeaths, allRecovered, allActive, dates);
    }
    
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
        countryName.setText(data.getCountryName());
        date.setText(data.getDate());
    }
    
    public void setupData (CountryData data) {
        this.data = data;
        setupValues();
        setupCountryGraph();
        showDetailedData(0);
        
    }
    
    private void showDetailedData (int forced) {
        final String apiURL = "https://api.covid19api.com/total/country/" + data.getSlug();
        String result = FileManagement.getFromFile(data.getSlug());
        
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
                    System.out.println("Try getting " + data.getSlug() + " data from Server");
                    BufferedReader r  = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.forName("UTF-8")));
                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = r.readLine()) != null) {
                        sb.append(line);
                    }

                    result = sb.toString();
                    FileManagement.saveIntoFile(result, data.getSlug());
                }
            }else {
                System.out.println("Using Country " + data.getSlug() + " History Data");
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
            allCases = new ArrayList<Integer>();
            allDeaths = new ArrayList<Integer>();
            allRecovered = new ArrayList<Integer>();
            allActive = new ArrayList<Integer>();
            dates = new ArrayList<String>();

            JSONArray data = new JSONArray(result);
            for (int x = 0; x< data.length();x++) {
                allCases.add(data.getJSONObject(x).getInt("Confirmed"));
                allDeaths.add(data.getJSONObject(x).getInt("Deaths"));
                allRecovered.add(data.getJSONObject(x).getInt("Recovered"));
                allActive.add(data.getJSONObject(x).getInt("Active"));
                dates.add(data.getJSONObject(x).getString("Date").substring(0,10));
            }

            drawInitialLineChart(allCases, allDeaths, allRecovered, allActive, dates);
            //dataList.add(new PieChart.Data(legends[x], data[x]));
            
        }catch (JSONException ex) {
            System.out.println("File Integrity changed, requesting new data from server");
            if (timeOut < 4){
                showDetailedData(1);
                timeOut++;
            }else {
                //this need to be popped up
                System.out.println("Couldn't load API data and there's no history data for the selected Item.");
            }
        }
    }
    
    private void drawInitialLineChart (ArrayList<Integer> totalCases, ArrayList<Integer> deaths, ArrayList<Integer> recovered,
                                        ArrayList<Integer> active, ArrayList<String> legends) {
        allCases = totalCases;
        allDeaths = deaths;
        allRecovered = recovered;
        allActive = active;
        dates = legends;
        
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        
        LineChart<String,Number> chart = new LineChart<String,Number>(xAxis,yAxis);
        chart.setTitle("Country Data - All");
        
        Series TCases= new Series();
        TCases.setName("total Cases");
        for (int x = 0; x < totalCases.size();x++) {
            TCases.getData().add(new XYChart.Data(legends.get(x), totalCases.get(x)));
        }
        
        Series DCases= new Series();
        DCases.setName("Total Deaths");
        for (int x = 0; x < deaths.size();x++) {
            DCases.getData().add(new XYChart.Data(legends.get(x), deaths.get(x)));
        }
        
        Series RCases= new Series();
        RCases.setName("Total Recovered");
        for (int x = 0; x < recovered.size();x++) {
            RCases.getData().add(new XYChart.Data(legends.get(x), recovered.get(x)));
        }
        
        Series ACases= new Series();
        ACases.setName("Total Active");
        for (int x = 0; x < active.size();x++) {
            ACases.getData().add(new XYChart.Data(legends.get(x), active.get(x)));
        }
        
        chart.setLegendVisible(true);
        //chart.getData().
        chart.getData().addAll(TCases, DCases, RCases, ACases);
        
        setupLineChart(chart);
    }
    
    private void setupLineChart (LineChart<String,Number> chart) {
        final Label caption = new Label("");
            for (Series<String,Number> serie: chart.getData()){
                for (XYChart.Data<String, Number> item: serie.getData()){
                    item.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED, (MouseEvent e) -> {
                        caption.setTextFill(Color.BLACK);
                        caption.setStyle("-fx-font: 24 arial;");
                        caption.setTranslateX(e.getSceneX() - 50);
                        caption.setTranslateY(e.getSceneY() - 20);
                        caption.setText(String.valueOf(item.getYValue()));
                        mainPane.getChildren().add(caption);
                    });
                    item.getNode().addEventHandler(MouseEvent.MOUSE_RELEASED, (MouseEvent e) -> {
                        mainPane.getChildren().remove(caption);
                    });
                }
            }

            chart.setMinWidth(1400);
            graphCont.setMinViewportWidth(1400);
            graphPlace.getChildren().add(chart);
            chart.setLegendVisible(true);
    }
    
}
