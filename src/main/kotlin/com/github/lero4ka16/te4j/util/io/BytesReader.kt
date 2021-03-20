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
class BytesReader(private val value: ByteArray) : DataReader() {
    override fun substring(start: Int, end: Int): String {
        return String(value, start, end - start)
    }

    override fun at(position: Int): Int {
        return value[position].toInt() and 0xFF
    }

    override val length
        get() = value.size
}