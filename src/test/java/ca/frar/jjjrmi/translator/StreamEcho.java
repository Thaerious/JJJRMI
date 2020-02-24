/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.translator;

import ca.frar.jjjrmi.exceptions.DecoderException;
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
class StreamEcho extends Thread {
    private BufferedReader bsr;
    private final InputStream stream;
    private final String prequel;

    public StreamEcho(InputStream stream, String prequel) {
        this.stream = stream;
        this.prequel = prequel;
    }

    public void close() throws IOException {
        bsr.close();
    }

    public void run() {
        try {
            System.out.println("[JAVA] stream echo running");
            InputStreamReader isr = new InputStreamReader(stream);
            bsr = new BufferedReader(isr);

            String line = bsr.readLine();
            while (line != null) {
                System.out.println(prequel + line);
                line = bsr.readLine();
            }
        } catch (IOException ex) {
            Logger.getLogger(StreamEcho.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("[JAVA] stream echo exiting");
    }
}
