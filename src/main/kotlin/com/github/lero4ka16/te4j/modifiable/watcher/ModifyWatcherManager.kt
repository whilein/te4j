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

import com.github.lero4ka16.te4j.modifiable.Modifiable
import com.github.lero4ka16.te4j.modifiable.ModifiableReference
import java.io.IOException
import java.lang.ref.Reference
import java.lang.ref.ReferenceQueue
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchService

class ModifyWatcherManager {
    var watcher: WatchService? = null

    private val queue = ReferenceQueue<Modifiable?>()
    private val directories: MutableMap<Path, ModifyWatcherDirectory> = HashMap()

    private fun cleanup() {
        var reference: Reference<out Modifiable?>?

        while (queue.poll().also { reference = it } != null) {
            val modifiable = reference as ModifiableReference

            for (path in modifiable.files) {
                val abs = path.toAbsolutePath()
                val absParent = abs.parent

                val directory = directories[absParent] ?: continue
                directory.removeFile(abs)

                if (!directory.hasFiles()) {
                    directory.remove()

                    directories.remove(absParent)
                }
            }
        }
    }

    @Synchronized
    fun handle(path: Path) {
        cleanup()
        val abs = path.toAbsolutePath()
        val absParent = abs.parent
        val directory = directories[absParent] ?: return
        val file = directory.getFile(abs) ?: return
        file.handleModify()
    }

    @Synchronized
    fun register(modifiable: Modifiable) {
        val reference = ModifiableReference(modifiable, queue)

        for (path in modifiable.files) {
            val abs = path.toAbsolutePath()
            val absParent = abs.parent

            directories.computeIfAbsent(absParent) { parent: Path ->
                try {
                    return@computeIfAbsent ModifyWatcherDirectory(
                        parent.register(
                            watcher,
                            StandardWatchEventKinds.ENTRY_MODIFY
                        )
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                    throw RuntimeException(e)
                }
            }.addFile(abs, reference)
        }
    }

    init {
        watcher = try {
            FileSystems.getDefault().newWatchService()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

        val thread = ModifyWatcherThread(this)
        thread.start()
    }
}