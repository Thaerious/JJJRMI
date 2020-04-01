package ca.frar.jjjrmi.translator;

import ca.frar.jjjrmi.Global;
import static ca.frar.jjjrmi.Global.LOGGER;
import static ca.frar.jjjrmi.Global.VERBOSE;
import static ca.frar.jjjrmi.Global.VERY_VERBOSE;
import ca.frar.jjjrmi.annotations.Handles;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
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

    private HandlerFactory() {
        SubTypesScanner subTypesScanner = new SubTypesScanner(false);
        TypeAnnotationsScanner typeAnnotationsScanner = new TypeAnnotationsScanner();
        Reflections reflections = new Reflections("", subTypesScanner, typeAnnotationsScanner);
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Handles.class);

        Global.header(VERBOSE, "Handlers");
        for (Class<?> aClass : classes) {
            LOGGER.debug(aClass);
            Handles handles = aClass.getAnnotation(Handles.class);
            if (AHandler.class.isAssignableFrom(aClass)) {
                Global.line(VERBOSE, aClass.getName() + " : " + handles.value());
                classMap.put(handles.value(), (Class<? extends AHandler<?>>) aClass);
            } else {
                Global.line(VERY_VERBOSE, aClass.getName() + " does not extend AHander");
            }
        }
        Global.tail(VERBOSE);
    }

    public void addJar(File file) throws MalformedURLException {
        URL[] urls = new URL[]{file.toURI().toURL()};
        URLClassLoader urlClassLoader = new URLClassLoader(urls, this.getClass().getClassLoader());

        Reflections reflections = new Reflections(urlClassLoader);
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Handles.class);

        Global.header(VERBOSE, "Handlers: " + file.getPath());
        for (Class<?> aClass : classes) {
            Handles handles = aClass.getAnnotation(Handles.class);
            if (AHandler.class.isAssignableFrom(aClass)) {
                LOGGER.log(VERBOSE, Global.line(aClass.getName() + " : " + handles.value()));
                classMap.put(handles.value(), (Class<? extends AHandler<?>>) aClass);
            } else {
                LOGGER.log(VERBOSE, Global.line(aClass.getName() + " does not extend AHander"));
            }
        }
    }

    public void addClasspath(File file) throws MalformedURLException, ClassNotFoundException, IOException {
        Global.header(VERBOSE, "Handlers: " + file.getPath());

        URL[] urls = new URL[]{file.toURI().toURL()};
        URLClassLoader urlClassLoader = new URLClassLoader(urls, this.getClass().getClassLoader());

        List<String> classnames = Files.walk(file.toPath())
                .filter(f -> f.toString().endsWith(".class"))
                .map(Object::toString)
                .map(s -> s.substring(file.toString().length() + 1))
                .map(s -> s.substring(0, s.length() - 6))
                .map(s -> s.replace("/", "."))
                .collect(Collectors.toList());

        for (String classname : classnames) {
            Class<?> aClass = urlClassLoader.loadClass(classname);
            Handles handles = aClass.getAnnotation(Handles.class);
            if (handles == null) continue;
            if (AHandler.class.isAssignableFrom(aClass)) {
                LOGGER.log(VERBOSE, Global.line(aClass.getName() + " : " + handles.value()));
                classMap.put(handles.value(), (Class<? extends AHandler<?>>) aClass);
            } else {
                LOGGER.log(VERBOSE, Global.line(aClass.getName() + " does not extend AHander"));
            }
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
