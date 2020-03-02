/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.utility;

import ca.frar.jjjrmi.utility.StreamEcho;
import ca.frar.jjjrmi.exceptions.JJJRMIException;
import ca.frar.jjjrmi.testclasses.None;
import ca.frar.jjjrmi.translator.Translator;
import ca.frar.jjjrmi.translator.TranslatorResult;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author Ed Armstrong
 */
public class TranslatorClient {
    public static String HOST = "127.0.0.1";
    public static int PORT = 1337;
    
    public static void main(String... args) throws IOException, JJJRMIException {
        TranslatorClient tc = new TranslatorClient().connect(HOST, PORT);
        Translator translator = new Translator();
        TranslatorResult result = translator.encode(new None());
        tc.cmd("error", "-");
        tc.close();
    }
    
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private StreamEcho outEcho;
    private StreamEcho errEcho;
    private Process exec;
    
    public TranslatorClient connect(String host, int port) throws IOException{
        exec = Runtime.getRuntime().exec("node src/test/js/PortTranslator.js");        
        outEcho = new StreamEcho(exec.getInputStream(), "[JS] ");
        errEcho = new StreamEcho(exec.getErrorStream(), "[JS-ERROR] ");
        outEcho.start();
        errEcho.start();
        
        socket = new Socket(HOST, PORT);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        return this;
    }
    
    public void close() throws IOException{
        cmd("exit", "");
        outEcho.close();        
        errEcho.close();
        exec.destroy();
        System.out.println("java exiting");
    }
    
    public String cmd(String cmd, String arg) throws IOException{
        out.println(cmd + " " + arg);
        out.flush();
        String line = in.readLine();
        return line;
    }
    
    public String cmd(String cmd) throws IOException{
        out.println(cmd);
        out.flush();
        String line = in.readLine();
        return line;
    }    
}
