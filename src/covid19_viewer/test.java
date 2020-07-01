/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package covid19_viewer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Scanner;

/**
 *
 * @author User
 */
public class test {
    public static void main (String[] args) throws FileNotFoundException, IOException{
        File directory = new File(".");
        System.out.println(directory.getCanonicalPath().concat(File.separator + "historyData") + File.separator + "global.txt");
        String fileName = directory.getCanonicalPath().concat(File.separator + "historyData") + File.separator + "global.txt";
        
        Scanner input = new Scanner(new File(fileName));
        
        long time = new File (fileName).lastModified();
        System.out.println(System.currentTimeMillis() - time);
        
    }
}
