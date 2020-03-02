/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.utility;

import ca.frar.jjjrmi.exceptions.DecoderException;
import ca.frar.jjjrmi.translator.Translator;
import ca.frar.jjjrmi.translator.TranslatorResult;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.LinkedList;

/**
 * Use with 'CrossLanguageTest.js' to test JS generated objects being decoded
 * in java.
 * @author Ed Armstrong
 */
public class JSGetter implements HasTranslator{
    private final StreamEcho errReader;
    private final Process exec;
    LinkedList<TranslatorResult> results = new LinkedList<>();
    final Translator translator;
    StdinReader reader;
    
    public static void main(String ... args) throws IOException, DecoderException, InterruptedException{
        JSGetter js = new JSGetter().echo(true);
        
        js.cmd("get has null");
        js.cmd("resend");
        
        System.out.println(js.rv());
        System.out.println(js.exit());        
    }

    public JSGetter echo(boolean value){
        this.reader.echo(value);
        return this;
    }    

    public JSGetter() throws IOException {
        this.exec = Runtime.getRuntime().exec("node src/test/js/CrossLanguageTest.js");
        this.translator = new Translator();
        this.reader = new StdinReader(this.exec.getInputStream(), this);        
        this.errReader = new StreamEcho(this.exec.getErrorStream(), "[JAVA]");
        this.reader.start();
        this.errReader.start();
    }

    /**
     * Exit and return the result of the last cmd.
     *
     * @return
     */
    public TranslatorResult exit() throws InterruptedException {
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
    public TranslatorResult rv() throws InterruptedException {
        synchronized(this){
            while(this.results.isEmpty()){
                this.wait();
            }
            return this.results.pop();
        }
    }

    public JSGetter cmd(String methodName) throws IOException, DecoderException {
        OutputStream outputStream = exec.getOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        printStream.println(methodName);
        printStream.flush();
        return this;
    }

    @Override
    public Translator getTranslator() {
        return this.translator;
    }

    @Override
    public void addResult(TranslatorResult result) {
        this.results.addLast(result);
    }
}
