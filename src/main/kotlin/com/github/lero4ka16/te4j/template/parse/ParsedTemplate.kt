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
package com.github.lero4ka16.te4j.template.parse

import com.github.lero4ka16.te4j.modifiable.watcher.ModifyWatcherManager
import com.github.lero4ka16.te4j.template.Template
import com.github.lero4ka16.te4j.template.context.TemplateContext
import com.github.lero4ka16.te4j.template.path.TemplatePath
import com.github.lero4ka16.te4j.util.type.ref.ITypeRef
import java.util.function.IntPredicate

/**
 * @author lero4ka16
 */
abstract class ParsedTemplate(
    protected val context: TemplateContext,
    val rawContent: ByteArray,
    var offset: Int,
    var length: Int
) {
    private fun hasNewlines(): Boolean {
        for (i in 0 until length) {
            if (rawContent[offset + i] == '\n'.toByte()) {
                return true
            }
        }

        return false
    }

    private var _content: ByteArray? = null

    val content: ByteArray
        get() {
            if (_content == null) {
                _content =
                    if (offset != 0 || length != rawContent.size)
                        rawContent.copyOfRange(offset, offset + length)
                    else rawContent
            }

            return _content!!
        }

    fun trim() {
        val inline = !hasNewlines()
        if (inline) return

        // trim spaces until crlf
        trim { value: Int -> space(value) }
        // trim beginning crlf
        trim { value: Int -> crlf(value) }

        _content = null
    }

    private fun space(value: Int): Boolean {
        return value == ' '.toInt() || value == '\t'.toInt()
    }

    private fun crlf(value: Int): Boolean {
        return value == '\r'.toInt() || value == '\n'.toInt()
    }

    private fun trim(value: IntPredicate) {
        while (length != 0 && value.test(rawContent[offset].toInt() and 0xFF)) {
            offset++
            length--
        }
    }

    abstract fun <BoundType> compile(
        modifyWatcherManager: ModifyWatcherManager?,
        parentFile: String, file: String,
        type: ITypeRef<BoundType>
    ): Template<BoundType>

    abstract val hasPaths: Boolean
    abstract val paths: List<TemplatePath>

    init {
        if (offset < 0 || offset >= rawContent.size) {
            throw IllegalArgumentException(
                "offset must be between 0 and "
                        + rawContent.size + " inclusive, but " + offset
            )
        }

        if (length < 0 || offset + length > rawContent.size) {
            throw IllegalArgumentException(
                "length must be between 0 and "
                        + (rawContent.size - offset) + ", but " + length
            )
        }

        trim()
    }

}