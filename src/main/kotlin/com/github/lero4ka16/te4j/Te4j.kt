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

package com.github.lero4ka16.te4j;

import com.github.lero4ka16.te4j.filter.Filters
import com.github.lero4ka16.te4j.modifiable.watcher.ModifyWatcherManager
import com.github.lero4ka16.te4j.template.Template
import com.github.lero4ka16.te4j.template.context.TemplateContext
import com.github.lero4ka16.te4j.template.context.TemplateContextBuilder
import com.github.lero4ka16.te4j.template.parse.ParsedTemplate
import com.github.lero4ka16.te4j.util.type.ref.ITypeRef
import java.io.File
import java.nio.file.Path

/**
 * @author lero4ka16
 */
class Te4j {

    internal companion object {
        const val DEL_CR = 1
        const val DEL_LF = 2
        const val DEL_REPEATING_SPACES = 3
        const val DEL_TAB = 4
        const val DEL_ALL = DEL_CR or DEL_LF or DEL_REPEATING_SPACES or DEL_TAB

        const val STRING = 1
        const val BYTES = 2

        @JvmField val OUTPUT_TYPES = intArrayOf(STRING, BYTES)

        private val FILTERS = Filters()

        private val DEFAULT_MODIFY_WATCHER = ModifyWatcherManager()

        private val DEFAULTS = custom()
            .replace(DEL_ALL)
            .outputTypes(STRING or BYTES)
            .enableHotReloading(DEFAULT_MODIFY_WATCHER)
            .build()

        @JvmStatic
        fun getDefaultModifyWatcher(): ModifyWatcherManager {
            return DEFAULT_MODIFY_WATCHER;
        }

        @JvmStatic
        fun getFilters(): Filters {
            return FILTERS
        }

        /**
         * @return Default template context
         */
        @JvmStatic
        fun defaults(): TemplateContext {
            return DEFAULTS
        }

        @JvmStatic
        fun parse(name: String): ParsedTemplate {
            return DEFAULTS.parse(name);
        }

        @JvmStatic
        fun parseFile(file: File): ParsedTemplate {
            return DEFAULTS.parseFile(file);
        }

        @JvmStatic
        fun parseFile(path: Path): ParsedTemplate {
            return DEFAULTS.parseFile(path);
        }

        /*
        fun <T : Any> loadFile(type: KClass<T>, file: File): Template<T> {
            return DEFAULTS.loadFile(type, file)
        }

        fun <T : Any> loadFile(type: KClass<T>, file: Path): Template<T> {
            return DEFAULTS.loadFile(type, file)
        }

        fun <T : Any> load(type: KClass<T>, file: File): Template<T> {
            return DEFAULTS.loadFile(type, file)
        }
        */

        @JvmStatic
        fun <T> load(type: Class<T>, name: String): Template<T> {
            return DEFAULTS.load(type, name)
        }

        @JvmStatic
        fun <T> load(type: ITypeRef<T>, name: String): Template<T> {
            return DEFAULTS.load(type, name)
        }

        @JvmStatic
        fun <T> loadFile(type: Class<T>, file: File): Template<T> {
            return DEFAULTS.loadFile(type, file)
        }

        @JvmStatic
        fun <T> loadFile(type: ITypeRef<T>, file: File): Template<T> {
            return DEFAULTS.loadFile(type, file)
        }

        @JvmStatic
        fun <T> loadFile(type: Class<T>, file: Path): Template<T> {
            return DEFAULTS.loadFile(type, file)
        }

        @JvmStatic
        fun <T> loadFile(type: ITypeRef<T>, file: Path): Template<T> {
            return DEFAULTS.loadFile(type, file)
        }

        /**
         * @return New custom template context builder
         */
        @JvmStatic
        fun custom(): TemplateContextBuilder {
            return TemplateContextBuilder();
        }

    }

    init {
        throw UnsupportedOperationException()
    }

}
