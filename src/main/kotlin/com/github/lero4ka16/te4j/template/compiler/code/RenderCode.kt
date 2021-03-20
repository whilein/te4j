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
package com.github.lero4ka16.te4j.template.compiler.code

import com.github.lero4ka16.te4j.template.compiler.TemplateCompileProcess
import com.github.lero4ka16.te4j.template.parse.ParsedTemplate
import com.github.lero4ka16.te4j.template.path.TemplatePath
import com.github.lero4ka16.te4j.util.formatter.TextFormatter
import java.io.ByteArrayOutputStream
import java.io.IOException

/**
 * @author lero4ka16
 */
class RenderCode(private val process: TemplateCompileProcess<*>) {
    private val fieldTextMap: MutableMap<Int, String> = HashMap()
    private var field: Int? = null
    private var prevText = false
    private val textBuffer = ByteArrayOutputStream()
    private val out = StringBuilder()
    private var position = -1

    fun appendTemplate(template: ParsedTemplate) {
        appendTemplate(
            template.rawContent, template.offset,
            template.length, template.paths
        )
    }

    private fun appendTemplate(template: ByteArray?, off: Int, len: Int, paths: List<TemplatePath>) {
        val compiled = process.compilePaths(paths)

        var startIndex = off

        for (path in compiled) {
            appendTextSegment(template, startIndex, path.offset - startIndex)
            process.writePath(path, this)
            startIndex = path.offset + path.length
        }

        appendTextSegment(template, startIndex, len + off - startIndex)
        flushText()
    }

    fun flushTemplates() {
        for ((key, value) in fieldTextMap) {
            process.addField(key, value)
        }
    }

    fun append(code: String): RenderCode {
        appendCodeSegment(code)
        return this
    }

    fun position(): Int {
        return if (position == -1) out.length else position
    }

    fun setPosition(position: Int) {
        this.position = position
    }

    fun resetPosition() {
        position = -1
    }

    fun appendCodeSegment(codeSegment: String) {
        if (prevText) {
            prevText = false
            flushText()
        }

        if (position == -1) {
            out.append(codeSegment)
        } else {
            out.insert(position, codeSegment)
            position += codeSegment.length
        }
    }

    fun appendTextSegment(template: ParsedTemplate) {
        appendTextSegment(template.rawContent, template.offset, template.length)
    }

    private fun appendTextSegment(buf: ByteArray?, off: Int, len: Int) {
        if (len == 0) {
            return
        }

        requireNotNull(buf) { "buf is null" }
        require(!(off < 0 || off >= buf.size)) { "off is less than zero or more than " + buf.size }
        require(!(len < 0 || len + off > buf.size)) { "off is less than zero or more than " + (buf.size - off) }

        if (!prevText) {
            field = process.nameCounter.incrementAndGet()
        }

        prevText = true
        textBuffer.write(buf, off, len)
    }

    private fun getString(bytes: ByteArray): String {
        val sb = StringBuilder()

        try {
            TextFormatter(bytes)
                .replace(process.context.replace)
                .write(sb)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return sb.toString()
    }

    private fun flushText() {
        if (field == null) {
            return;
        }

        val value = getString(textBuffer.toByteArray())

        if (value.isEmpty()) {
            return
        }

        fieldTextMap[field!!] = value

        out.append("out.write(")
        out.append(process.getOutputPrefix(process.outputType))
        out.append(field)
        out.append(");")

        textBuffer.reset()
        field = null
    }

    override fun toString(): String {
        return out.toString()
    }
}