package ca.frar.jjjrmi.jsbuilder;
import ca.frar.jjjrmi.CLI;
import ca.frar.jjjrmi.exceptions.JJJRMIException;
import ca.frar.jjjrmi.jsbuilder.testclasses.*;
import ca.frar.jjjrmi.translator.testclasses.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Test basic functionality of the JSParser.
 *
 * @author edward
 */
public class JSParserTest {
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger("JJJRMI");
    final static String OUT_DIR = "deleteme";
    final static String IN_DIR = "src/test/java/ca/frar/jjjrmi/translator/testclasses/ "
                               + "src/test/java/ca/frar/jjjrmi/jsbuilder/testclasses/";

    public static void main(String ... args){
        try {
            new JSParserTest().test_simple();
        } catch (JJJRMIException | JSBuilderException | IOException ex) {
            Throwable current = ex;
            while(current != null){
                LOGGER.error(current);
                current = current.getCause();
            }
        }
    }
    
    public JSParserTest() {
        try {
            File dir = new File(OUT_DIR);
            
            if (dir.exists()) {
                for (File file : dir.listFiles()) {
                    file.delete();
                }
                dir.delete();
            }

            CLI.main("-s", "-d", IN_DIR, "-o", OUT_DIR);
        } catch (JSBuilderException ex) {
            Logger.getLogger(JSParserTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * File will generate because Simple extends JJJObject.
     *
     */
    @Test
    public void test_simple() throws JJJRMIException, JSBuilderException, IOException {
        String className = Simple.class.getSimpleName();
        assertTrue(new File(OUT_DIR + "/" + className + ".js").exists());
    }

    /**
     * 'Simple.js' will require 'Shapes' because 'Shapes' is invoked in a field.
     *
     */
    @Test
    public void test_simple_has_require() throws JJJRMIException, JSBuilderException, IOException {
        String className = Simple.class.getSimpleName();
        String contents = new String(Files.readAllBytes(Paths.get(OUT_DIR + "/" + className + ".js")));
        String assertString = "const Shapes = require(\"./Shapes\");";
        LOGGER.debug(contents);
        assertTrue(contents.contains(assertString));
    }

    /**
     * File will generate because ExtendsSimple extends Simple which in turn
     * extends JJJObject.
     *
     */
    @Test
    public void test_son_of_a_simple() throws JJJRMIException, JSBuilderException, IOException {
        String className = ExtendsSimple.class.getSimpleName();
        assertTrue(new File(OUT_DIR + "/" + className + ".js").exists());
    }

    @Test
    public void test_extends_jjjobject_not_has_require() throws JJJRMIException, JSBuilderException, IOException {
        String className = None.class.getSimpleName();
        String contents = new String(Files.readAllBytes(Paths.get(OUT_DIR + "/" + className + ".js")));
        String assertString = "require";
        assertTrue(!contents.contains(assertString));
    }

    @Test
    public void test_extends_jjjobject_not_has_super() throws JJJRMIException, JSBuilderException, IOException {
        String className = None.class.getSimpleName();
        String contents = new String(Files.readAllBytes(Paths.get(OUT_DIR + "/" + className + ".js")));
        String assertString = "super();";
        assertTrue(!contents.contains(assertString));
    }

    @Test
    public void test_deep_extends_jjjobject_has_require() throws JJJRMIException, JSBuilderException, IOException {
        String className = ExtendsNone.class.getSimpleName();
        String contents = new String(Files.readAllBytes(Paths.get(OUT_DIR + "/" + className + ".js")));
        String assertString = "const None = require(\"./None\");";
        assertTrue(contents.contains(assertString));
    }

    @Test
    public void test_deep_extends_jjjobject_has_super() throws JJJRMIException, JSBuilderException, IOException {
        String className = ExtendsNone.class.getSimpleName();
        String contents = new String(Files.readAllBytes(Paths.get(OUT_DIR + "/" + className + ".js")));
        String assertString = "super();";
        assertTrue(contents.contains(assertString));
    }

    @Test
    public void test_extends_jjj_anno_not_has_require() throws JJJRMIException, JSBuilderException, IOException {
        String className = NoneAnnotated.class.getSimpleName();
        String contents = new String(Files.readAllBytes(Paths.get(OUT_DIR + "/" + className + ".js")));
        String assertString = "require";
        assertTrue(!contents.contains(assertString));
    }

    @Test
    public void test_deep_extends_jjj_anno_has_require() throws JJJRMIException, JSBuilderException, IOException {
        String className = ExtendsNoneAnnotated.class.getSimpleName();
        String contents = new String(Files.readAllBytes(Paths.get(OUT_DIR + "/" + className + ".js")));
        String assertString = "const NoneAnnotated = require(\"./NoneAnnotated\");";
        LOGGER.debug(contents);
        assertTrue(contents.contains(assertString));
    }

    @Test
    public void test_extends_jjj_anno_not_has_super() throws JJJRMIException, JSBuilderException, IOException {
        String className = NoneAnnotated.class.getSimpleName();
        String contents = new String(Files.readAllBytes(Paths.get(OUT_DIR + "/" + className + ".js")));
        String assertString = "super();";
        assertTrue(!contents.contains(assertString));
    }

    @Test
    public void test_deep_extends_jjj_anno_has_super() throws JJJRMIException, JSBuilderException, IOException {
        String className = ExtendsNoneAnnotated.class.getSimpleName();
        String contents = new String(Files.readAllBytes(Paths.get(OUT_DIR + "/" + className + ".js")));
        String assertString = "super();";
        assertTrue(contents.contains(assertString));
    }

    /**
     * Java classes directly extending the AHandler class will produce JS
     * classes which extend the jjjrmi/AHandler class.
     *
     */
    @Test
    public void test_is_handler() throws JJJRMIException, JSBuilderException, IOException {
        String className = IsHandler.class.getSimpleName();
        String contents = new String(Files.readAllBytes(Paths.get(OUT_DIR + "/" + className + ".js")));
        assertTrue(contents.contains("extends AHandler"));
        assertTrue(contents.contains("const AHandler = require(\"jjjrmi\").AHandler;"));
    }

    /**
     * Classes annotated with @JJJ(retain=false) will set the __isRetained to
     * false.
     */
    @Test
    public void test_do_not_retain() throws JJJRMIException, JSBuilderException, IOException {
        String className = NoRetain.class.getSimpleName();
        String contents = new String(Files.readAllBytes(Paths.get(OUT_DIR + "/" + className + ".js")));
        contents = contents.replaceAll("[ \t\r\n]+", " ");
        assertTrue(contents.contains("__isRetained() { return false; }"));
    }

    /**
     * Classes annotated without @JJJ(retain=false) will set the __isRetained to
     * true.
     */
    @Test
    public void test_retain() throws JJJRMIException, JSBuilderException, IOException {
        String className = None.class.getSimpleName();
        String contents = new String(Files.readAllBytes(Paths.get(OUT_DIR + "/" + className + ".js")));
        contents = contents.replaceAll("[ \t\r\n]+", " ");
        assertTrue(contents.contains("__isRetained() { return true; }"));
    }
    
    /**
     * The encoded class has an array, the size of which is set by a variable.
     */
    @Test
    public void test_variable_size_array() throws JJJRMIException, JSBuilderException, IOException {
        String className = VariableSizeArray.class.getSimpleName();
        String contents = new String(Files.readAllBytes(Paths.get(OUT_DIR + "/" + className + ".js")));
        contents = contents.replaceAll("[ \t\r\n]+", " ");
        assertTrue(contents.contains("new Array(size);"));
    }    
    
    /**
     * JS Switch statements match their Java counterpart.
     */
    @Test
    public void test_switch() throws JJJRMIException, JSBuilderException, IOException {
        String className = Switched.class.getSimpleName();
        String contents = new String(Files.readAllBytes(Paths.get(OUT_DIR + "/" + className + ".js")));
        assertTrue(contents.contains("case \"-0.5 -1.0 0.0\": return Cardinality.S;"));
    }    
    
    /**
     * Static assignments using same class name get the class name.
     */
    @Test
    public void test_static_self() throws JJJRMIException, JSBuilderException, IOException {
        String className = Literals.class.getSimpleName();
        String contents = new String(Files.readAllBytes(Paths.get(OUT_DIR + "/" + className + ".js")));
        assertTrue(contents.contains("Literals.b = 6;"));
    }     
    
    /**
     * Static assignments using the this operator get the class name. (Not 'this').
     */
    @Test
    public void test_static_self_this() throws JJJRMIException, JSBuilderException, IOException {
        String className = Literals.class.getSimpleName();
        String contents = new String(Files.readAllBytes(Paths.get(OUT_DIR + "/" + className + ".js")));
        assertTrue(contents.contains("Literals.a = 5;"));
    }       
    
    /**
     * Static assignments using other class name get the other class name.
     */
    @Test
    public void test_static_other() throws JJJRMIException, JSBuilderException, IOException {
        String className = Literals.class.getSimpleName();
        String contents = new String(Files.readAllBytes(Paths.get(OUT_DIR + "/" + className + ".js")));
        assertTrue(contents.contains("Switched.value = \"x\";"));
    } 
    
    /**
     * Long literals appended with 'L' in Java are not in JS
     * Switched.serialVersionUID = 1L;
     */
    @Test
    public void test_literal_long() throws JJJRMIException, JSBuilderException, IOException {
        String className = Literals.class.getSimpleName();
        String contents = new String(Files.readAllBytes(Paths.get(OUT_DIR + "/" + className + ".js")));
        assertTrue(contents.contains("Literals.serialVersionUID = 1;"));
        assertTrue(contents.contains("this.z = 5;"));
    } 
    
    /**
     * An overridden method will produce the new method.
     */
    @Test
    public void test_extends_method_override() throws JJJRMIException, JSBuilderException, IOException {
        String className = ExtendSwitched.class.getSimpleName();
        String contents = new String(Files.readAllBytes(Paths.get(OUT_DIR + "/" + className + ".js")));
        contents = contents.replaceAll("[ \t\r\n]+", " ");
        assertTrue(contents.contains("cardinality(target) { return Cardinality.N; }"));
    }     
    
    /**
     * An array with elements becomes an array literal.
     */
    @Test
    public void test_array_literal() throws JJJRMIException, JSBuilderException, IOException {
        String className = ExtendSwitched.class.getSimpleName();
        String contents = new String(Files.readAllBytes(Paths.get(OUT_DIR + "/" + className + ".js")));
        contents = contents.replaceAll("[ \t\r\n]+", " ");
        System.out.println(contents);
        assertTrue(contents.contains("domSubscribers() { return [\"a\", \"b\"]; }"));
    }     
}
