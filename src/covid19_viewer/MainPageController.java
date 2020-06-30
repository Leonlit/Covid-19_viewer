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
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.logging.Logger.global;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author User
 */
public class MainPageController implements Initializable {
    
    @FXML
    private Label newCase, newDeath, newRecovered, totalCase, totalDeath, totalRecovered;
    
    @FXML
    private AnchorPane newChart, totalChart;
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        //label.setText("Hello World!");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            URL website = new URL("https://api.covid19api.com/summary");
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
                System.out.println(sb.toString());
                String result = sb.toString();
                
                JSONObject parsedJSON = new JSONObject(result);
                JSONObject arr = (JSONObject) parsedJSON.get("Global");
                
                int globalStats[] = { 
                                        Integer.parseInt(arr.get("NewConfirmed").toString()),
                                        Integer.parseInt(arr.get("NewDeaths").toString()),
                                        Integer.parseInt(arr.get("NewRecovered").toString()),
                                        Integer.parseInt(arr.get("TotalConfirmed").toString()),
                                        Integer.parseInt(arr.get("TotalDeaths").toString()),
                                        Integer.parseInt(arr.get("TotalRecovered").toString())
                                        };
                String globalLegends[] = {"New Confirmed", "New Deaths", "New Recovered", "Total Confirmed", "Total Deaths", "Total Recovered"};
                
                drawGraph(Arrays.copyOfRange(globalStats, 0, 3), Arrays.copyOfRange(globalLegends, 0, 3), newChart);
                drawGraph(Arrays.copyOfRange(globalStats, 3, 6), Arrays.copyOfRange(globalLegends, 3, 6), totalChart);
                updateCounter(globalStats);
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(MainPageController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MainPageController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void drawGraph (int data[], String legends[], Pane chartLocation) {
        ObservableList dataList = FXCollections.observableArrayList();
        for (int x = 0; x< data.length;x++) {
            dataList.add(new PieChart.Data(legends[x], data[x]));
        }
        
        final PieChart chart = new PieChart(dataList);
        chart.setStyle("-fx-padding:10px;-fx-insets:0px;");
        
        
        final Label caption = new Label("");
        caption.setTextFill(Color.DARKORANGE);
        caption.setStyle("-fx-font: 24 arial;");

        for (final PieChart.Data dataValue : chart.getData()) {
            dataValue.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED,
                new EventHandler<MouseEvent>() {
                    @Override public void handle(MouseEvent e) {
                        caption.setTranslateX(e.getSceneX());
                        caption.setTranslateY(e.getSceneY());
                        caption.setText(String.valueOf(dataValue.getPieValue()));
                     }
                });
        }

        chart.setMaxHeight(400.0);
        chart.setMaxWidth(400.0);
        chart.setLegendVisible(false);
        chartLocation.getChildren().add(chart);
    }
    
    private void updateCounter (int data[]) {
        newDeath.setText(Integer.toString(data[2]));
        newCase.setText(Integer.toString(data[0]));
        newRecovered.setText(Integer.toString(data[4]));
        totalCase.setText(Integer.toString(data[1]));
        totalRecovered.setText(Integer.toString(data[3]));
        totalDeath.setText(Integer.toString(data[5]));
    }
    
}
