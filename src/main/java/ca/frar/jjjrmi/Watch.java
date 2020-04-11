package ca.frar.jjjrmi;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;

import static ca.frar.jjjrmi.Global.LOGGER;
import static java.nio.file.StandardWatchEventKinds.*;

//https://docs.oracle.com/javase/tutorial/essential/io/notification.html#register

public class Watch extends CLI {
    private final Base base;
    private Path watchedDir;

    public static void main(String... args) throws IOException, ClassNotFoundException {
        new Watch(args).start();
    }

    public Watch(String... args) throws IOException, ClassNotFoundException {
        base = new Base();
        this.parseArgs(base, args);
        LOGGER.info(Global.header("JJJRMI CLI WATCH"));
    }

    public void start() {
        boolean valid = true;

        try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
            for (Path p : base.getSourceDirectories()){
                LOGGER.info(Global.header("watching: " + p.toAbsolutePath()));
                this.registerTree(watcher, p);
            }

            while (valid) {
                LOGGER.info(Global.header("waiting for change"));
                WatchKey key = watcher.take();

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

    private void registerTree(WatchService watcher, Path path) throws IOException {
        Files.walk(path)
            .filter(p -> p.toFile().isDirectory())
            .forEach(p -> {
                try {
                    p.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
    }

    private void entryModify(WatchEvent<Path> event) {
        Path filepath = event.context();
        System.out.println(filepath);

//        String pathString = this.watchedDir.toString() + "/" + filepath.getFileName().toString();
//        if (!pathString.endsWith(".java")) return;
//        Path fullPath = new File(pathString).toPath();
//
//        try {
//            base.run(Arrays.asList(fullPath));
//            base.output();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
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
