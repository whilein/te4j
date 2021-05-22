/*
 *    Copyright 2021 Whilein
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

package te4j.modifiable.watcher;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import te4j.modifiable.Modifiable;
import te4j.modifiable.ModifiableReference;

import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SimpleModifyWatcherManager implements ModifyWatcherManager {

    @NotNull WatchService watcher;

    @NotNull ReferenceQueue<@NonNull Modifiable> queue;
    @NotNull Map<@NotNull String, @NotNull ModifyWatcherDirectory> directories;

    private void runThread() {
        val thread = new ModifyWatcherThread();
        thread.setName("Modify Watcher");
        thread.setDaemon(true);
        thread.start();
    }

    private static @NotNull ModifyWatcherManager _create(final @NotNull WatchService watcher) {
        val modifyWatcherManager = new SimpleModifyWatcherManager(watcher, new ReferenceQueue<>(), new HashMap<>());
        modifyWatcherManager.runThread();

        return modifyWatcherManager;
    }

    public static @NotNull ModifyWatcherManager create(final @NonNull WatchService watcher) {
        return _create(watcher);
    }

    public static @NotNull ModifyWatcherManager create() {
        try {
            return _create(FileSystems.getDefault().newWatchService());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void cleanup() {
        Reference<? extends Modifiable> reference;

        while ((reference = queue.poll()) != null) {
            val modifiable = (ModifiableReference) reference;

            for (val file : modifiable.getFiles()) {
                remove(modifiable, Paths.get(file));
            }
        }
    }


    @Override
    public synchronized void close() throws IOException {
        watcher.close();
    }

    @Override
    public boolean handle(@NotNull final String name) {
        return handle(Paths.get(name));
    }

    private synchronized boolean handle(final @NonNull Path path) {
        cleanup();

        val abs = path.toAbsolutePath();
        val asbString = abs.toString();
        val absParent = abs.getParent();
        val absParentString = absParent.toString();

        val directory = directories.get(absParentString);
        if (directory == null) return false;

        for (val reference : directory.getEntries(asbString)) {
            val oldFiles = reference.getFiles();
            if (!reference.handleModify()) continue;

            val newFiles = reference.getFiles();

            val addedFiles = new ArrayList<>(newFiles);
            addedFiles.removeAll(oldFiles);

            val removedFiles = new ArrayList<>(oldFiles);
            removedFiles.removeAll(newFiles);

            for (val add : addedFiles) {
                add(reference, Paths.get(add));
            }

            for (val remove : removedFiles) {
                remove(reference, Paths.get(remove));
            }
        }

        return true;
    }

    @Override
    public synchronized void register(final @NonNull Modifiable modifiable) {
        val reference = new ModifiableReference(modifiable, queue);

        for (val file : modifiable.getFiles()) {
            add(reference, Paths.get(file));
        }
    }

    private void add(final @NotNull ModifiableReference modifiable, final @NotNull Path path) {
        val abs = path.toAbsolutePath();
        val absString = abs.toString();

        val absParent  = abs.getParent();
        val absParentString = absParent.toString();

        directories.computeIfAbsent(absParentString, __ -> {
            try {
                return SimpleModifyWatcherDirectory.create(absParent.register(this.watcher, ENTRY_MODIFY));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).addEntry(absString, modifiable);
    }

    private void remove(final @NotNull ModifiableReference modifiable, final @NotNull Path path) {
        val abs = path.toAbsolutePath();
        val absString = abs.toString();

        val absParent = abs.getParent();
        val absParentString = absParent.toString();

        val directory = directories.get(absParentString);
        directory.removeEntry(absString, modifiable);

        if (!directory.hasEntries()) {
            directory.close();

            directories.remove(absParentString);
        }
    }

    private final class ModifyWatcherThread extends Thread {

        @Override
        public void run() {
            try {
                WatchKey key;

                while ((key = watcher.take()) != null) {
                    val dir = (Path) key.watchable();

                    for (WatchEvent<?> event : key.pollEvents()) {
                        val path = (Path) event.context();

                        handle(dir.resolve(path).toAbsolutePath());
                    }

                    key.reset();
                }
            } catch (ClosedWatchServiceException ignored) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
