/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.demo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 *
 * @author edward
 */
public class NewClass {
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger("JJJRMI");
    
    public static void main(String... args) throws IOException, InterruptedException {
        String cDir = System.getProperty("user.dir");
        System.out.println("The current working directory is " + cDir);
        
        Runtime runtime = Runtime.getRuntime();
        Process exec = runtime.exec("node src/test/js/main.js");
        
        InputStream inputStream = exec.getInputStream();                       
        InputStreamReader isr = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(isr);

        OutputStream outputStream = exec.getOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        
        writer.write("apple\n");
        writer.write("exit\n");
        writer.flush();
        
        
        String readLine = reader.readLine();
        while(readLine != null){
            LOGGER.info(readLine);
            readLine = reader.readLine();
        }
        
        LOGGER.info("exit " + exec.exitValue());
    }
}
