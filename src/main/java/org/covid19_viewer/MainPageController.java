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
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author User
 */
public class MainPageController implements Initializable {
    
    private int timeOut = 0;
    private boolean viewingCountry = false;
    
    @FXML
    private Label newCase, newDeath, newRecovered, 
                    totalCase, activeCase, totalDeath, totalRecovered;
    
    @FXML
    private AnchorPane mainPane, newChart, totalChart;
    
    @FXML
    private TableView countryData;
    
    @FXML private TableColumn<CountryData, Integer> CountryName;
    @FXML private TableColumn<CountryData, Integer> NewCases;
    @FXML private TableColumn<CountryData, Integer> NewDeaths;
    @FXML private TableColumn<CountryData, Integer> NewRecovered;
    @FXML private TableColumn<CountryData, Integer> ActiveCases;
    @FXML private TableColumn<CountryData, Integer> TotalCases;
    @FXML private TableColumn<CountryData, Integer> TotalDeaths;
    @FXML private TableColumn<CountryData, Integer> TotalRecovered;
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        //label.setText("Hello World!");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //setting up tableView section
        CountryName.setCellValueFactory(new PropertyValueFactory<CountryData, Integer>("CountryName"));
        NewCases.setCellValueFactory(new PropertyValueFactory<CountryData, Integer>("NewCases"));
        NewDeaths.setCellValueFactory(new PropertyValueFactory<CountryData, Integer>("NewDeaths"));
        NewRecovered.setCellValueFactory(new PropertyValueFactory<CountryData, Integer>("NewRecovered"));
        ActiveCases.setCellValueFactory(new PropertyValueFactory<CountryData, Integer>("ActiveCases"));
        TotalCases.setCellValueFactory(new PropertyValueFactory<CountryData, Integer>("TotalCases"));
        TotalDeaths.setCellValueFactory(new PropertyValueFactory<CountryData, Integer>("TotalDeaths"));
        TotalRecovered.setCellValueFactory(new PropertyValueFactory<CountryData, Integer>("TotalRecovered"));
        
        countryData.setRowFactory(tv -> {
            TableRow<CountryData> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    CountryData rowData = row.getItem();
                    try {
                        Stage cont = new Stage();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("CountryView.fxml"));
                        Parent root = loader.load();

                        CountryViewController controller = loader.getController();
                        //Pass whatever data you want. You can have multiple method calls here
                        controller.setupData(rowData);
                        Scene countryView = new Scene(root);
                        URL cssFile = getClass().getResource("index.css");
                        String css = cssFile.toExternalForm(); 
                        countryView.getStylesheets().add(css);
                        cont.setScene(countryView);
                        cont.setTitle("Data for " + rowData.getCountryName());
                        cont.show();
                    } catch (IOException ex) {
                        Logger.getLogger(MainPageController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            return row ;
        });
        
        countryData.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        //loading main page
        getGlobalData(0);
    }
    
