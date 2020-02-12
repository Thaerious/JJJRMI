/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.translator;

import ca.frar.jjjrmi.exceptions.DecoderException;
import ca.frar.jjjrmi.exceptions.EncoderException;
import ca.frar.jjjrmi.testclasses.HasNone;
import ca.frar.jjjrmi.testclasses.None;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Ed Armstrong
 */
public class CrossLanguageTest {

    public Object get(Translator translator, String methodName) throws IOException, DecoderException {
        Process exec = Runtime.getRuntime().exec("node src/test/js/TranslatorCorrectnessTest.js");

        OutputStream outputStream = exec.getOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        printStream.println(methodName);
        printStream.flush();

        InputStream errorStream = exec.getErrorStream();
        InputStreamReader esr = new InputStreamReader(errorStream);
        BufferedReader besr = new BufferedReader(esr);

        String errline = besr.readLine();
        while (errline != null) {
            System.out.println(errline);
            errline = besr.readLine();
        }        
        
        InputStream inputStream = exec.getInputStream();
        InputStreamReader isr = new InputStreamReader(inputStream);
        BufferedReader br = new BufferedReader(isr);

        StringBuilder builder = new StringBuilder();
        String line = br.readLine();
        while (line != null) {
            builder.append(line).append("\n");
            line = br.readLine();
        }

        String encoded = builder.toString();
        return translator.decode(encoded);
    }

    @Test
    public void test_get_none() throws EncoderException, DecoderException, IOException {
        Translator translator = new Translator();
        Object decoded = this.get(translator, "get_none");
        assertEquals(None.class, decoded.getClass());
    }
    
    @Test
    public void test_get_has_none() throws EncoderException, DecoderException, IOException {
        Translator translator = new Translator();
        Object decoded = this.get(translator, "get_has_none");
        assertEquals(HasNone.class, decoded.getClass());
    }    
}
