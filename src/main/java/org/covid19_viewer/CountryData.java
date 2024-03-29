
package org.covid19_viewer;

import java.util.ArrayList;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author User
 */
public class CountryData extends CountriesData{
    private final SimpleIntegerProperty newCases, newDeaths, newRecovered, activeCases,
                                        totalCases, totalDeaths, totalRecovered;
    private String date;
    
    public CountryData (String countryName, String slug, int newCases, int newDeaths, int newRecovered,
                        int activeCases, int totalCases, int totalDeaths, int totalRecovered, String date) {
        super(countryName, slug);
        this.newCases = new SimpleIntegerProperty(newCases);
        this.newDeaths = new SimpleIntegerProperty(newDeaths);
        this.newRecovered = new SimpleIntegerProperty(newRecovered);
        this.activeCases = new SimpleIntegerProperty(activeCases);
        this.totalCases = new SimpleIntegerProperty(totalCases);
        this.totalDeaths = new SimpleIntegerProperty(totalDeaths);
        this.totalRecovered = new SimpleIntegerProperty(totalRecovered);
        this.date = date;
    }
    
    public String getDate() {
        String token[] = date.substring(0,10).split("-");
        return token[2] + "/" + token[1] + "/" + token[0];
    }
    
    public int getNewCases () {
        return newCases.get();
    }
    
    public int getNewDeaths () {
        return newDeaths.get();
    }
    
    public int getNewRecovered () {
        return newRecovered.get();
    }
    public int getActiveCases () {
        return activeCases.get();
    }
    
    public int getTotalCases () {
        return totalCases.get();
    }
    
    public int getTotalDeaths () {
        return totalDeaths.get();
    }
    
    public int getTotalRecovered () {
        return totalRecovered.get();
    }        
}

class CountriesData {
    private final SimpleStringProperty countryName;
    private String slug;
    
    public CountriesData (String country, String slug) {
        this.slug = slug;
        countryName = new SimpleStringProperty(country);
    }
    
    public String getCountryName () {
        return countryName.get();
    }
    
    public String getSlug () {
        return slug;
    }
}

class CompareData extends CountriesData{
    private ArrayList<Integer> allCases, allDailyCases, allActive, allRecovered, allDailyRecovered, allDeaths, allDailyDeaths;
    private ArrayList<String> dates;
            
    public CompareData (String country, String slug, ArrayList<Integer> allCases, ArrayList<Integer> allDeaths,
                        ArrayList<Integer> allRecovered, ArrayList<Integer> allActive, ArrayList<String> dates){
        super(country, slug);
        this.allCases = allCases;
        this.allDailyCases = calculateDaily(allCases);
        this.allDailyRecovered = calculateDaily(allRecovered);
        this.allDailyDeaths = calculateDaily(allDeaths);
        this.allDeaths = allDeaths;
        this.allRecovered = allRecovered;
        this.allActive = allActive;
        this.dates = dates;
    }
    
    private ArrayList<Integer> calculateDaily(ArrayList<Integer> parents) {
        ArrayList<Integer> arr = new ArrayList<Integer>();
        for (int idx = 0; idx< parents.size();idx++) {
            int newCases = 0;
            if (idx == 0) {
                newCases = parents.get(idx);
            }else {
                newCases = parents.get(idx) - parents.get(idx - 1);
            }
            arr.add(newCases);
        }
        return arr;
    }
    
    public ArrayList<Integer> getAllCases () {
        return allCases;
    }
    
    public ArrayList<Integer> getAllDailyCases() {
        return allDailyCases;
    }
    
    public ArrayList<Integer> getAllDailyRecovered() {
        return allDailyRecovered;
    }
    
    public ArrayList<Integer> getAllDailyDeaths() {
        return allDailyDeaths;
    }
    
    public ArrayList<Integer> getAllDeaths () {
        return allDeaths;
    }
    
    public ArrayList<Integer> getAllRecovered() {
        return allRecovered;
    }
    
    public ArrayList<Integer> getAllActive () {
        return allActive;
    }
    
    public ArrayList<String> getAllDates () {
        return dates;
    }
}