    private void getGlobalData (int forced) {
        String result = result = FileManagement.getFromFile("global");
        final String url = "https://api.covid19api.com/summary";
        try {
            
            if (result.equals("") || result == "" || forced == 1) {
                System.out.println("Getting global data from Server");
                URL website = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) website.openConnection();
                conn.setRequestMethod("GET");
                conn.addRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36");
                conn.connect();

                int responseCode = conn.getResponseCode(); //200 means ok
                if (responseCode != 200) {
                    throw new RuntimeException("HttpResponseCode: " + responseCode);
                }else {
                    BufferedReader r  = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.forName("UTF-8")));

                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = r.readLine()) != null) {
                        sb.append(line);
                    }
                    result = sb.toString(); 
                    FileManagement.saveIntoFile(result, "global");
                }
            }else {
                System.out.println("Using History Data for global stats");
            }
        } catch (MalformedURLException ex) {
            //remember to change to custom error handling
            System.out.println("Access point to the API has been changed. Searching for data history in repository");
        } catch (IOException | RuntimeException ex) {
            System.out.println("Unable to connect with the API's server, searching for history data in directory");
            result = FileManagement.getFromFile("global", 1);
            
        }finally {
            if (!(result.equals("") || result == "")) {
                if (forced == 1) {
                    ShowError.error("Unable to fetch new data from API server!!!", "Error: Unable to fetch new data from server, currently using old data for global stats");
                }
                setupMainPage(result);
            }else {
                ShowError.error("No data available!!!" ,"Couldn't load API data and there's no history data for global stats");
            }
        }
    }
    
    private void setupMainPage (String response) {
        try {
            JSONObject parsedJSON = new JSONObject(response);
            JSONObject globalData = (JSONObject) parsedJSON.get("Global");
            JSONArray countriesData = parsedJSON.getJSONArray("Countries");
        
            setupCountryData(countriesData);

            String globalLegends[] = {"New Confirmed", "New Deaths", "New Recovered", "Total Confirmed", "Total Active", "Total Deaths", "Total Recovered"};

            //since everthing in JSON is string, we'll need to get the string one by one RIP lel.
            int globalStats[] = { 
                                    Integer.parseInt(globalData.get("NewConfirmed").toString()),
                                    Integer.parseInt(globalData.get("NewDeaths").toString()),
                                    Integer.parseInt(globalData.get("NewRecovered").toString()),
                                    Integer.parseInt(globalData.get("TotalConfirmed").toString()),
                                    0,
                                    Integer.parseInt(globalData.get("TotalDeaths").toString()),
                                    Integer.parseInt(globalData.get("TotalRecovered").toString())
                                    };
            //calculating active cases
            globalStats[4] = globalStats[3] - globalStats[5] - globalStats[6];

            drawPieGraph(Arrays.copyOfRange(globalStats, 0, 3), Arrays.copyOfRange(globalLegends, 0, 3), newChart, mainPane);
            drawPieGraph(Arrays.copyOfRange(globalStats, 4, 7), Arrays.copyOfRange(globalLegends, 4, 7), totalChart, mainPane);
            updateGlobalCounter(globalStats);
            timeOut = 0;
        }catch (JSONException ex) {
            System.out.println("File Integrity changed, requesting new data from server");
            getGlobalData(1);
        }
    }
    
    public static void drawPieGraph (int data[], String legends[], Pane chartLocation, AnchorPane mainPane) {
        ObservableList dataList = FXCollections.observableArrayList();
        for (int x = 0; x< data.length;x++) {
            if (data[x] != 0) {
                dataList.add(new PieChart.Data(legends[x], data[x]));
            }
        }
        final PieChart chart = new PieChart(dataList);
        chart.setStyle("-fx-padding:10px;-fx-insets:0px;");
        final Label caption = new Label("");
        chart.getData().forEach((dataValue) -> {
            dataValue.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED, (MouseEvent e) -> {
                
                caption.setTextFill(Color.BLUE);
                caption.setStyle("-fx-font: 24 arial;" + 
                                "-fx-padding: 5px;" + 
                                "-fx-border-radius: 5px;" + 
                                "-fx-background-radius: 5px;" +
                                "-fx-background-color: rgba(255, 255, 255, 0.5);");
                caption.setTranslateX(e.getSceneX() + 10);
                caption.setTranslateY(e.getSceneY() - 20);
                caption.setText(String.valueOf(dataValue.getPieValue()));
                mainPane.getChildren().add(caption);
            });
        });
        
        chart.getData().forEach((dataValue) -> {
            dataValue.getNode().addEventHandler(MouseEvent.MOUSE_RELEASED, (MouseEvent e) -> {
                mainPane.getChildren().remove(caption);
            });
        });

        chart.setMaxHeight(400.0);
        chart.setMaxWidth(400.0);
        chart.setLegendVisible(false);
        chartLocation.getChildren().add(chart);
    }
    
    private void updateGlobalCounter (int data[]) {
        String texts[] = new String[data.length];
        for (int x = 0; x < data.length; x++) {
            texts[x] = Integer.toString(data[x]);
        }
        newCase.setText(texts[0]);
        newDeath.setText(texts[1]);
        newRecovered.setText(texts[2]);
        totalCase.setText(texts[3]);
        activeCase.setText(texts[4]);
        totalDeath.setText(texts[5]);
        totalRecovered.setText(texts[6]);
    }
    
    public void setupCountryData (JSONArray data) {
        int newCases, newDeaths, newRecovered, totalCases, totalDeaths, totalRecovered, activeCases = 0;
        String countryName, countrySlug, date;
        ObservableList<CountryData> dataList = FXCollections.observableArrayList();
        
        try {
            for (int i = 0; i < data.length(); i++) {
                countryName = data.getJSONObject(i).getString("Country");
                countrySlug = data.getJSONObject(i).getString("Slug");
                newCases = data.getJSONObject(i).getInt("NewConfirmed");
                newDeaths = data.getJSONObject(i).getInt("NewDeaths");
                newRecovered = data.getJSONObject(i).getInt("NewRecovered");
                totalCases = data.getJSONObject(i).getInt("TotalConfirmed");
                totalDeaths = data.getJSONObject(i).getInt("TotalDeaths");
                totalRecovered = data.getJSONObject(i).getInt("TotalRecovered");
                date = data.getJSONObject(i).getString("Date");
                activeCases = totalCases - totalDeaths - totalRecovered;
                dataList.add(new CountryData(countryName, countrySlug, newCases, newDeaths, newRecovered,
                            activeCases, totalCases, totalDeaths, totalRecovered, date));
            }
        }catch (JSONException ex) {
            System.out.println("Unable to connect with the API's server, searching for history data in directory");
            getGlobalData(1);
        }finally {
            countryData.setItems(dataList);
        }
    }
    
}
