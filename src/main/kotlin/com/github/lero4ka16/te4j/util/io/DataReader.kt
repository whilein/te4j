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
package com.github.lero4ka16.te4j.util.io

/**
 * @author lero4ka16
 */
abstract class DataReader {
    protected var position = 0

    abstract fun substring(start: Int, end: Int): String
    abstract fun at(position: Int): Int

    abstract val length: Int

    val isReadable: Boolean
        get() = position != length

    fun move(value: Int): Boolean {
        val pos = position()

        while (true) {
            val ch = read()

            if (ch == -1) {
                position(pos)
                return false
            }

            if (ch != value) {
                continue
            } else {
                position--
            }
            return true
        }
    }

    fun moveNonWhitespace(): Boolean {
        val pos = position()
        while (true) {
            val ch = read()
            if (ch == -1) {
                position(pos)
            } else if (isSpace(ch)) {
                continue
            } else {
                position--
            }
            return ch != -1
        }
    }

    fun position(): Int {
        return position
    }

    operator fun next() {
        if (position != length) position++
    }

    fun readNonWhitespace(): Int {
        return if (moveNonWhitespace()) read() else -1
    }

    fun readString(): String {
        return substring(position, length)
    }

    fun read(): Int {
        return if (isReadable) at(position++) else -1
    }

    fun position(position: Int) {
        this.position = position
    }

    fun roll() {
        position--
    }

    companion object {
        fun isSpace(ch: Int): Boolean {
            return ch <= 0x20
        }
    }
}