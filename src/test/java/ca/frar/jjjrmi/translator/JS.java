/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.translator;

import ca.frar.jjjrmi.exceptions.DecoderException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.LinkedList;

/**
 *
 * @author Ed Armstrong
 */
public class JS {

    private final ErrReader errReader;
    private final Process exec;
    LinkedList<Object> results = new LinkedList<Object>();
    final Translator translator;
    StdinReader reader;
    
    public static void main(String ... args) throws IOException, DecoderException, InterruptedException{
        JS js = new JS().echo(true);
        
        js.cmd("get has null");
        js.cmd("resend");
        
        System.out.println(js.rv());
        System.out.println(js.exit());        
    }

    public JS echo(boolean value){
        this.reader.echo(value);
        return this;
    }    

    public JS() throws IOException {
        this.exec = Runtime.getRuntime().exec("node src/test/js/CrossLanguageTest.js");
        this.translator = new Translator();
        this.reader = new StdinReader(this.exec.getInputStream(), this);        
        this.errReader = new ErrReader(this.exec.getErrorStream(), this);
        this.reader.start();
        this.errReader.start();
    }

    /**
     * Exit and return the result of the last cmd.
     *
     * @return
     */
    public Object exit() throws InterruptedException {
        OutputStream outputStream = exec.getOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        printStream.println("exit");
        printStream.flush();        
        return this.rv();
    }

    /**
     * Retrieve the result of the last cmd.
     *
     * @return
     */
    public Object rv() throws InterruptedException {
        synchronized(this){
            while(this.results.isEmpty()){
                this.wait();
            }
            return this.results.pop();
        }
    }

    public JS cmd(String methodName) throws IOException, DecoderException {
        OutputStream outputStream = exec.getOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        printStream.println(methodName);
        printStream.flush();
        return this;
    }
}
