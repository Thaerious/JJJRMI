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
class ErrReader extends Thread {
    private BufferedReader bsr;
    private InputStreamReader isr;
    private final JS outer;
    private final InputStream stream;

    public ErrReader(InputStream stream, final JS outer) {
        this.outer = outer;
        this.stream = stream;
    }

    public void close() throws IOException {
        bsr.close();
        isr.close();
    }

    public void run() {
        try {
            isr = new InputStreamReader(stream);
            bsr = new BufferedReader(isr);

            String line = bsr.readLine();
            while (line != null) {
                System.out.println(line);
                line = bsr.readLine();
            }
        } catch (IOException ex) {
            Logger.getLogger(ErrReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
