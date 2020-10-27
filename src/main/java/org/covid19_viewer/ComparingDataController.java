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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;
import static org.covid19_viewer.CountryViewController.getDataConstrainer;
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
    @FXML private CheckBox casesT, deathsT, recoveredT, activeT, logChart;
    @FXML private ScrollPane graphCont;
    @FXML ComboBox durationOfDataToShow;
    
    private ArrayList<String> countryLegends = new ArrayList<String>();
    private ArrayList<CountriesData> dataCont = new ArrayList<CountriesData>();
    private ObservableList<CountriesData> dataList;
    private ArrayList<Integer> optionsMaxValue = new ArrayList<Integer>();
    private int constraints = 0;
    final int contraintsArr[] = {30, 90, 180, 365};

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        getCountriesName(0);
    }
    
    private void searchCountryName (String name) {
        FilteredList<CountriesData> filtered = new FilteredList(dataList);
        filtered.setPredicate(p -> p.getCountryName().toLowerCase().contains(name.toLowerCase()));
        ObservableList<CountriesData> newData = FXCollections.observableArrayList(filtered);
        setItems(newData);
    }
    
    @FXML
    private void addToCompare () {
        try {
            CountriesData selected = countriesList.getValue();
            if (selected.getCountryName() != null && !selected.toString().equals("Countries") && dataCont.size() <3){
                dataCont.add(selected);
                System.out.println("added " + selected.getCountryName() + "into the list" );
            }
            countriesList.getSelectionModel().clearSelection();
            setItems(dataList);
            countriesList.hide();
            updateSelectedNames();
        }catch (NullPointerException ex) {
            System.out.println("Error, no country name is selected");
        }
    }
    
    @FXML
    private void removeData (ActionEvent event) {
        boolean error = false;
        if(dataCont.size() > 0) {
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
    private void drawAll () {
        casesT.setSelected(true);
        recoveredT.setSelected(true);
        deathsT.setSelected(true);
        activeT.setSelected(true);
        constructGraph();
    }
    
    @FXML
    private void constructGraph () {
        try {
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
            if (!firstCont.isDisabled() && !firstCont.getText().equals("")) {
                countryList.add(dataCont.get(0));
            }
            if (!secondCont.isDisabled() && !secondCont.getText().equals("")) {
                countryList.add(dataCont.get(1));
            }
            if (!thirdCont.isDisabled() && !thirdCont.getText().equals("")) {
                countryList.add(dataCont.get(2));
            }
            
            ArrayList<CompareData> parsedData = new ArrayList<CompareData>();
            
            parsedData = getCountriesData(countryList, 0);
            
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
            for (int x = 0;x< countryList.size();x++) {
                title += countryList.get(x).getCountryName() + ", ";
            }
            title = title.substring(0, title.length() - 2);
            if (CountryViewController.isLogChartSelected(logChart)) {
                title += " (logarithmic chart view)";
            }
            chart.setTitle("Comparing Data - " + title);
            for (int country = 0; country < parsedData.size();country++) {
                for (int line=0;line<checks.length;line++) {
                    int max = Collections.max(options.get(line + (country * checks.length)));
                    optionsMaxValue.add(max);
                    XYChart.Series data = new XYChart.Series();
                    data.setName(parsedData.get(country).getCountryName() + "." + legends[Integer.parseInt(checks[line])]);
                    ArrayList<Integer> currItem = options.get(line + (country * checks.length));
                    for (int x = getDataConstrainer(currItem, constraints); x < currItem.size();x++) {
                        double value = CountryViewController.getBackLogValueIfSelected(options.get(line + (country * checks.length)).get(x), max, logChart);
                        data.getData().add(new XYChart.Data(parsedData.get(country).getAllDates().get(x), CountryViewController.isLogChartSelected(logChart) ? value : (int)value ));
                    }
                    chart.getData().add(data);
                }
            }
            CountryViewController.setupLineChart(chart, optionsMaxValue, logChart, mainPane, graphCont, graphPlace);
        }
        }catch (RuntimeException ex) {
            System.out.println("Error when loading history data" + ex.getMessage());
        }
        
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
            System.out.println("constraints: " + constraints);
            constructGraph();
        });
    }
    
    private ArrayList<CompareData> getCountriesData (ArrayList<CountriesData> countries, int forced) {
        int timeOut = 0;
        ArrayList<String> data = new ArrayList<String>();
        
        for (int country = 0; country < countries.size();country++) {
            final String apiURL = "https://api.covid19api.com/total/country/" + countries.get(country).getSlug();
            String result = FileManagement.getFromFile(countries.get(country).getSlug());

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
                        System.out.println("Try getting " + countries.get(country).getSlug() + " data from Server");
                        BufferedReader r  = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.forName("UTF-8")));
                        StringBuilder sb = new StringBuilder();
                        String line;

                        while ((line = r.readLine()) != null) {
                            sb.append(line);
                        }

                        result = sb.toString();
                        System.out.println("used api data");
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
                result = FileManagement.getFromFile(countries.get(country).getSlug(), 1);
                forced = 1;
            } finally {
                if (!(result.equals("") || result == "")) {
                    if (forced == 1) {
                        ShowError.error("Unable to fetch new data from API server!!!", "Error: Unable to fetch new data from server, currently using old data for " + countries.get(country).getCountryName());
                    }
                    data.add(result);
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException ex) {
                        System.out.println("Unable to sleep for awhile");
                    }
                    System.out.println("getted " + countries.get(country).getCountryName());
                }else {
                    if (timeOut > 3) {
                        ShowError.error("Error, No data available!!!", "Couldn't load API data and there's no history data for " + countries.get(country).getCountryName());
                        countries.remove(country);
                        country--;
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
        int timeOut=0;
        for (int x = 0;x<countries.size();) {
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
                
                String country = countries.get(x).getCountryName();
                String slug = countries.get(x).getSlug();
                x++;
                parsed.add(new CompareData(country, slug, allCases, allDeaths, allRecovered, allActive, dates));

            }catch (JSONException ex) {
                System.out.println("File Integrity changed, requesting new data from server");
                parsed = getCountriesData(countries, 1);
                timeOut++;
                if (timeOut == 4){
                    //this need to be popped up
                    ShowError.error("Error, No data available!!!", "Couldn't load API data and there's no history data for " + countries.get(x).getSlug());
                }
            }catch (RuntimeException ex) {
                x++;
                ShowError.error("Error, No data available!!!", "Couldn't load API data and there's no history data for " + countries.get(x).getSlug());
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
            result = FileManagement.getFromFile("countries", 1);
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
        
        dataList = FXCollections.observableArrayList();
        for (int x=0;x<countries.size();x++) {
            dataList.add(new CountriesData(countries.get(x), slugs.get(x)));
        }
        setItems(dataList);
        countriesList.hide();
        
        countriesList.getEditor().focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                countriesList.show();
            }else {
                countriesList.hide();
            }
        });
        
        countriesList.getEditor().setOnMouseClicked(event ->{
            if(event.getButton().equals(MouseButton.PRIMARY)){
                if(event.getClickCount() == 2){
                    return;
                }
                if (countriesList.getItems().size() != 0) {
                    countriesList.show();
                }else {
                    countriesList.hide();
                }
            }
        });
        
        countriesList.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            moveCaret(countriesList.getEditor().getText().length());
        });

        countriesList.setOnKeyReleased((t)->{
            KeyCode keyCode = t.getCode();
            if ( keyCode == KeyCode.UP || keyCode == KeyCode.DOWN
                || keyCode == KeyCode.RIGHT || keyCode == KeyCode.LEFT
                || keyCode == KeyCode.HOME
                || keyCode == KeyCode.END || keyCode == KeyCode.TAB || keyCode == keyCode.SPACE) {
                    return;
            }else {
                if(keyCode == KeyCode.BACK_SPACE){
                    String str = countriesList.getEditor().getText();
                    if (str != null && str.length() > 0) {
                        str = str.substring(0, str.length());
                    }
                    if(str != null){
                        countriesList.getEditor().setText(str);
                        moveCaret(str.length());
                    }
                    countriesList.getSelectionModel().clearSelection();
                }
                
                if(keyCode == KeyCode.ENTER && countriesList.getSelectionModel().getSelectedIndex()>-1)
                    return;
                
                final String countryName = countriesList.getEditor().getText();
                searchCountryName(countryName);
            }
        });
        setupDateDropdown();
        countriesList.setConverter(new StringConverter<CountriesData>() {
            @Override
            public String toString(CountriesData object) {
                if (object == null) {
                    return "";
                }
                return object.getCountryName();
            }

            @Override
            public CountriesData fromString(String string) {
                return countriesList.getItems().stream().filter(ap -> 
                    ap.getCountryName().equals(string)).findFirst().orElse(null);
            }
        });
    }
    
    private void setItems (ObservableList<CountriesData> data) {
        int rows = data.size();
        if (rows > 9) {
            rows = 9;
        }
        countriesList.setVisibleRowCount(rows);
        Collections.sort(data, getComparator());
        countriesList.hide();
        countriesList.setItems(data);
        if (data.size() != 0) {
            countriesList.show();
        }
    }
    
    private void moveCaret(int textLength) {
        countriesList.getEditor().positionCaret(textLength);
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