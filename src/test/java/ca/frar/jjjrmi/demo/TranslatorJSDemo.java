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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author edward
 */
public class TranslatorJSDemo {

    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger(TranslatorJSDemo.class);

    public static void main(String... args) throws IOException, InterruptedException {
        String cDir = System.getProperty("user.dir");
        System.out.println("The current working directory is " + cDir);

        Runtime runtime = Runtime.getRuntime();
        Process exec = runtime.exec("node src/test/js/TranslatorMain.js");

        setupReader("stdout: ", exec.getInputStream());
        setupReader("error:  ", exec.getErrorStream());

        OutputStream outputStream = exec.getOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(outputStream);

        writer.write("apple\n");
        writer.write("exit\n");
        writer.flush();

//        LOGGER.info("exit " + exec.exitValue());
    }

    public static void setupReader(String prefix, InputStream inputStream)  {
        InputStreamReader isr = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(isr);

        Runnable runnable = () -> {
            try {
                String readLine = reader.readLine();
                while (readLine != null) {
                    System.out.println(prefix + readLine);
                    readLine = reader.readLine();
                }
            } catch (IOException ex) {
                Logger.getLogger(TranslatorJSDemo.class.getName()).log(Level.SEVERE, null, ex);
            }
        };
        
        new Thread(runnable).start();
    }
}
