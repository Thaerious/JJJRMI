/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.translator;

import ca.frar.jjjrmi.exceptions.JJJRMIException;
import ca.frar.jjjrmi.testclasses.None;
import static ca.frar.jjjrmi.translator.TranslatorClient.HOST;
import static ca.frar.jjjrmi.translator.TranslatorClient.PORT;
import java.io.IOException;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Ed Armstrong
 */
public class JavaToJSTest {
    
    @Test
    public void test_sanity() throws IOException, JJJRMIException{
        TranslatorClient tc = new TranslatorClient().connect(HOST, PORT);
        Translator translator = new Translator();
        TranslatorResult result = translator.encode(new None());
        tc.cmd("decode", result.toString());
    }
    
}
