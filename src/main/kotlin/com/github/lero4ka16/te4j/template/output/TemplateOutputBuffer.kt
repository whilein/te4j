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
package com.github.lero4ka16.te4j.template.output

/**
 * @author lero4ka16
 */
class TemplateOutputBuffer @JvmOverloads constructor(capacity: Int = 32) : TemplateOutput() {
    private var value: ByteArray

    var length = 0
        private set

    override fun toString(): String {
        return String(value, 0, length)
    }

    fun toByteArray(): ByteArray {
        return value.copyOf(length)
    }

    fun ensure(len: Int) {
        if (length + len > value.size) {
            value = value.copyOf((value.size + len).coerceAtLeast(value.size * 2))
        }
    }

    override fun write(ch: Int) {
        ensure(1)

        value[length++] = ch.toByte()
    }

    override fun write(bytes: ByteArray) {
        write(bytes, 0, bytes.size)
    }

    override fun write(bytes: ByteArray, off: Int, len: Int) {
        if (len == 0) return

        ensure(len)
        System.arraycopy(bytes, off, value, length, len)

        length += len
    }

    fun reset() {
        length = 0
    }

    override fun flush() {}
    override fun close() {}

    init {
        value = ByteArray(capacity)
    }
}