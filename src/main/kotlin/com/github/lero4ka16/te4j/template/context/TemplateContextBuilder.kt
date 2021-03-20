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
package com.github.lero4ka16.te4j.template.context

import com.github.lero4ka16.te4j.Te4j
import com.github.lero4ka16.te4j.Te4j.Companion.getDefaultModifyWatcher
import com.github.lero4ka16.te4j.modifiable.watcher.ModifyWatcherManager

/**
 * @author lero4ka16
 */
class TemplateContextBuilder {
    private var useResources = false
    private var modifyWatcherManager: ModifyWatcherManager? = null
    private var outputTypes = 0
    private var replace = 0

    fun outputTypes(bits: Int): TemplateContextBuilder {
        outputTypes = bits
        return this
    }

    @JvmOverloads
    fun enableHotReloading(modifyWatcherManager: ModifyWatcherManager? = getDefaultModifyWatcher()): TemplateContextBuilder {
        this.modifyWatcherManager = modifyWatcherManager
        return this
    }

    fun useResources(): TemplateContextBuilder {
        useResources = true
        return this
    }

    fun replace(value: Int): TemplateContextBuilder {
        replace = value
        return this
    }

    fun build(): TemplateContext {
        if (outputTypes == 0) {
            outputTypes(Te4j.STRING or Te4j.BYTES)
        }

        return TemplateContext(useResources, modifyWatcherManager, outputTypes, replace)
    }
}