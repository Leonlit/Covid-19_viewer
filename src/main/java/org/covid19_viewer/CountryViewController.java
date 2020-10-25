/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.covid19_viewer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * FXML Controller class
 *
 * @author User
 */
public class CountryViewController implements Initializable {
    
    private CountryData data;
    private ArrayList<Integer> allCases, allDeaths, allRecovered, allActive;
    private ArrayList<String> dates;
    private ArrayList<Integer> optionsMaxValue = new ArrayList<Integer>();
    
    @FXML private CheckBox casesT, deathsT, recoveredT, activeT, logChart;
    
    @FXML 
    private Label newCase, newDeath, newRecovered, 
                    totalCase, activeCase, totalDeath, totalRecovered, countryName, date;
    
    @FXML AnchorPane graphPlace, subPane;
    
    @FXML ScrollPane graphCont;
    
    @FXML
    private AnchorPane mainPane, newChart, totalChart;
    
    @FXML
    private void drawSpecificGraph() {
        optionsMaxValue.clear();
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
            title = data.getCountryName() + " Data - " + title;
            if (isLogChartSelected()) {
                title += " (Logarithm chart view)";
            }
            chart.setTitle(title);

            for (int line = 0; line < options.size();line++) {
                Series data = new Series();
                data.setName(legends[Integer.parseInt(checks[line])]);
                int max = Collections.max(options.get(line));
                optionsMaxValue.add(max);
                for (int x = 0; x < options.get(line).size();x++) {
                    double value = getBackLogValueIfSelected(options.get(line).get(x), max);
                    data.getData().add(new XYChart.Data(dates.get(x), isLogChartSelected() ? value : (int)value ));
                }
                chart.getData().add(data);
            }
            setupLineChart(chart);
        }
    }
    
    @FXML
    private void showAllBack () {
        graphPlace.getChildren().clear();
        drawInitialLineChart(allCases, allDeaths, allRecovered, allActive, dates, data.getCountryName());
    }
    
    @FXML
    
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
            result = FileManagement.getFromFile(data.getSlug(), 1);
            forced = 1;
        } finally {
            if (!(result.equals("") || result == "")) {
                if (forced == 1) {
                    ShowError.error("Unable to fetch new data from API server!!!", "Error: Unable to fetch new data from server, currently using old data for " + data.getSlug());
                }
                setupDetailedGraph(result);
            }else {
                ShowError.error("No data available!!!" ,"Couldn't load API data and there's no history data for " + data.getSlug());
            }
        }
    }
    
    private void setupCountryGraph () {
        String globalLegends[] = {"New Confirmed", "New Deaths", "New Recovered", "Total Confirmed", "Total Active", "Total Deaths", "Total Recovered"};

        //since everthing in JSON is string, we'll need to get the string one by one RIP lel.
        int countryStats[] = { 
                                data.getNewCases(),
                                data.getNewDeaths(),
                                data.getNewRecovered(),
                                data.getTotalCases(),
                                data.getActiveCases(),
                                data.getTotalDeaths(),
                                data.getTotalRecovered()
                                };
        MainPageController.drawPieGraph(Arrays.copyOfRange(countryStats, 0, 3), 
                                        Arrays.copyOfRange(globalLegends, 0, 3),
                                        newChart, mainPane);
        MainPageController.drawPieGraph(Arrays.copyOfRange(countryStats, 4, 7),
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
            String country = data.getJSONObject(0).getString("Country");
            
            drawInitialLineChart(allCases, allDeaths, allRecovered, allActive, dates, country);
            //dataList.add(new PieChart.Data(legends[x], data[x]));
            
        }catch (JSONException ex) {
            System.out.println("File Integrity changed, requesting new data from server");
            showDetailedData(1);
        }
    }
    
    private void drawInitialLineChart (ArrayList<Integer> totalCases, ArrayList<Integer> deaths, ArrayList<Integer> recovered,
                                        ArrayList<Integer> active, ArrayList<String> legends, String countryName) {
        allCases = totalCases;
        allDeaths = deaths;
        allRecovered = recovered;
        allActive = active;
        dates = legends;
        try {
            final CategoryAxis xAxis = new CategoryAxis();
            final NumberAxis yAxis = new NumberAxis();

            LineChart<String,Number> chart = new LineChart<String,Number>(xAxis,yAxis);
            chart.setTitle(countryName + " Data - All");

            Series TCases= new Series();
            TCases.setName("total Cases");
            for (int x = 0; x < totalCases.size();x++) {
                int max = Collections.max(totalCases);
                optionsMaxValue.add(max);
                double value = getBackLogValueIfSelected(totalCases.get(x), max);
                TCases.getData().add(new XYChart.Data(legends.get(x), isLogChartSelected() ? value : (int)value ));
            }

            Series DCases= new Series();
            DCases.setName("Total Deaths");
            for (int x = 0; x < deaths.size();x++) {
                int max = Collections.max(deaths);
                optionsMaxValue.add(max);
                double value = getBackLogValueIfSelected(deaths.get(x), max);
                DCases.getData().add(new XYChart.Data(legends.get(x), isLogChartSelected() ? value : (int)value ));
            }

            Series RCases= new Series();
            RCases.setName("Total Recovered");
            for (int x = 0; x < recovered.size();x++) {
                int max = Collections.max(recovered);
                optionsMaxValue.add(max);
                double value = getBackLogValueIfSelected(recovered.get(x), max);
                RCases.getData().add(new XYChart.Data(legends.get(x), isLogChartSelected() ? value : (int)value ));
            }

            Series ACases= new Series();
            ACases.setName("Total Active");
            for (int x = 0; x < active.size();x++) {
                int max = Collections.max(active);
                optionsMaxValue.add(max);
                double value = getBackLogValueIfSelected(active.get(x), max);
                ACases.getData().add(new XYChart.Data(legends.get(x), isLogChartSelected() ? value : (int)value ));
            }

            chart.setLegendVisible(true);
            chart.getData().addAll(TCases, DCases, RCases, ACases);

            setupLineChart(chart);
        }catch (NullPointerException ex){
            System.out.println(ex);
            ShowError.error("No data available for graph!!!" ,"Couldn't load API data and there's no history data for detailed data on " + data.getCountryName());
        }
    }
    
    private double getBackLogValueIfSelected (int value, int max) {
        if (isLogChartSelected() && value == 0) {
            return 0;
        }else if (isLogChartSelected()){
            return logOfBase(max, value);
        }
        return value;
    }
    
    private int getBackValueFromLog (double value, int max) {
        if (isLogChartSelected()){
            return getBackFromBase(max, value);
        }
        return (int)value;
    }
    
    public double logOfBase(int base, double num) {
        double value = Math.log(num) / Math.log(base);
        if (Double.isInfinite(value)) {
            return 0;
        }
        return value;
    }
    
    public int getBackFromBase(int base, double num) {
        return (int)Math.exp(num * Math.log(base));
    }
    
    private boolean isLogChartSelected() {
        return logChart.isSelected();
    }
    
    private void setupLineChart (LineChart<String,Number> chart) {
        final Label caption = new Label("");
        int counter = 0;
            for (Series<String,Number> serie: chart.getData()){
                int max = optionsMaxValue.get(counter);
                for (XYChart.Data<String, Number> item: serie.getData()){
                    item.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED, (MouseEvent e) -> {
                        caption.setTextFill(Color.BLACK);
                        caption.setStyle("-fx-font: 24 arial;" + 
                                        "-fx-padding: 5px;" + 
                                        "-fx-border-radius: 5px;" + 
                                        "-fx-background-radius: 5px;" +
                                        "-fx-background-color: rgba(255, 255, 255, 0.5);");
                        caption.setTranslateX(e.getSceneX() - 50);
                        caption.setTranslateY(e.getSceneY() - 20);
                        final int value = getBackValueFromLog(
                                                Double.parseDouble(
                                                    String.valueOf(
                                                            item.getYValue()
                                                    )
                                                ),
                                            max);
                        caption.setText(Integer.toString(value));
                        mainPane.getChildren().add(caption);
                    });
                    item.getNode().addEventHandler(MouseEvent.MOUSE_RELEASED, (MouseEvent e) -> {
                        mainPane.getChildren().remove(caption);
                    });
                }
                counter++;
            }

            chart.setMinWidth(1400);
            graphCont.setMinViewportWidth(1400);
            graphPlace.getChildren().add(chart);
            chart.setLegendVisible(true);
    }
    
}
