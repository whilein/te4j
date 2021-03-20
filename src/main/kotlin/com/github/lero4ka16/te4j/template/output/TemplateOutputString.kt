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

import java.nio.charset.StandardCharsets

/**
 * @author lero4ka16
 */
class TemplateOutputString @JvmOverloads constructor(capacity: Int = 32) : TemplateOutput() {

    private val builder: StringBuilder = StringBuilder(capacity)

    override fun toString(): String {
        return builder.toString()
    }

    val length: Int
        get() = builder.length

    override fun put(value: String) {
        builder.append(value)
    }

    override fun put(d: Double) {
        builder.append(d)
    }

    override fun put(f: Float) {
        builder.append(f)
    }

    override fun put(arg: Long) {
        builder.append(arg)
    }

    override fun put(arg: Int) {
        builder.append(arg)
    }

    override fun write(ch: Int) {
        throw UnsupportedOperationException()
    }

    override fun write(bytes: ByteArray) {
        throw UnsupportedOperationException()
    }

    override fun write(bytes: ByteArray, off: Int, len: Int) {
        throw UnsupportedOperationException()
    }

    fun write(value: String?) {
        builder.append(value)
    }

    fun reset() {
        builder.setLength(0)
    }

    fun toByteArray(): ByteArray {
        return builder.toString().toByteArray(StandardCharsets.UTF_8)
    }

    override fun flush() {}
    override fun close() {}

}