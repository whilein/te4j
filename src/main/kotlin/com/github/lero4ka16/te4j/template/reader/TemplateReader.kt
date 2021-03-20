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
package com.github.lero4ka16.te4j.template.reader

import com.github.lero4ka16.te4j.include.IncludeTarget
import com.github.lero4ka16.te4j.template.context.TemplateContext
import com.github.lero4ka16.te4j.template.exception.TemplateException
import com.github.lero4ka16.te4j.template.exception.TemplateUnexpectedTokenException
import com.github.lero4ka16.te4j.template.method.TemplateMethod
import com.github.lero4ka16.te4j.template.method.TemplateMethodType
import com.github.lero4ka16.te4j.template.method.impl.Condition
import com.github.lero4ka16.te4j.template.method.impl.Foreach
import com.github.lero4ka16.te4j.template.method.impl.Include
import com.github.lero4ka16.te4j.template.method.impl.SwitchCase
import com.github.lero4ka16.te4j.template.method.impl.Value
import com.github.lero4ka16.te4j.template.parse.ParsedTemplate
import com.github.lero4ka16.te4j.template.parse.PlainParsedTemplate
import com.github.lero4ka16.te4j.template.parse.StandardParsedTemplate
import com.github.lero4ka16.te4j.template.path.TemplatePath
import com.github.lero4ka16.te4j.template.reader.token.TemplateToken
import com.github.lero4ka16.te4j.template.reader.token.TemplateTokenType
import com.github.lero4ka16.te4j.util.formatter.TextFormatter
import com.github.lero4ka16.te4j.util.io.BytesReader
import com.github.lero4ka16.te4j.util.io.DataReader
import java.util.ArrayList

class TemplateReader(private val context: TemplateContext, private val value: ByteArray) {
    private val reader: DataReader
    private fun newTemplate(paths: List<TemplatePath>, begin: Int, end: Int, inner: Boolean): ParsedTemplate {
        return if (paths.isEmpty()) {
            if (!inner) {
                val processed = TextFormatter(value, begin, end - begin)
                    .disableEscaping()
                    .replace(context.replace)
                    .formatAsBytes()
                PlainParsedTemplate(context, processed, 0, processed.size)
            } else {
                PlainParsedTemplate(context, value, begin, end - begin)
            }
        } else {
            StandardParsedTemplate(context, paths, value, begin, end - begin)
        }
    }

    fun readTemplate(): ParsedTemplate {
        val begin = reader.position()
        val paths: MutableList<TemplatePath> = ArrayList()
        readPathsTo(paths)
        val end = reader.position()
        return newTemplate(paths, begin, end, false)
    }

    private fun readPathsTo(paths: MutableList<TemplatePath>) {
        var value: Int

        while (reader.read().also { value = it } != -1) {
            val position = reader.position()

            val path: TemplatePath? = when (value.toChar()) {
                '^' -> {
                    reader.roll()
                    readValue()
                }
                '<' -> {
                    reader.roll()
                    readOperation()
                }
                else -> continue
            }

            if (path == null) {
                reader.position(position)
            } else {
                paths.add(path)
            }
        }
    }

    private fun readValue(): TemplatePath? {
        var pathBegin = reader.position()
        val startA = reader.read()
        val startB = reader.read()

        // должно быть ^^, без пробелов и других знаков
        if (startA != startB) return null
        var valueBegin = reader.position()

        while (true) {
            val value = reader.read()
            if (value == -1) return null
            if (value == '^'.toInt()) {
                if (reader.read() == '^'.toInt()) break else reader.roll()
                if (valueBegin + 1 == reader.position()) {
                    valueBegin++
                    pathBegin++
                }
            }
        }

        val pathEnd = reader.position()
        val valueEnd = pathEnd - 2 // }}
        val value = reader.substring(valueBegin, valueEnd).trim { it <= ' ' }

        return TemplatePath(pathBegin, pathEnd - pathBegin, Value(value))
    }

