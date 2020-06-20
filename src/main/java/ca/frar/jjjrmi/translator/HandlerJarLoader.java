package ca.frar.jjjrmi.translator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class HandlerJarLoader extends URLClassLoader {
    private static final Logger LOGGER = LogManager.getLogger(HandlerJarLoader.class);

    public HandlerJarLoader(ClassLoader parent) throws MalformedURLException {
        super(new URL[]{}, parent);
    }

    public boolean hasClass(String name) {
        return this.findLoadedClass(name) != null;
    }

    public List<Class<? extends AHandler>> load(File file) throws IOException, ClassNotFoundException {
        if (!file.exists()) throw new FileNotFoundException("Handler .jar not found: " + file.toString());

        super.addURL(file.toURI().toURL());
        JarFile jar = new JarFile(file);
        Enumeration<JarEntry> entries = jar.entries();

        ArrayList<Class<? extends AHandler>> list = new ArrayList<>();
        while (entries.hasMoreElements()) {
            ZipEntry zipEntry = entries.nextElement();
            String path = zipEntry.getName();
            if (!path.endsWith(".class")) continue;

            String className = path.replace(".class", "").replace("/", ".");
            Class<?> aClass = this.loadClass(className);
            if (AHandler.class.isAssignableFrom(aClass)){
                list.add((Class<? extends AHandler>) aClass);
            }
        }

        return list;
    }
}
