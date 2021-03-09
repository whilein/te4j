package com.github.lero4ka16.te4j.watcher;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.util.concurrent.atomic.AtomicInteger;

public class FilesWatcherThread extends Thread {

    private static final AtomicInteger THREAD_ID = new AtomicInteger();

    private final FilesWatcherManager service;

    public FilesWatcherThread(FilesWatcherManager service) {
        super("FileWatcher #" + THREAD_ID.incrementAndGet());

        this.service = service;

        setDaemon(true);
    }

    @Override
    public void run() {
        try {
            WatchKey key;

            while ((key = service.getWatcher().take()) != null) {
                Path dir = (Path) key.watchable();

                for (WatchEvent<?> event : key.pollEvents()) {
                    Path path = (Path) event.context();
                    service.handle(dir.resolve(path));
                }

                key.reset();
            }
        } catch (Throwable ignored) {
        }
    }
}
