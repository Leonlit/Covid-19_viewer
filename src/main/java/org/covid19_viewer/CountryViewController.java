package org.covid19_viewer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.json.JSONArray;
import org.json.JSONException;

public class CountryViewController implements Initializable {
    
    private CountryData data;
    private ArrayList<Integer> allCases, allNewCases, allDeaths, allRecovered, allActive;
    private ArrayList<String> dates;
    private ArrayList<Integer> optionsMaxValue = new ArrayList<Integer>();
    private int constraints = 0;
    final int contraintsArr[] = {30, 90, 180, 365};
    private Scene mainScene;
    
    @FXML private CheckBox totalCasesT, deathsT, recoveredT, activeT, newCasesT, logChart;
    
    @FXML private Label newCase, newDeath, newRecovered, 
          totalCase, activeCase, totalDeath, totalRecovered, countryName, date;
    
    @FXML private AnchorPane graphPlace;
    @FXML private ScrollPane graphCont;
    @FXML private ComboBox durationOfDataToShow;
    @FXML private AnchorPane mainPane, newChart, totalChart;

    @FXML
    private void drawSpecificGraph() {
        optionsMaxValue.clear();
        graphPlace.getChildren().clear();
        String checked = "";
        String legends[] = {"Total Cases", "Total New Daily Cases", "Total Deaths", "Total Recovered", "Total Active"};
        
        if (totalCasesT.isSelected()) {
            checked += "0";
        }
        if (newCasesT.isSelected()) {
            checked += "1";
        }
        if (deathsT.isSelected()) {
            checked += "2";
        }
        if (recoveredT.isSelected()) {
            checked += "3";
        }
        if(activeT.isSelected()) {
            checked += "4";
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
                        options.add(allNewCases);
                        break;
                    case 2:
                        options.add(allDeaths);
                        break;
                    case 3:
                        options.add(allRecovered);
                        break;
                    case 4:
                        options.add(allActive);
                        break;
                }
            }
            final CategoryAxis xAxis = new CategoryAxis();
            final NumberAxis yAxis = new NumberAxis();

            LineChart chart = new LineChart(xAxis,yAxis);
            String title = "";
            for (int x = 0;x<options.size();x++) {
                title += legends[Integer.parseInt(checks[x])] + ", ";
            }
            title = title.substring(0, title.length() - 2);
            title = data.getCountryName() + " Data - " + title;
            if (Helper.isLogChartSelected(logChart)) {
                title += " (Logarithmic chart view)";
            }
            chart.setTitle(title);

