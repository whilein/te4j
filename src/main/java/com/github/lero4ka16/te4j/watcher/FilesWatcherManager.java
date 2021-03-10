package com.github.lero4ka16.te4j.watcher;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FilesWatcherManager {

    private final WatchService watcher;

    private final Map<Path, FilesWatcherEntryDirectory> directories = new HashMap<>();
    private final Map<Path, Set<ModifiableHolder>> files = new HashMap<>();

    public static final FilesWatcherManager INSTANCE = new FilesWatcherManager();

    public FilesWatcherManager() {
        try {
            watcher = FileSystems.getDefault().newWatchService();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        FilesWatcherThread thread = new FilesWatcherThread(this);
        thread.start();
    }

    public WatchService getWatcher() {
        return watcher;
    }

    private void cleanup() {
        files.entrySet().removeIf(entry -> {
            entry.getValue().removeIf(ModifiableHolder::wasDeleted);

            boolean fileRemoved = entry.getValue().isEmpty();

            if (fileRemoved) {
                Path path = entry.getKey();
                Path parent = path.getParent();
                FilesWatcherEntryDirectory directory = directories.get(parent);

                if (directory != null && directory.removeFile(path) == 0) {
                    directory.remove();
                    directories.remove(parent);
                }
            }

            return fileRemoved;
        });
    }

    public synchronized void handle(Path path) {
        cleanup();

        Set<ModifiableHolder> set = files.get(path.toAbsolutePath());
        if (set == null) return;

        for (ModifiableHolder holder : set) {
            holder.handleModify();
        }
    }

    public synchronized void register(Modifiable modifiable) {
        for (Path path : modifiable.getFiles()) {
            Path abs = path.toAbsolutePath();

            files.computeIfAbsent(abs, x -> new HashSet<>())
                    .add(new ModifiableHolder(modifiable));

            directories.computeIfAbsent(abs.getParent(), parent -> {
                try {
                    return new FilesWatcherEntryDirectory(parent.register(this.watcher, StandardWatchEventKinds.ENTRY_MODIFY));
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }).addFile(abs);
        }
    }

}
