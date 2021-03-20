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
package com.github.lero4ka16.te4j.template

import com.github.lero4ka16.te4j.modifiable.Modifiable
import com.github.lero4ka16.te4j.modifiable.watcher.ModifyWatcherManager
import com.github.lero4ka16.te4j.template.context.TemplateContext
import com.github.lero4ka16.te4j.template.output.TemplateOutputBuffer
import com.github.lero4ka16.te4j.template.output.TemplateOutputString
import com.github.lero4ka16.te4j.util.type.ref.ITypeRef
import java.io.IOException
import java.io.OutputStream
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author lero4ka16
 */
abstract class Template<BoundType> {
    abstract val includes: Array<String>

    abstract fun renderAsString(input: BoundType): String
    abstract fun renderAsBytes(input: BoundType): ByteArray

    @Throws(IOException::class)
    abstract fun renderTo(input: BoundType, os: OutputStream)

    private class HotReloadingWrapper<BoundType>(
        modifyWatcherManager: ModifyWatcherManager,
        private val context: TemplateContext,
        private val type: ITypeRef<BoundType>,
        @Volatile private var handle: Template<BoundType>,
        private val file: String
    ) : Template<BoundType>(), Modifiable {

        private val lock = Object()

        @Volatile
        private var locked = false

        override fun handleModify() {
            locked = true
            handle = context.load(type, file)
            locked = false

            synchronized(lock) { lock.notifyAll() }
        }

        private fun awaitUnlock() {
            if (locked) {
                synchronized(lock) {
                    try {
                        lock.wait()
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        override val files: Collection<Path>
            get() {
                val includes = includes

                val result = ArrayList<Path>()
                result.add(Paths.get(file))

                for (include in includes) {
                    result.add(Paths.get(include))
                }

                return result
            }

        override fun renderAsString(input: BoundType): String {
            awaitUnlock()
            return handle.renderAsString(input)
        }

        override fun renderAsBytes(input: BoundType): ByteArray {
            awaitUnlock()
            return handle.renderAsBytes(input)
        }

        @Throws(IOException::class)
        override fun renderTo(input: BoundType, os: OutputStream) {
            awaitUnlock()
            handle.renderTo(input, os)
        }

        init {
            modifyWatcherManager.register(this)
        }

        override val includes: Array<String>
            get() {
                awaitUnlock()
                return handle.includes
            }
    }

    companion object {
        // ==================================================================================
        //                  !! Warning !! Do not remove following fields:
        @JvmField
        protected val bytesOptimized = ThreadLocal.withInitial { TemplateOutputBuffer() }

        @JvmField
        protected val stringOptimized = ThreadLocal.withInitial { TemplateOutputString() }
        // ==================================================================================

        fun <BoundType> wrapHotReloading(
            modifyWatcherManager: ModifyWatcherManager,
            context: TemplateContext,
            template: Template<BoundType>,
            type: ITypeRef<BoundType>,
            file: String
        ): Template<BoundType> {
            return HotReloadingWrapper(modifyWatcherManager, context, type, template, file)
        }
    }
}