            for (int line = 0; line < options.size();line++) {
                Series data = new Series();
                data.setName(legends[Integer.parseInt(checks[line])]);
                int max = Collections.max(options.get(line));
                optionsMaxValue.add(max);
                int seriesSize = options.get(line).size();
                for (int x = Helper.getDataConstrainer(options.get(line), constraints); x < seriesSize;x++) {
                    double value = Helper.getBackLogValueIfSelected(options.get(line).get(x), max, logChart);
                    data.getData().add(new XYChart.Data(dates.get(x), Helper.isLogChartSelected(logChart) ? value : (int)value ));
                }
                chart.getData().add(data);
            }
            setupLineChart(chart, optionsMaxValue, logChart, mainPane, graphCont, graphPlace, mainScene);
        }
    }
    
    @FXML
    private void showAllBack () {
        checkAllSettings();
        graphPlace.getChildren().clear();
        drawInitialLineChart(allCases, allNewCases, allDeaths, allRecovered, allActive, dates, data.getCountryName());
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }
    
    public void setupData (CountryData data, Scene mainScene) {
        this.data = data;
        this.mainScene = mainScene;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                setupValues();
                setupCountryGraph();
                showDetailedData(0);
                Helper.changeCursorToNormal(mainScene);  
            }
        });
    }
    
    private void setupValues () {
        setupDateDropdown();
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
    
    private void setupDateDropdown () {
        String customDate[] = {"Last 30 days", "Last 90 days", "Last 180 days", "Last 365 days", "All"};
        ObservableList<String> items = FXCollections.observableArrayList(customDate);
        durationOfDataToShow.setItems(items);
        durationOfDataToShow.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            constraints = 0;
            if ((int)newValue < contraintsArr.length) {
                constraints = contraintsArr[(int)newValue];
            }
            drawSpecificGraph();
        });
    }
    
    private void checkAllSettings () {
        totalCasesT.setSelected(true);
        newCasesT.setSelected(true);
        recoveredT.setSelected(true);
        deathsT.setSelected(true);
        activeT.setSelected(true);
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
                    AppLogger.logging("HttpResponseCode: " + responseCode, 2);
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
                AppLogger.logging("Using Country " + data.getSlug() + " History Data", 1);
                System.out.println("Using Country " + data.getSlug() + " History Data");
            }
        } catch (MalformedURLException ex) {
            //remember to change to custom error handling
            AppLogger.logging("Access point to the API has been changed. Searching for data history in repository", 1);
            System.out.println("Access point to the API has been changed. Searching for data history in repository");
        } catch (IOException ex) {
            AppLogger.logging("Unable to connect with the API's server", 3);
            System.out.println("Unable to connect with the API's server");
            result = FileManagement.getFromFile(data.getSlug(), 1);
            forced = 1;
        } finally {
            if (!(result.equals("") || result == "")) {
                if (forced == 1) {
                    AppLogger.logging("Unable to connect with the API's server, using history data", 3);
                    ShowError.error("Unable to fetch new data from API server!!!", "Error: Unable to fetch new data from server, currently using old data for " + data.getSlug());
                }
                setupDetailedGraph(result);
            }else {
                AppLogger.logging("Couldn't load API data and there's no history data for " + data.getSlug(), 3);
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
            allNewCases = new ArrayList<Integer>();
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
                int newCases = 0;
                if (x == 0) {
                    newCases = allCases.get(x) - 0;
                }else {
                    newCases = allCases.get(x) - allCases.get(x - 1);
                }
                allNewCases.add(newCases);
            }
            String country = data.getJSONObject(0).getString("Country");
            
            drawInitialLineChart(allCases, allNewCases, allDeaths, allRecovered, allActive, dates, country);            
        }catch (JSONException ex) {
            AppLogger.logging("File Integrity changed, requesting new data from server", 1);
            System.out.println("File Integrity changed, requesting new data from server");
            showDetailedData(1);
        }catch (RuntimeException ex) {
            AppLogger.logging(ex.getMessage(), 1);
            ex.printStackTrace();
        }
    }
    
    private void drawInitialLineChart (ArrayList<Integer> totalCases, ArrayList<Integer> totalNewCases, ArrayList<Integer> deaths, ArrayList<Integer> recovered,
                                        ArrayList<Integer> active, ArrayList<String> legends, String countryName) {
        checkAllSettings();
//        allCases = totalCases;
//        allDeaths = deaths;
//        allRecovered = recovered;
//        allActive = active;
        
        final ArrayList allData[] = {totalCases, totalNewCases, deaths, recovered, active};
        final String seriesName[] = {"total Cases","Daily New Cases", "Total Deaths", "Total Recovered", "Total Active"};
        try {
            final CategoryAxis xAxis = new CategoryAxis();
            final NumberAxis yAxis = new NumberAxis();

            LineChart chart = new LineChart(xAxis,yAxis);
            String title = countryName + " Data - All";
            if (Helper.isLogChartSelected(logChart)) {
                title += " (logarithmic chart view)";
            }
            chart.setTitle(title);
            
            for (int index=0;index < seriesName.length;index++) {
                Series series = new Series();
                series.setName(seriesName[index]);
                for (int x = Helper.getDataConstrainer(allData[index], constraints); x < allData[index].size();x++) {
                    ArrayList<Integer> temp = allData[index];
                    int max = Collections.max(temp);
                    optionsMaxValue.add(max);
                    double value = Helper.getBackLogValueIfSelected(temp.get(x), max, logChart);
                    series.getData().add(new XYChart.Data(legends.get(x), Helper.isLogChartSelected(logChart) ? value : (int)value ));
                }
                chart.getData().add(series);
            }
            chart.setLegendVisible(true);
            setupLineChart(chart, optionsMaxValue, logChart, mainPane, graphCont, graphPlace, mainScene);
        }catch (NullPointerException ex){
            AppLogger.logging("Couldn't load API data and there's no history data for detailed data on " + data.getCountryName(), 3);
            ex.printStackTrace();
            ShowError.error("No data available for graph!!!" ,"Couldn't load API data and there's no history data for detailed data on " + data.getCountryName());
        }catch (RuntimeException ex) {
            AppLogger.logging(ex.getMessage(), 3);
            ex.printStackTrace();       
        }
    }
    
    
    
    public static void setupLineChart (LineChart<String,Number> chart, ArrayList<Integer> optionsMax,
                                CheckBox logChart, AnchorPane mainPane, ScrollPane graphCont,
                                AnchorPane graphPlace, Scene mainScene) {
        try {
            int counter = 0;
            for (Series<String,Number> serie: chart.getData()){
                int max = optionsMax.get(counter);
                for (XYChart.Data<String, Number> item: serie.getData()){
                    final Label caption = new Label("");
                    final Label date = new Label("");
                    final VBox container = new VBox();
                    item.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED, (MouseEvent e) -> {
                        caption.setTextFill(Color.BLACK);
                        container.setStyle("-fx-font: 22 arial;" + 
                                        "-fx-padding: 5px;" + 
                                        "-fx-border-radius: 5px;" + 
                                        "-fx-background-radius: 5px;" +
                                        "-fx-background-color: rgba(255, 255, 255, 0.5);");
                        container.setTranslateX(e.getSceneX() - 50);
                        container.setTranslateY(e.getSceneY() - 20);
                        final int value = Helper.getBackValueFromLog(
                                                Double.parseDouble(
                                                    String.valueOf(
                                                            item.getYValue()
                                                    )
                                                ),
                                            max, logChart);
                        date.setStyle("-fx-font: 18 arial;");
                        caption.setText(Integer.toString(value));
                        try {
                            String dateValue = item.getXValue();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            Date convertedDate = new Date();
                            convertedDate = dateFormat.parse(dateValue);
                            SimpleDateFormat changedFormaat = new SimpleDateFormat("dd-MMM-yyyy");
                            String changedValue = changedFormaat.format(convertedDate);
                            date.setText(changedValue);
                            container.getChildren().addAll(caption, date);
                            mainPane.getChildren().add(container);
                        } catch (ParseException ex) {
                            AppLogger.logging(ex.getMessage(), 3);
                            ex.printStackTrace();
                        }catch (Exception ex) {
                            AppLogger.logging(ex.getMessage(), 3);
                            ex.printStackTrace();
                        }
                        
                    });
                    item.getNode().addEventHandler(MouseEvent.MOUSE_RELEASED, (MouseEvent e) -> {
                        try{
                            container.getChildren().removeAll(caption, date);
                            mainPane.getChildren().remove(container);
                        }catch (Exception ex){
                            AppLogger.logging(ex.getMessage(), 3);
                            ex.printStackTrace();
                        }
                    });
                }
            }
            Helper.changeCursorToNormal(mainScene);
            chart.setMinWidth(1400);
            graphCont.setMinViewportWidth(1400);
            graphPlace.getChildren().clear();
            graphPlace.getChildren().add(chart);
            chart.setLegendVisible(true);
        }catch (Exception ex) {
            AppLogger.logging(ex.getMessage(), 3);
            ex.printStackTrace();
        }
    }
    
    
}
