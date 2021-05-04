package org.covid19_viewer;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.XMLFormatter;

public class AppLogger {
    private final static Logger LOGGER = Logger.getLogger("AppLogger"); 

    public final static void logging(String msg, int type){
        makeDirectoryIfNotExists();
        String timeStamp = new SimpleDateFormat("MM_dd_yyyy").format(Calendar.getInstance().getTime());
        try {
            LogManager.getLogManager().reset();
            LOGGER.setLevel(Level.ALL);
            String name = timeStamp + ".log";
            File directory = new File(".");
            String fileName = directory.getCanonicalPath().concat(File.separator + "logs") 
                                + File.separator + name;
            System.out.println(fileName);
            FileHandler fh = new FileHandler(fileName , true);
            LOGGER.addHandler(fh);
            XMLFormatter formatter = new XMLFormatter();  
            fh.setFormatter(formatter);
            switch (type) {
                case 1:
                    LOGGER.info(msg);
                    break;
                case 2:
                    LOGGER.warning(msg);
                    break;
                case 3:
                    LOGGER.severe(msg);
                    break;
            }
            
        } catch (IOException ex) {
            Logger.getLogger(AppLogger.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(AppLogger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void makeDirectoryIfNotExists () {
        File directory = new File(".");
        try {
            File dirName = new File(directory.getCanonicalPath().concat(File.separator + "logs"));
            if (! dirName.exists()){
                dirName.mkdir();
            }
        } catch (IOException ex) {
            AppLogger.logging(ex.getMessage(), 3);
        }
    }
}
