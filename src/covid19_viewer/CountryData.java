/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package covid19_viewer;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author User
 */
public class CountryData {
    private final SimpleIntegerProperty newCases, newDeaths, newRecovered, activeCases,
                                        totalCases, totalDeaths, totalRecovered;
    private final SimpleStringProperty countryName;
    
    public CountryData (String countryName, int newCases, int newDeaths, int newRecovered,
                        int activeCases, int totalCases, int totalDeaths, int totalRecovered) {
        super();
        this.countryName = new SimpleStringProperty(countryName);
        this.newCases = new SimpleIntegerProperty(newCases);
        this.newDeaths = new SimpleIntegerProperty(newDeaths);
        this.newRecovered = new SimpleIntegerProperty(newRecovered);
        this.activeCases = new SimpleIntegerProperty(activeCases);
        this.totalCases = new SimpleIntegerProperty(totalCases);
        this.totalDeaths = new SimpleIntegerProperty(totalDeaths);
        this.totalRecovered = new SimpleIntegerProperty(totalRecovered);
    }
    
    public String getCountryName () {
        return countryName.get();
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
