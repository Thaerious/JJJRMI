package ca.frar.jjjrmi.targetted.handler;
import ca.frar.jjjrmi.exceptions.JJJRMIException;
import ca.frar.jjjrmi.translator.HandlerFactory;
import ca.frar.jjjrmi.translator.Translator;
import ca.frar.jjjrmi.translator.TranslatorResult;

import java.io.File;
import java.io.IOException;

public class Encode {

    public static void main(String ... args) throws IOException, ClassNotFoundException, JJJRMIException {
        HandlerFactory instance = HandlerFactory.getInstance();
        instance.addJar(new File("target/dependency/jjjrmi-ext-0.7.0.jar"));

        Both both = new Both();
        Translator translator = new Translator();
        TranslatorResult encoded = translator.encode(both);
        System.out.println(encoded.toString(2));
    }
}
