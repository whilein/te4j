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
import org.jetbrains.annotations.UnmodifiableView;
import te4j.modifiable.ModifiableReference;

import java.nio.file.WatchKey;
import java.util.*;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SimpleModifyWatcherDirectory implements ModifyWatcherDirectory {

    @NotNull WatchKey key;
    @NotNull Map<@NotNull String, @NotNull Set<@NotNull ModifiableReference>> files;

    public static @NotNull ModifyWatcherDirectory create(
            final @NonNull WatchKey key
    ) {
        return new SimpleModifyWatcherDirectory(key, new HashMap<>());
    }

    @Override
    public synchronized boolean hasEntries() {
        return !files.isEmpty();
    }

    @Override
    public synchronized void addEntry(
            final @NonNull String name,
            final @NonNull ModifiableReference reference
    ) {
        files.computeIfAbsent(name, __ -> new HashSet<>()).add(reference);
    }

    @Override
    public synchronized void removeEntry(
            final @NonNull String name,
            final @NonNull ModifiableReference reference
    ) {
        val references = files.get(name);
        if (references == null) return;

        references.remove(reference);

        if (references.isEmpty()) {
            files.remove(name);
        }
    }

    @Override
    public synchronized @UnmodifiableView @NotNull Collection<@NotNull ModifiableReference> getEntries(
            final @NonNull String name
    ) {
        val references = files.get(name);
        if (references == null) return Collections.emptyList();

        return Collections.unmodifiableSet(new HashSet<>(references));
    }

    @Override
    public void close() {
        key.reset();
    }
}
