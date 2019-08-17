
import ca.frar.jjjrmi.Main;
import ca.frar.jjjrmi.RuntimeOptions;
import ca.frar.jjjrmi.annotations.Transient;
import ca.frar.jjjrmi.jsbuilder.JSParser;
import java.util.ArrayList;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Ed Armstrong
 */
public class TestField {
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger(TestField.class);
    
    public static void main(String ... args){
        RuntimeOptions runtimeOptions = new RuntimeOptions();
        runtimeOptions.setInputDirectory("src/test/resources/test.java");
        Main main = new Main(runtimeOptions);
        main.run();
        
        JSParser parser = main.getParser();
        ArrayList<CtClass<?>> sourceClasses = parser.getSourceClasses();
        CtClass<?> ctClass = sourceClasses.get(0);

        CtField<?> fieldA = ctClass.getField("a");
        CtField<?> fieldB = ctClass.getField("b");
        
        LOGGER.debug(fieldA.hasAnnotation(Transient.class));
        LOGGER.debug(fieldB.hasAnnotation(Transient.class));

    }
    
}
