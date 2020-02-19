package ca.frar.jjjrmi.jsbuilder;

import ca.frar.jjjrmi.CLI;
import ca.frar.jjjrmi.exceptions.JJJRMIException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Test basic functionality of the JSParser.
 *
 * @author edward
 */
public class JSParserTest {

    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger("JJJRMI");
    final static String OUT_DIR = "deleteme";
    final static String IN_DIR = "src/test/java/ca/frar/jjjrmi/testclasses/";

    public JSParserTest() {
        File dir = new File(OUT_DIR);

        if (dir.exists()) {
            for (File file : dir.listFiles()) {
                file.delete();
            }
            dir.delete();
        }
    }

    /**
     * File will generate because Simple extends JJJObject.
     *
     * @throws JJJRMIException
     */
    @Test
    public void test_simple() throws JJJRMIException, MojoExecutionException, MojoFailureException, JSBuilderException, IOException {
        String className = "Simple";
        CLI.main("-d", IN_DIR, "-i", className, "-o", OUT_DIR);
        assertTrue(new File(OUT_DIR + "/" + className + ".js").exists());
    }

    /**
     * 'Simple.js' will require 'Shapes' because 'Shapes' is invoked in a field.
     * @throws JJJRMIException
     * @throws MojoExecutionException
     * @throws MojoFailureException
     * @throws JSBuilderException
     * @throws IOException 
     */
    @Test
    public void test_simple_has_require() throws JJJRMIException, MojoExecutionException, MojoFailureException, JSBuilderException, IOException {
        String className = "Simple";
        CLI.main("-d", IN_DIR, "-i", className, "-o", OUT_DIR);
        String contents = new String(Files.readAllBytes(Paths.get(OUT_DIR + "/" + className + ".js")));
        String assertString = "const Shapes = require(\"./Shapes\");";
        LOGGER.debug(contents);
        assertTrue(contents.contains(assertString));
    }    
    
    /**
     * File will generate because ExtendsSimple extends Simple which in turn
     * extends JJJObject.
     *
     * @throws JJJRMIException
     */
    @Test
    public void test_son_of_a_simple() throws JJJRMIException, MojoExecutionException, MojoFailureException, JSBuilderException, IOException {
        String className = "ExtendsSimple";
        CLI.main("-d", IN_DIR, "-i", className, "-o", OUT_DIR);
        assertTrue(new File(OUT_DIR + "/" + className + ".js").exists());
    }

    @Test
    public void test_extends_has_require() throws JJJRMIException, MojoExecutionException, MojoFailureException, JSBuilderException, IOException {
        String className = "ExtendsSimple";
        CLI.main("-d", IN_DIR, "-i", className, "-o", OUT_DIR);
        String contents = new String(Files.readAllBytes(Paths.get(OUT_DIR + "/" + className + ".js")));
        String assertString = "const Simple = require(\"./Simple\");";
        assertTrue(contents.contains(assertString));
    }
}
