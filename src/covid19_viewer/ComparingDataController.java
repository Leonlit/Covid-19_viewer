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
import java.util.Collections;
import java.util.Comparator;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * FXML Controller class
 *
 * @author User
 */
public class ComparingDataController implements Initializable {
    
    @FXML private ComboBox<CountriesData> countriesList;
    @FXML private Label firstCont, secondCont, thirdCont;
    @FXML private Button del1, del2, del3, hide1, hide2, hide3;
    @FXML private AnchorPane mainPane, graphPlace;
    @FXML private CheckBox casesT, deathsT, recoveredT, activeT;
    @FXML private ScrollPane graphCont;
    
    private ArrayList<String> countryLegends = new ArrayList<String>();
    private ArrayList<ArrayList<CountryData>> datas = new ArrayList<ArrayList<CountryData>>();
    private ArrayList<CountriesData> dataCont = new ArrayList<CountriesData>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        getCountriesName(0);
    }
    
    @FXML
    private void addToCompare () {
        try {
            CountriesData selected = countriesList.getValue();
            if (selected.getCountryName() != null && !selected.toString().equals("Countries") && dataCont.size() <3){
                dataCont.add(selected);
                System.out.println("added " + selected.getCountryName() + "into the list" );
            }
            updateSelectedNames();
        }catch (NullPointerException ex) {
            System.out.println("Error, no country name is selected");
        }
    }
    
    @FXML
    private void removeData (ActionEvent event) {
        boolean error = false;
        if(dataCont.size() > 0) {
            System.out.println(event.getSource());
            if (event.getSource().equals(del1)) {
                dataCont.remove(0);
            }else if (event.getSource().equals(del2)) {
                dataCont.remove(1);
            }else if (event.getSource().equals(del3)) {
                dataCont.remove(2);
            }else {
                error = true;
            }
        }
        if (!error) { 
            updateSelectedNames();
        }
    }
    
    @FXML
    private void hideData (ActionEvent event) {
        boolean error = false;
        if(dataCont.size() > 0) {
            System.out.println(event.getSource());
            if (event.getSource().equals(hide1)) {
                firstCont.setDisable(!firstCont.isDisabled());
            }else if (event.getSource().equals(hide2)) {
                secondCont.setDisable(!secondCont.isDisabled());
            }else if (event.getSource().equals(hide3)) {
                thirdCont.setDisable(!thirdCont.isDisabled());
            }else {
                error = true;
            }
        }
        if (!error) { 
            updateSelectedNames();
        }
    }
    
    @FXML
    private void ConstructGraph () {
        
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
            
            ArrayList<CountriesData> countryList = new ArrayList<CountriesData>();
            countryLegends.clear();
            if (!firstCont.isDisabled()) {
                countryList.add(dataCont.get(0));
            }
            if (!secondCont.isDisabled()) {
                countryList.add(dataCont.get(1));
            }
            if (!thirdCont.isDisabled()) {
                countryList.add(dataCont.get(2));
            }
            
            ArrayList<CompareData> parsedData = getCountriesData(countryList);
            
            ArrayList<ArrayList<Integer>> options = new ArrayList<ArrayList<Integer>>();
            for (int country = 0; country < parsedData.size();country++) {
                for (int x = 0 ; x< option.length;x++) {
                   switch (option[x]) {
                        case 0:
                            options.add(parsedData.get(country).getAllCases());
                            break;
                        case 1:
                            options.add(parsedData.get(country).getAllDeaths());
                            break;
                        case 2:
                            options.add(parsedData.get(country).getAllRecovered());
                            break;
                        case 3:
                            options.add(parsedData.get(country).getAllActive());
                            break;
                    }
                }
            }

            final CategoryAxis xAxis = new CategoryAxis();
            final NumberAxis yAxis = new NumberAxis();

            LineChart<String,Number> chart = new LineChart<String,Number>(xAxis,yAxis);
            String title = "";
            for (int x = 0;x< parsedData.size();x++) {
                title += parsedData.get(x).getCountryName() + ", ";
            }
            title = title.substring(0, title.length() - 2);
            chart.setTitle("Comparing Data - " + title);

            for (int country = 0; country < parsedData.size();country++) {
                System.out.println("country: " + country);
                for (int line=0;line<checks.length;line++) {
                    System.out.println("line: " + country);
                    XYChart.Series data = new XYChart.Series();
                    data.setName(parsedData.get(country).getCountryName() + "." + legends[Integer.parseInt(checks[line])]);
                    for (int x = 0; x < options.get(line + (country * checks.length) ).size();x++) {
                        data.getData().add(new XYChart.Data(parsedData.get(country).getAllDates().get(x), options.get(line + (country * checks.length)).get(x)));
                    }
                    chart.getData().add(data);
                }
            }
            setupLineChart(chart);
        }
        
    }
    
    private ArrayList<CompareData> getCountriesData (ArrayList<CountriesData> countries) {
        int timeOut = 0;
        ArrayList<String> data = new ArrayList<String>();
        
        for (int country = 0; country < countries.size();country++) {
            final String apiURL = "https://api.covid19api.com/total/country/" + countries.get(country).getSlug();
            String result = FileManagement.getFromFile(countries.get(country).getSlug());

            try {
                if (result.equals("") || result == "") {
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
                        System.out.println("Try getting " + countries.get(country).getSlug() + " data from Server");
                        BufferedReader r  = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.forName("UTF-8")));
                        StringBuilder sb = new StringBuilder();
                        String line;

                        while ((line = r.readLine()) != null) {
                            sb.append(line);
                        }

                        result = sb.toString();
                        FileManagement.saveIntoFile(result, countries.get(country).getSlug());
                    }
                }else {
                    System.out.println("Using Country " + countries.get(country).getSlug() + " History Data");
                }
            } catch (MalformedURLException ex) {
                //remember to change to custom error handling
                System.out.println("Access point to the API has been changed. Searching for data history in repository");
            } catch (IOException ex) {
                System.out.println("Unable to connect with the API's server");
            } finally {
                if (!(result.equals("") || result == "")) {
                    data.add(result);
                    System.out.println("getted " + countries.get(country).getCountryName());
                }else {
                    if (timeOut > 3) {
                        //error
                    }else {
                        timeOut++;
                        country--;
                    }
                }
            }
        }
        ArrayList<CompareData> parsedData = parseData(data, countries);
        return parsedData;
    }
    
    private ArrayList<CompareData> parseData (ArrayList<String> result, ArrayList<CountriesData> countries) {
        ArrayList<CompareData> parsed = new ArrayList<CompareData>();
        int counter = 0, timeOut=0;
        for (int x = 0;x<countries.size();x++) {
            try {
                ArrayList<Integer> allCases = new ArrayList<Integer>();
                ArrayList<Integer> allDeaths = new ArrayList<Integer>();
                ArrayList<Integer> allRecovered = new ArrayList<Integer>();
                ArrayList<Integer> allActive = new ArrayList<Integer>();
                ArrayList<String> dates = new ArrayList<String>();

                JSONArray json = new JSONArray(result.get(x));
                for (int y = 0;y < json.length();y++) {
                    allCases.add(json.getJSONObject(y).getInt("Confirmed"));
                    allDeaths.add(json.getJSONObject(y).getInt("Deaths"));
                    allRecovered.add(json.getJSONObject(y).getInt("Recovered"));
                    allActive.add(json.getJSONObject(y).getInt("Active"));
                    dates.add(json.getJSONObject(y).getString("Date").substring(0,10));
                }
                
                String country = countries.get(counter).getCountryName();
                String slug = countries.get(counter).getSlug();
                counter++;

                parsed.add(new CompareData(country, slug, allCases, allDeaths, allRecovered, allActive, dates));

            }catch (JSONException ex) {
                System.out.println("File Integrity changed, requesting new data from server");
                timeOut++;
                if (timeOut == 4){
                    //this need to be popped up
                    System.out.println("Couldn't load API data and there's no history data for the selected Item.");
                }
            }
        }
        return parsed;
    }
    
    private void updateSelectedNames () {
        firstCont.setText("");
        secondCont.setText("");
        thirdCont.setText("");
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
        Collections.sort(dataList, getComparator());
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
    
    private void setupLineChart (LineChart<String,Number> chart) {
        final Label caption = new Label("");
            for (XYChart.Series<String,Number> serie: chart.getData()){
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
    
    private static Comparator<CountriesData> getComparator() {
        return new Comparator<CountriesData>() {
           @Override
           public int compare(CountriesData o1, CountriesData o2) {
               String name = o1.getCountryName();
               String name2 = o2.getCountryName();

               // ordering is the natural String ordering in your example
               return name.compareTo(name2); 
           }
        };
    }
}
