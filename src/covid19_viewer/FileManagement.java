/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package covid19_viewer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Scanner;

/**
 *
 * @author User
 */
public class FileManagement {
    public static void saveIntoFile(String json, String filename) {
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
            //error when storing content to file, quite impossible
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
            System.out.println("Forcefully Getting History Data");
        }catch (IOException ex) {
            System.out.println("Error when getting data from file");
        }
        return data;
    }
}
