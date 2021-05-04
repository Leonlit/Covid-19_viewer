package org.covid19_viewer;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.XMLFormatter;

public class AppLogger {
    private final static Logger LOGGER = Logger.getLogger("AppLogger"); 

    public final static void logging(String msg, int type){
        try {
            LogManager.getLogManager().reset();
            LOGGER.setLevel(Level.ALL);
            FileHandler fh = new FileHandler("AppLogger.log");
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
}
