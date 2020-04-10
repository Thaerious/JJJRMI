package ca.frar.jjjrmi.translator;
import ca.frar.jjjrmi.exceptions.JJJRMIException;
import ca.frar.jjjrmi.translator.testclasses.*;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.json.JSONObject;

/**
 * Generate JSON encoding from the Java classes, placing the result in a file.
 * This get's called by the JS test
 * @author Ed Armstrong
 */
public class GenerateJSON {

    public static void main(String... args) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        new GenerateJSON().run(args[0]);
    }

    JSONObject json = new JSONObject();

    private void run(String filename) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        this.doGenerate();
        this.write(filename);
    }

    private void doGenerate() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method[] declaredMethods = this.getClass().getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (method.getName().startsWith("generate_")) {
                String key = method.getName().substring(9);
                TranslatorResult value = (TranslatorResult) method.invoke(this);
                if (value != null) this.json.put(key, value.toJSON());
            }
        }
    }

    private void write(String filename) throws IOException {
        File file = new File(filename);
        if (file.exists()) file.delete();
        file.getParentFile().mkdirs();
        file.createNewFile();

        FileOutputStream fos = new FileOutputStream(file);
        BufferedOutputStream bos = new BufferedOutputStream(fos);

        String jsonString = this.json.toString(2);
        bos.write(jsonString.getBytes());

        bos.close();
        fos.close();
    }

    public TranslatorResult generate_circular() throws JJJRMIException {
        Translator translator = new Translator();
        return translator.encode(new CircularRef());
    }

    public TranslatorResult generate_referenceAsRoot() throws JJJRMIException {
        Translator translator = new Translator();
        Has<Object> has = new Has<>(null);

        this.json.put("hasRoot", translator.encode(has).toJSON());
        this.json.put("hasRef", translator.encode(has).toJSON());
        this.json.put("hasField", translator.encode(new Has<>(has)).toJSON());

        return null;
    }

    public TranslatorResult generate_handled() throws JJJRMIException {
        Translator translator = new Translator();
        HasHandler hasHandler = new HasHandler(2, 7);
        return translator.encode(hasHandler);
    }

    public TranslatorResult generate_nonEmptyArray() throws JJJRMIException {
        Translator translator = new Translator();
        int[] array = new int[]{1, 3, 7};
        return translator.encode(new Has<>(array));
    }

    public TranslatorResult generate_emptyArray() throws JJJRMIException {
        Translator translator = new Translator();
        int[] array = new int[0];
        return translator.encode(new Has<>(array));
    }

    public TranslatorResult generate_hasNull() throws JJJRMIException {
        Translator translator = new Translator();
        return translator.encode(new Has<>(null));
    }

    public TranslatorResult generate_primitivesExtended() throws JJJRMIException {
        Translator translator = new Translator();
        return translator.encode(new PrimitivesExtended(16));
    }

    public TranslatorResult generate_simple() throws JJJRMIException {
        Translator translator = new Translator();
        return translator.encode(new Simple());
    }

    public TranslatorResult generate_none() throws JJJRMIException {
        Translator translator = new Translator();
        return translator.encode(new None());
    }

    public TranslatorResult generate_primitives() throws JJJRMIException {
        Translator translator = new Translator();
        return translator.encode(new Primitives(9));
    }

    public TranslatorResult generate_arrayWrapper() throws JJJRMIException {
        Translator translator = new Translator();
        return translator.encode(new Primitives(9));
    }

    public TranslatorResult generate_doNotRetainExtends() throws JJJRMIException {
        Translator translator = new Translator();
        return translator.encode(new DoNotRetainExtends(5));
    }

    public TranslatorResult generate_doNotRetainAnno() throws JJJRMIException {
        Translator translator = new Translator();
        return translator.encode(new DoNotRetainAnno(5));
    }

    public TranslatorResult generate_transientField() throws JJJRMIException {
        Translator translator = new Translator();
        return translator.encode(new TransientField().set(9));
    }

    public TranslatorResult generate_hasAfterDecode() throws JJJRMIException {
        Translator translator = new Translator();
        return translator.encode(new HasAfterDecode());
    }
}
