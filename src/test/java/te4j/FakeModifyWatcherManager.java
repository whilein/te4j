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

package te4j;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import te4j.modifiable.Modifiable;
import te4j.modifiable.ModifiableReference;
import te4j.modifiable.watcher.ModifyWatcherManager;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.*;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class FakeModifyWatcherManager implements ModifyWatcherManager {

    @NotNull ReferenceQueue<@NonNull Modifiable> queue;
    @NotNull Map<@NotNull String, @NotNull Collection<@NotNull ModifiableReference>> modifiables;

    public static @NotNull ModifyWatcherManager create() {
        return new FakeModifyWatcherManager(new ReferenceQueue<>(), new HashMap<>());
    }

    private void cleanup() {
        Reference<? extends Modifiable> reference;

        while ((reference = queue.poll()) != null) {
            val modifiable = (ModifiableReference) reference;

            for (val file : modifiable.getFiles()) {
                remove(modifiable, file);
            }
        }
    }

    @Override
    public void close() {
    }

    @Override
    public boolean handle(@NotNull final String name) {
        cleanup();

        val modifiableSet = modifiables.get(name);
        if (modifiableSet == null) return false;

        for (val reference : modifiableSet) {
            val oldFiles = reference.getFiles();
            if (!reference.handleModify()) continue;

            val newFiles = reference.getFiles();

            val addedFiles = new ArrayList<>(newFiles);
            addedFiles.removeAll(oldFiles);

            val removedFiles = new ArrayList<>(oldFiles);
            removedFiles.removeAll(newFiles);

            for (val add : addedFiles) {
                add(reference, add);
            }

            for (val remove : removedFiles) {
                remove(reference, remove);
            }
        }

        return true;
    }

    @Override
    public void register(final @NonNull Modifiable modifiable) {
        val reference = new ModifiableReference(modifiable, queue);

        for (val file : modifiable.getFiles()) {
            add(reference, file);
        }
    }

    private void add(final @NotNull ModifiableReference modifiable, final @NotNull String name) {
        modifiables.computeIfAbsent(name, __ -> new HashSet<>())
                .add(modifiable);
    }

    private void remove(final @NotNull ModifiableReference modifiable, final @NotNull String name) {
        val modifiableSet = modifiables.get(name);
        modifiableSet.remove(modifiable);

        if (modifiableSet.isEmpty()) {
            modifiables.remove(name);
        }
    }

}
