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

import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

final class ModifyWatcherDirectory {

    private final WatchKey key;
    private final Set<Path> files = ConcurrentHashMap.newKeySet();

    public ModifyWatcherDirectory(WatchKey key) {
        this.key = key;
    }

    public boolean hasFiles() {
        return !files.isEmpty();
    }

    public void removeFile(Path path) {
        files.remove(path);
    }

    public void remove() {
        key.reset();
    }

    public void addFile(Path path) {
        files.add(path);
    }
}