    @Throws(TemplateUnexpectedTokenException::class)
    private fun readInner(vararg types: TemplateTokenType): Inner {
        val blockBegin = reader.position()
        val blockEnd: Int
        val innerPaths: MutableList<TemplatePath> = ArrayList()
        val token: TemplateToken

        try {
            readPathsTo(innerPaths)

            throw IllegalStateException("No inner block found")
        } catch (e: TemplateUnexpectedTokenException) {
            token = e.token
            blockEnd = e.position

            token.expect(blockEnd, *types)
        }

        return Inner(newTemplate(innerPaths, blockBegin, blockEnd, true), token.type)
    }

    @Throws(TemplateUnexpectedTokenException::class)
    private fun readOperation(): TemplatePath? {
        val begin = reader.position()
        val token = readToken(reader) ?: return null
        token.expect(begin, TemplateTokenType.BEGIN)
        val fullPath = token.value
        var separator = fullPath.indexOf(' ')
        val methodName: String
        val path: String

        if (separator == -1) {
            methodName = fullPath
            path = ""
        } else {
            methodName = fullPath.substring(0, separator)
            path = fullPath.substring(separator + 1)
        }

        val methodType = TemplateMethodType.findType(methodName)
        val method: TemplateMethod

        when (methodType) {
            TemplateMethodType.INCLUDE -> {
                method = Include(IncludeTarget(path))
            }

            TemplateMethodType.FOR -> {
                separator = path.indexOf(':')
                check(separator != -1) { "Not found ':' in $path" }
                val `as` = path.substring(0, separator).trim { it <= ' ' }
                val value = path.substring(separator + 1).trim { it <= ' ' }
                method = Foreach(value, `as`, readInner(TemplateTokenType.END_FOR).template)
            }

            TemplateMethodType.CASE -> {
                separator = path.indexOf(':')
                val value: String
                val from: String?
                if (separator == -1) {
                    value = path
                    from = null
                } else {
                    value = path.substring(0, separator).trim { it <= ' ' }
                    from = path.substring(separator + 1).trim { it <= ' ' }
                }
                val inner = readInner(TemplateTokenType.CASE_DEFAULT, TemplateTokenType.END_CASE)

                method = if (inner.byToken === TemplateTokenType.END_CASE) {
                    SwitchCase(value, from, inner.template, null)
                } else {
                    SwitchCase(value, from, inner.template, readInner(TemplateTokenType.END_CASE).template)
                }
            }

            TemplateMethodType.CONDITION -> {
                val inner = readInner(TemplateTokenType.ELSE, TemplateTokenType.END_IF)
                method = if (inner.byToken === TemplateTokenType.END_IF) {
                    Condition(path, inner.template, null)
                } else {
                    Condition(path, inner.template, readInner(TemplateTokenType.END_IF).template)
                }
            }

            else -> {
                method = Include(IncludeTarget(path))
            }
        }

        return TemplatePath(begin, reader.position() - begin, method)
    }

    private fun readToken(reader: DataReader): TemplateToken? {
        if (reader.read() != '<'.toInt() || reader.read() != '*'.toInt()) { // <*
            return null
        }

        // читаем первый символ, который не является пробелом
        if (!reader.moveNonWhitespace()) return null
        val start = reader.position()
        while (true) {
            if (!reader.move('*'.toInt())) {
                throw TemplateException("Not found closing for token")
            }

            reader.next()
            val ch = reader.readNonWhitespace()
            if (ch == -1) return null
            if (ch == '>'.toInt()) break
        }
        val end = reader.position() - 2 // *>
        val path = reader.substring(start, end).trim { it <= ' ' }
        return TemplateToken(path, TemplateTokenType.getType(path))
    }

    private class Inner(val template: ParsedTemplate, val byToken: TemplateTokenType)

    init {
        reader = BytesReader(value)
    }
}