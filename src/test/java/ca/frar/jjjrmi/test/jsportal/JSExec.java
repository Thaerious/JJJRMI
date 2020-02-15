/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.test.jsportal;

import ca.frar.jjjrmi.demo.TranslatorJSDemo;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Facilitates calls to the JS Translator class.
 *
 * @author Ed Armstrong
 */
public class JSExec {
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger("JJJRMI");
    protected OutputStreamWriter writer;
    private Process exec;

    public void start(String scriptPath) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        exec = runtime.exec("node " + scriptPath);
        OutputStream outputStream = exec.getOutputStream();
        writer = new OutputStreamWriter(outputStream);
    }

    public void writeLine(String line) throws IOException{
        writer.write(line + "\n");
    }
    
    public String stop() throws IOException {
        writer.flush();
        
        StringBuilder builder = new StringBuilder();
        InputStreamReader isr = new InputStreamReader(exec.getInputStream());
        BufferedReader reader = new BufferedReader(isr);
        
        String readLine = reader.readLine();
        while (readLine != null) {
            builder.append(readLine).append("\n");
            readLine = reader.readLine();
        }

        exec.destroy();
        return builder.toString();
    }

    private void setupReader(String prefix, InputStream inputStream) {
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
                LOGGER.catching(ex);
            }
        };

        new Thread(runnable).start();
    }
}
