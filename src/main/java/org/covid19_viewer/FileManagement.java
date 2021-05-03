package org.covid19_viewer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileManagement {
    
    public FileManagement () {
        makeDirectoryIfNotExists();
    }
    
    public static void saveIntoFile(String json, String filename) {
        makeDirectoryIfNotExists();
        try {
            File directory = new File(".");
            String fileName = directory.getCanonicalPath().concat(File.separator + "historyData") 
                                + File.separator + filename +".txt";
                                
            RandomAccessFile stream = new RandomAccessFile(fileName, "rw");
            FileChannel channel = stream.getChannel();
            byte[] strBytes = json.getBytes();
            ByteBuffer buffer = ByteBuffer.allocate(strBytes.length);
            buffer.put(strBytes);
            buffer.flip();
            channel.write(buffer);
            stream.close();
            channel.close();
            System.out.println("Saving data into files in the historyData directory");
        }catch (IOException ex) {
            AppLogger.logging(ex.getMessage(), 3);
        }
    }
    
    public static String getFromFile (String type) {
        String data = "";
        try {
            File directory = new File(".");
            String fileName = directory.getCanonicalPath().concat(File.separator + "historyData") 
                                + File.separator + type +".txt";
            long time = new File (fileName).lastModified();
            
            if (System.currentTimeMillis() - time < 900000) {
                Scanner input = new Scanner(new File(fileName));
                data = input.nextLine();
                System.out.println("Try getting History Data");
            }
        }catch (IOException ex) {
            AppLogger.logging(ex.getMessage(), 3);
            System.out.println("Error when getting data from file");
        }
        return data;
    }
    
    public static String getFromFile (String type, int forced) {
        String data = "";
        try {
            File directory = new File(".");
            String fileName = directory.getCanonicalPath().concat(File.separator + "historyData") 
                                + File.separator + type +".txt";
            Scanner input = new Scanner(new File(fileName));
            data = input.nextLine();
            input.close();
            System.out.println("Forcefully Getting History Data");
        }catch (IOException ex) {
            AppLogger.logging(ex.getMessage(), 3);
            System.out.println("Error when getting data from file");
            System.out.println("The history file does not exist");
        }
        return data;
    }
    
    private static void makeDirectoryIfNotExists () {
        File directory = new File(".");
        try {
            File dirName = new File(directory.getCanonicalPath().concat(File.separator + "historyData"));
            if (! dirName.exists()){
                dirName.mkdir();
            }
        } catch (IOException ex) {
            AppLogger.logging(ex.getMessage(), 3);
        }
    }
}
