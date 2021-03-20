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

import com.github.lero4ka16.te4j.modifiable.watcher.ModifyWatcherManager
import com.github.lero4ka16.te4j.template.Template
import com.github.lero4ka16.te4j.template.exception.TemplateLoadException
import com.github.lero4ka16.te4j.template.parse.ParsedTemplate
import com.github.lero4ka16.te4j.template.reader.TemplateReader
import com.github.lero4ka16.te4j.util.Utils.Companion.readBytes
import com.github.lero4ka16.te4j.util.Utils.Companion.readFile
import com.github.lero4ka16.te4j.util.type.ref.ClassRef
import com.github.lero4ka16.te4j.util.type.ref.ITypeRef
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author lero4ka16
 */
class TemplateContext internal constructor(
    private val useResources: Boolean,
    private val modifyWatcherManager: ModifyWatcherManager?,
    val outputTypes: Int, val replace: Int
) {

    private fun getParent(name: String): String {
        var separator = -1

        for (i in name.length - 1 downTo 0) {
            val ch = name[i]
            if (ch == '/' || ch == '\\') {
                separator = i
                break
            }
        }

        return if (separator == -1) "." else name.substring(0, separator)
    }

    fun <BoundType> load(
        type: ITypeRef<BoundType>,
        name: String
    ): Template<BoundType> {
        return parse(name).compile(
            if (useResources) null else modifyWatcherManager,
            getParent(name), name, type
        )
    }

    fun <BoundType> loadFile(
        type: ITypeRef<BoundType>,
        file: File
    ): Template<BoundType> {
        return parseFile(file).compile(
            modifyWatcherManager,
            file.absoluteFile.parent,
            file.absolutePath,
            type
        )
    }

    fun <BoundType> loadFile(
        type: ITypeRef<BoundType>,
        path: Path
    ): Template<BoundType> {
        return parseFile(path).compile(
            modifyWatcherManager,
            path.toAbsolutePath().parent.toString(),
            path.toAbsolutePath().toString(),
            type
        )
    }

    fun <BoundType> load(type: Class<BoundType>, name: String): Template<BoundType> {
        return load(ClassRef(type), name)
    }

    fun <BoundType> loadFile(type: Class<BoundType>, file: File): Template<BoundType> {
        return loadFile(ClassRef(type), file)
    }

    fun <BoundType> loadFile(type: Class<BoundType>, path: Path): Template<BoundType> {
        return loadFile(ClassRef(type), path)
    }

    private fun parse(bytes: ByteArray): ParsedTemplate {
        return TemplateReader(this, bytes).readTemplate()
    }

    /**
     * Parse template from filename or resource
     *
     * @param name Filename or resource
     * @return Parsed template
     */
    fun parse(name: String): ParsedTemplate {
        return if (useResources) {
            try {
                val stream = ClassLoader.getSystemResourceAsStream(name)
                    ?: throw FileNotFoundException("Resource not found: $name")
                parse(readBytes(stream))
            } catch (e: IOException) {
                throw TemplateLoadException("Cannot read template", e)
            }
        } else {
            parseFile(Paths.get(name))
        }
    }

    fun parseFile(file: File): ParsedTemplate {
        return try {
            parse(readFile(file))
        } catch (e: IOException) {
            throw TemplateLoadException("Cannot read template", e)
        }
    }

    fun parseFile(path: Path): ParsedTemplate {
        return try {
            parse(Files.readAllBytes(path))
        } catch (e: IOException) {
            throw TemplateLoadException("Cannot read template", e)
        }
    }
}