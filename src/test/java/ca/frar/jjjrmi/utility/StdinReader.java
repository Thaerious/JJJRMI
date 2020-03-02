/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.utility;

import ca.frar.jjjrmi.utility.JSGetter;
import ca.frar.jjjrmi.exceptions.DecoderException;
import ca.frar.jjjrmi.translator.TranslatorResult;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ed Armstrong
 */
class StdinReader extends Thread {    
    private BufferedReader bsr;
    private boolean doEcho = false;
    private InputStreamReader isr;
    private final HasTranslator outer;
    private final InputStream stream;

    public StdinReader(InputStream stream, final HasTranslator outer) {
        this.outer = outer;
        this.stream = stream;
    }

    public StdinReader echo(boolean value){
        this.doEcho = value;
        return this;
    }
    
    public void close() throws IOException {
        bsr.close();
        isr.close();
    }

    public void run() {
        isr = new InputStreamReader(stream);
        bsr = new BufferedReader(isr);
        StringBuilder builder = new StringBuilder();
        try {
            String line = bsr.readLine();
            while (line != null) {
                if (line.charAt(0) == 3) {
                    String string = builder.toString();
                    if (doEcho) System.out.println(string);
                    TranslatorResult decoded = outer.getTranslator().decode(string);
                    
                    synchronized (outer) {
                        outer.addResult(decoded);
                        outer.notify();
                    }
                    
                    builder = new StringBuilder();
                    line = bsr.readLine();
                } else {
                    builder.append(line).append("\n");
                    line = bsr.readLine();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(JSGetter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DecoderException ex) {
            Logger.getLogger(JSGetter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
