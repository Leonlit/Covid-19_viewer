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

/**
 *
 * @author User
 */
public class ShowDetailedData {
    public ShowDetailedData (String countrySlug, String url) {
        final String apiURL = "https://api.covid19api.com/country/" + countrySlug +  "?from=2020-03-01T00:00:00Z&to=2020-04-01T00:00:00Z";
        String result = FileManagement.getFromFile(countrySlug);
        
        try {
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
            }
        } catch (MalformedURLException ex) {
            //remember to change to custom error handling
            System.out.println("Access point to the API has been changed. Searching for data history in repository");
        } catch (IOException ex) {
            System.out.println("Unable to connect with the API's server");
        } finally {
            if (!(result.equals("") || result == "")) {
                setupCountryWindow(result);
            }else {
                //pop up error
            }
        }
    }
    
    public void setupCountryWindow (String result) {
        
    }
}
