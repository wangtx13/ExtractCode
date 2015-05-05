/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package extractcode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author apple
 */
public class ExtractCode {

    
    public static void extractCode(File inputFile, File outputFile) {
        
        boolean commentsStart = false;
        StringBuffer outputStr = new StringBuffer();
        StringBuffer lastOutputStr = new StringBuffer();
        
        if(!inputFile.exists()) {
            System.out.println("File doesn't exist!");
        } else if(inputFile.isDirectory()) {
            System.out.println(inputFile.getName() + " is a directory. Please input a file.");
        } else {
            try {
                //get input file name
                String inputFilePath = inputFile.getPath();
                
                try(
                    InputStream in = new FileInputStream(inputFilePath);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))
                ){
                    //read lines from the file
                    String lineString = null;
                    while((lineString = reader.readLine()) != null) {
                        int strLength = lineString.length();
                        char currentChar = '$';
                        char lastChar = '$';
                        char lastLastChar = '$';
                        for(int i = 0; i < strLength; ++i) {
                            currentChar = lineString.charAt(i);
                            
                            if(currentChar == '*' && lastChar == '/' && !commentsStart) {
                                outputStr.deleteCharAt(outputStr.length() - 1);
                                commentsStart = true;
                            } else if(currentChar == '/' && lastChar == '*' && lastLastChar != '/' && commentsStart) {
                                commentsStart = false;
                            } else if(currentChar == '/' && lastChar == '/' && lastLastChar != '*' && !commentsStart) {
                                outputStr.deleteCharAt(outputStr.length() - 1);
                                i = strLength;
                            } else if(!commentsStart) {
                                outputStr.append(currentChar);
                            }
                             
                            lastLastChar = lastChar;
                            lastChar = currentChar;
                        }
                        
                        //Whether the line includes comments
                        if(!outputStr.toString().equals(lastOutputStr.toString())) {
                            outputStr.append("\r\n");
                        }
                        lastOutputStr.replace(0, outputStr.length(), outputStr.toString());
                    }
                    
                    outputStr = ParseWords.parseAllWords(outputStr);
                    writer.write(outputStr.toString());
                    writer.flush();
                } 
                
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ExtractCode.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ExtractCode.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
}
