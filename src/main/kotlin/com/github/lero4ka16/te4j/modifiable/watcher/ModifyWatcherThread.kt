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
package com.github.lero4ka16.te4j.modifiable.watcher

import java.nio.file.Path
import java.nio.file.WatchKey

internal class ModifyWatcherThread(
    private val service: ModifyWatcherManager
) : Thread("ModifyWatcher") {
    override fun run() {
        try {
            var key: WatchKey

            while (service.watcher!!.take().also { key = it } != null) {
                val dir = key.watchable() as Path

                for (event in key.pollEvents()) {
                    val path = event.context() as Path
                    service.handle(dir.resolve(path))
                }

                key.reset()
            }
        } catch (ignored: Throwable) {
        }
    }

    init {
        isDaemon = true
    }
}