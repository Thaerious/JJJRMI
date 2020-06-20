package ca.frar.jjjrmi.targetted.handler;

import ca.frar.jjjrmi.exceptions.JJJRMIException;
import ca.frar.jjjrmi.translator.Translator;
import ca.frar.jjjrmi.translator.TranslatorResult;
import ca.frar.jjjrmi.translator.testclasses.HasHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HandlerTest {
    private static final Logger LOGGER = LogManager.getLogger(HandlerTest.class);

    /**
     * Has handler z = x * y, after decode z = x + y;
     * @throws JJJRMIException
     */
    @Test
    public void test_handler() throws JJJRMIException {
        Translator translator = new Translator();
        HasHandler object = new HasHandler(2, 5);
        TranslatorResult encoded = translator.encode(object);
        translator.clear();
        HasHandler decoded = (HasHandler) translator.decode(encoded.toString()).getRoot();
        assertEquals(7, decoded.z);
    }

    /**
     * Encode the 'Games' class, which has an arraylist.
     * @throws JJJRMIException
     */
    public void test_arrayList_handler() throws JJJRMIException{
        Translator translator = new Translator();
        TranslatorResult encoded = translator.encode(new Games());
        System.out.println(encoded.toString(2));
    }

}
