/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.translator;
import ca.frar.jjjrmi.exceptions.DecoderException;
import ca.frar.jjjrmi.exceptions.JJJRMIException;
import ca.frar.jjjrmi.testclasses.None;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.LinkedList;

/**
 * Use to test JS decoding of Java encoded objects.
 * @author Ed Armstrong
 */
public class JSInterface implements AcceptsResult<String>{
    private final StreamEcho errReader;
    private final Process exec;
    LinkedList<String> results = new LinkedList<>();
    final Translator translator;
    BlockingStreamReader blockingStreamReader;
    private boolean doEcho = false;
    
    public static void main(String ... args) throws IOException, DecoderException, InterruptedException, JJJRMIException{
        JSInterface js = new JSInterface().echo(true);
        Translator translator = new Translator();
        
        TranslatorResult encoded = translator.encode(new None());
        js.cmd("decode", encoded.toString());
        System.out.println(js.rv());
        js.exit();       
    }


    public JSInterface echo(boolean value){
        this.doEcho = value;
        this.blockingStreamReader.echo(value);
        return this;
    }

    public JSInterface() throws IOException {
        this.exec = Runtime.getRuntime().exec("node src/test/js/RemoteTranslator.js");
        this.translator = new Translator();
        this.blockingStreamReader = new BlockingStreamReader(this.exec.getInputStream(), this);        
        this.errReader = new StreamEcho(this.exec.getErrorStream(), "[JAVA]");
        this.blockingStreamReader.start();
        this.errReader.start();
        System.out.println("ready");
    }

    public void exit() throws InterruptedException, IOException {
        OutputStream outputStream = exec.getOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        printStream.println("exit");
        printStream.flush();      
        this.errReader.close();
        this.blockingStreamReader.close();
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

    public JSInterface cmd(String cmd, String arg) throws IOException, DecoderException {
        OutputStream outputStream = exec.getOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        
        StringBuilder builder = new StringBuilder();               
        builder.append(cmd).append(" ").append(arg);

        if (this.doEcho) System.out.println("$> " + builder.toString());
        
        printStream.print(builder.toString());
        printStream.flush();
        return this;
    }

    @Override
    public void addResult(String result) {
       this.results.addLast(result);
    }
}
