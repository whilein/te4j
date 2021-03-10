/*
 *    Copyright 2021 Lero4ka16
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.github.lero4ka16.te4j.modifiable.watcher;

import com.github.lero4ka16.te4j.modifiable.Modifiable;
import com.github.lero4ka16.te4j.modifiable.ModifiableReference;

import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ModifyWatcherManager {

    private final WatchService watcher;

    private final ReferenceQueue<Modifiable> queue = new ReferenceQueue<>();

    private final Map<Path, ModifyWatcherDirectory> directories = new HashMap<>();
    private final Map<Path, Set<ModifiableReference>> files = new HashMap<>();

    public static final ModifyWatcherManager INSTANCE = new ModifyWatcherManager();

    public ModifyWatcherManager() {
        try {
            watcher = FileSystems.getDefault().newWatchService();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        ModifyWatcherThread thread = new ModifyWatcherThread(this);
        thread.start();
    }

    public WatchService getWatcher() {
        return watcher;
    }

    private void cleanup() {
        Reference<? extends Modifiable> reference;

        while ((reference = queue.poll()) != null) {
            ModifiableReference modifiable = (ModifiableReference) reference;

            for (Path path : modifiable.getFiles()) {
                Set<ModifiableReference> references = files.get(path);
                references.remove(modifiable);

                if (references.isEmpty()) {
                    files.remove(path);

                    Path parent = path.getParent();
                    ModifyWatcherDirectory directory = directories.get(parent);

                    if (directory != null) {
                        directory.removeFile(path);

                        if (!directory.hasFiles()) {
                            directory.remove();

                            directories.remove(parent);
                        }
                    }
                }
            }
        }
    }

    public synchronized void handle(Path path) {
        cleanup();

        Set<ModifiableReference> set = files.get(path.toAbsolutePath());
        if (set == null) return;

        for (ModifiableReference holder : set) {
            holder.handleModify();
        }
    }

    public synchronized void register(Modifiable modifiable) {
        for (Path path : modifiable.getFiles()) {
            Path abs = path.toAbsolutePath();

            files.computeIfAbsent(abs, x -> new HashSet<>())
                    .add(new ModifiableReference(modifiable, queue));

            directories.computeIfAbsent(abs.getParent(), parent -> {
                try {
                    return new ModifyWatcherDirectory(parent.register(this.watcher, StandardWatchEventKinds.ENTRY_MODIFY));
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }).addFile(abs);
        }
    }

}
