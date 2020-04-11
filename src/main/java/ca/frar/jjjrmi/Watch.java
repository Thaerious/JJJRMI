package ca.frar.jjjrmi;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Collection;

import static ca.frar.jjjrmi.Global.LOGGER;
import static java.nio.file.StandardWatchEventKinds.*;

//https://docs.oracle.com/javase/tutorial/essential/io/notification.html#register

public class Watch extends CLI{
    private final Base base;

    public static void main(String... args) throws IOException, ClassNotFoundException {
        new Watch(args).start();
    }

    public Watch(String ... args) throws IOException, ClassNotFoundException {
        base = new Base();
        this.parseArgs(base, args);
        LOGGER.info(Global.header("JJJRMI CLI WATCH"));
    }

    public void start() {
        String path = base.getSourceDirectories().get(0);
        Path dir = new File(path).toPath();

        boolean valid = true;

        try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
            WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);

            while (valid) {
                key = watcher.take();

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    if (kind == ENTRY_CREATE) this.entryCreate((WatchEvent<Path>) event);
                    else if (kind == ENTRY_DELETE) this.entryDelete((WatchEvent<Path>) event);
                    else if (kind == ENTRY_MODIFY) this.entryModify((WatchEvent<Path>) event);
                }

                valid = key.reset();
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }

        System.out.println("watched directory no longer valid, exiting normally");
    }

    private void entryModify(WatchEvent<Path> event) {
        Path filepath = event.context();
        System.out.println("entryModify " + filepath);

        try {
            base.run(Arrays.asList(filepath));
            base.output();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void entryDelete(WatchEvent<Path> event) {
        Path filename = event.context();
        System.out.println("entryDelete " + filename);
    }

    private void entryCreate(WatchEvent<Path> event) {
        Path filename = event.context();
        System.out.println("entryCreate " + filename);
    }
}
