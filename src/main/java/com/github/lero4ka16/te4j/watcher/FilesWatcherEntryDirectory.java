package com.github.lero4ka16.te4j.watcher;

import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.util.HashSet;
import java.util.Set;

public class FilesWatcherEntryDirectory {

    private final WatchKey key;
    private final Set<Path> files = new HashSet<>();

    public FilesWatcherEntryDirectory(WatchKey key) {
        this.key = key;
    }

    public synchronized int removeFile(Path path) {
        files.remove(path);
        return files.size();
    }

    public void remove() {
        key.reset();
    }

    public synchronized void addFile(Path path) {
        files.add(path);
    }
}
