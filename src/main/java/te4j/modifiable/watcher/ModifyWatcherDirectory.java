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
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import te4j.modifiable.ModifiableReference;

import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.util.*;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
final class ModifyWatcherDirectory {

    WatchKey key;

    Map<Path, Set<ModifiableReference>> files
            = new HashMap<>();

    public synchronized boolean hasFiles() {
        return !files.isEmpty();
    }

    public synchronized void removeFile(Path path, ModifiableReference reference) {
        Set<ModifiableReference> references = files.get(path);

        if (references == null) {
            return;
        }

        references.remove(reference);

        if (references.isEmpty()) {
            files.remove(path);
        }
    }

    public void remove() {
        key.reset();
    }

    public synchronized Collection<ModifiableReference> getFiles(Path path) {
        return new ArrayList<>(files.getOrDefault(path, Collections.emptySet()));
    }

    public synchronized void addFile(Path path, ModifiableReference reference) {
        files.computeIfAbsent(path, x -> new HashSet<>()).add(reference);
    }

}
