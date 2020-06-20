package ca.frar.jjjrmi.targetted.handler;
import ca.frar.jjjrmi.CLI;
import ca.frar.jjjrmi.translator.HandlerFactory;

import java.io.File;
import java.io.IOException;
import ca.frar.jjjrmi.ext.*;

public class Main {

    public static void main(String ... args) throws IOException, ClassNotFoundException {
        HandlerFactory instance = HandlerFactory.getInstance();
        instance.addJar(new File("target/dependency/jjjrmi-ext-0.7.0.jar"));
    }
}
