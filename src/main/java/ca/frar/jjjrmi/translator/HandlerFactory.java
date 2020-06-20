package ca.frar.jjjrmi.translator;

import ca.frar.jjjrmi.Global;
import static ca.frar.jjjrmi.Global.LOGGER;
import static ca.frar.jjjrmi.Global.VERBOSE;
import static ca.frar.jjjrmi.Global.VERY_VERBOSE;
import ca.frar.jjjrmi.annotations.Handles;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

/**
 *
 * @author Ed Armstrong
 */
public class HandlerFactory {
    private static HandlerFactory instance = null;
    private final HashMap<String, Class<? extends AHandler<?>>> classMap = new HashMap<>();

    public static HandlerFactory getInstance() {
        if (instance == null) instance = new HandlerFactory();
        return instance;
    }

    public void addClass(Class<? extends AHandler> aClass){
        Handles handles = aClass.getAnnotation(Handles.class);
        if (handles == null) throw new RuntimeException("Class does not have Handles annotation");
        classMap.put(handles.value(), (Class<? extends AHandler<?>>) aClass);
        LOGGER.log(VERBOSE, Global.line( aClass.getName() + " : " + handles.value()));
    }

    public void addJar(File file) throws IOException, ClassNotFoundException {
        HandlerJarLoader jarLoader = new HandlerJarLoader(this.getClass().getClassLoader());
        List<Class<? extends AHandler>> classes = jarLoader.load(file);

        for (Class<? extends AHandler> aClass : classes){
            this.addClass(aClass);
        }
    }

    public boolean hasHandler(Class<?> aClass) {
        if (aClass == null) throw new NullPointerException();
        return this.classMap.containsKey(aClass.getName());
    }

    public boolean hasHandler(String className) {
        return this.classMap.containsKey(className);
    }

    public <T> Class<? extends AHandler<?>> getHandler(Class<T> aClass) {
        return this.classMap.get(aClass.getName());
    }

    public <T> Class<? extends AHandler<?>> getHandler(String className) {
        return this.classMap.get(className);
    }
}
