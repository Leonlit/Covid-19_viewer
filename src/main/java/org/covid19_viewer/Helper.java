package org.covid19_viewer;

import java.util.ArrayList;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;

public class Helper {
    public static void changeCursorToNormal (Scene mainScene) {
        mainScene.setCursor(Cursor.DEFAULT);
    }
            
    public static void changeCursorToLoading (Scene mainScene) {
        mainScene.setCursor(Cursor.WAIT);
    }
    
    public static int getDataConstrainer (ArrayList list, int constraints) {
        if (constraints == 0) return 0;
        int seriesSize = list.size();
        int value = constraints > seriesSize ? 0 : seriesSize - constraints;
        return value;
    }
    
    public static double getBackLogValueIfSelected (int value, int max, CheckBox box) {
        if (isLogChartSelected(box) && value == 0) {
            return 0;
        }else if (isLogChartSelected(box)){
            return logOfBase(max, value);
        }
        return value;
    }
    
    public static int getBackValueFromLog (double value, int max, CheckBox box) {
        if (isLogChartSelected(box)){
            return getBackFromBase(max, value);
        }
        return (int)value;
    }
    
    public static double logOfBase(int base, double num) {
        double value = Math.log(num) / Math.log(base);
        if (Double.isInfinite(value)) {
            return 0;
        }
        return value;
    }
    
    public static int getBackFromBase(int base, double num) {
        return (int)Math.exp(num * Math.log(base));
    }
    
    public static boolean isLogChartSelected(CheckBox box) {
        return box.isSelected();
    }
}
