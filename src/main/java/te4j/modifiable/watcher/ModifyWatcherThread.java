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

import java.nio.file.ClosedWatchServiceException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;

final class ModifyWatcherThread extends Thread {

    private final ModifyWatcherManager service;

    public ModifyWatcherThread(ModifyWatcherManager service) {
        super("ModifyWatcher");

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
        } catch (ClosedWatchServiceException ignored) {
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
