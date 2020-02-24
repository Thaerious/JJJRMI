/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.translator;

import ca.frar.jjjrmi.exceptions.JJJRMIException;
import ca.frar.jjjrmi.testclasses.None;
import ca.frar.jjjrmi.testclasses.Simple;
import static ca.frar.jjjrmi.translator.TranslatorClient.HOST;
import static ca.frar.jjjrmi.translator.TranslatorClient.PORT;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Ed Armstrong
 */
public class JavaToJSTest {
    
    @Test
    public void test_none() throws IOException, JJJRMIException{
        TranslatorClient tc = new TranslatorClient().connect(HOST, PORT);
        Translator translator = new Translator();
        TranslatorResult result = translator.encode(new None());
        String r = tc.cmd("decode", result.toString());
        tc.close();
        
        assertEquals("ca.frar.jjjrmi.testclasses.None", r);
    }
    
    @Test
    public void test_simple() throws IOException, JJJRMIException{
        TranslatorClient tc = new TranslatorClient().connect(HOST, PORT);
        Translator translator = new Translator();
        TranslatorResult result = translator.encode(new Simple());
        String r = tc.cmd("decode", result.toString());
        tc.close();
        
        assertEquals("ca.frar.jjjrmi.testclasses.Simple", r);
    }    
    
}
