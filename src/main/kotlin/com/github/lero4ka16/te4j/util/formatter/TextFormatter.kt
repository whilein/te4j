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
package com.github.lero4ka16.te4j.util.formatter

import com.github.lero4ka16.te4j.Te4j
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStream
import java.nio.charset.StandardCharsets

class TextFormatter @JvmOverloads constructor(
    private val buf: ByteArray,
    private val off: Int = 0,
    private val len: Int = buf.size
) {
    constructor(value: String) : this(value.toByteArray(StandardCharsets.UTF_8), 0, value.length)

    private var escaping = true
    private var replace = 0

    fun replace(value: Int): TextFormatter {
        replace = value
        return this
    }

    fun disableEscaping(): TextFormatter {
        escaping = false
        return this
    }

    fun formatAsBytes(): ByteArray {
        val baos = ByteArrayOutputStream()
        try {
            write(baos)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return baos.toByteArray()
    }

    fun format(): String {
        return String(formatAsBytes())
    }

    @Throws(IOException::class)
    fun write(out: Appendable) {
        val baos = ByteArrayOutputStream()
        write(baos)
        out.append(baos.toString())
    }

    @Throws(IOException::class)
    fun write(out: OutputStream) {
        var insertSpace = false
        for (i in 0 until len) {
            val ch = buf[i + off].toChar()

            if (insertSpace && ch != ' ') {
                out.write(' '.toInt())
                insertSpace = false
            }

            when (ch) {
                '"' -> if (escaping) {
                    out.write('\\'.toInt())
                    out.write(ch.toInt())
                    continue
                }
                '\\' -> if (escaping) {
                    out.write(ch.toInt())
                    out.write(ch.toInt())
                    continue
                }
                '\n' -> {
                    if (replace and Te4j.DEL_LF != 0) {
                        continue
                    }
                    if (escaping) {
                        out.write('\\'.toInt())
                        out.write('n'.toInt())
                        continue
                    }
                    if (replace and Te4j.DEL_TAB != 0) {
                        continue
                    }
                    if (escaping) {
                        out.write('\\'.toInt())
                        out.write('t'.toInt())
                        continue
                    }
                    if (replace and Te4j.DEL_CR != 0) {
                        continue
                    }
                    if (escaping) {
                        out.write('\\'.toInt())
                        out.write('r'.toInt())
                        continue
                    }
                    if (replace and Te4j.DEL_REPEATING_SPACES != 0) {
                        insertSpace = true
                        continue
                    }
                }
                '\t' -> {
                    if (replace and Te4j.DEL_TAB != 0) {
                        continue
                    }
                    if (escaping) {
                        out.write('\\'.toInt())
                        out.write('t'.toInt())
                        continue
                    }
                    if (replace and Te4j.DEL_CR != 0) {
                        continue
                    }
                    if (escaping) {
                        out.write('\\'.toInt())
                        out.write('r'.toInt())
                        continue
                    }
                    if (replace and Te4j.DEL_REPEATING_SPACES != 0) {
                        insertSpace = true
                        continue
                    }
                }
                '\r' -> {
                    if (replace and Te4j.DEL_CR != 0) {
                        continue
                    }
                    if (escaping) {
                        out.write('\\'.toInt())
                        out.write('r'.toInt())
                        continue
                    }
                    if (replace and Te4j.DEL_REPEATING_SPACES != 0) {
                        insertSpace = true
                        continue
                    }
                }
                ' ' -> if (replace and Te4j.DEL_REPEATING_SPACES != 0) {
                    insertSpace = true
                    continue
                }
            }

            out.write(ch.toInt())
        }
        if (insertSpace) {
            out.write(' '.toInt())
        }
    }
}
