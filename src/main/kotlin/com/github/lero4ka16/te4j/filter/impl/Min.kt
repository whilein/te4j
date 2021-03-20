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

package com.github.lero4ka16.te4j.filter.impl

import com.github.lero4ka16.te4j.filter.Filter

/**
 * @author lero4ka16
 */
class Min : Filter {

    override val name: String = "min"

    override fun apply(value: String): String {
        return "${javaClass.name}.process($value)"
    }

    companion object {

        @JvmStatic
        fun process(value: CharArray): Char {
            if (value.isEmpty()) throw IllegalArgumentException("Input is empty")
            
            var res = value[0]
            for (i in 1 until value.size) {
                if (res > value[i]) {
                    res = value[i]
                }
            }
            return res
        }

        @JvmStatic
        fun process(value: ByteArray): Byte {
            if (value.isEmpty()) throw IllegalArgumentException("Input is empty")
            
            var res = value[0]
            for (i in 1 until value.size) {
                if (res > value[i]) {
                    res = value[i]
                }
            }
            return res
        }

        @JvmStatic
        fun process(value: ShortArray): Short {
            if (value.isEmpty())  throw IllegalArgumentException("Input is empty")
            var res = value[0]
            for (i in 1 until value.size) {
                if (res > value[i]) {
                    res = value[i]
                }
            }
            return res
        }

        @JvmStatic
        fun process(value: IntArray): Int {
            if (value.isEmpty())  throw IllegalArgumentException("Input is empty")
            var res = value[0]
            for (i in 1 until value.size) {
                if (res > value[i]) {
                    res = value[i]
                }
            }
            return res
        }

        @JvmStatic
        fun process(value: LongArray): Long {
            if (value.isEmpty()) throw IllegalArgumentException("Input is empty")
            var res = value[0]
            for (i in 1 until value.size) {
                if (res > value[i]) {
                    res = value[i]
                }
            }
            return res
        }

        @JvmStatic
        fun process(value: FloatArray): Float {
            if (value.isEmpty()) throw IllegalArgumentException("Input is empty")
            var res = value[0]
            for (i in 1 until value.size) {
                if (res > value[i]) {
                    res = value[i]
                }
            }
            return res
        }

        @JvmStatic
        fun process(value: DoubleArray): Double {
            if (value.isEmpty()) throw IllegalArgumentException("Input is empty")
            var res = value[0]
            for (i in 1 until value.size) {
                if (res > value[i]) {
                    res = value[i]
                }
            }
            return res
        }

        @JvmStatic
        fun <T : Comparable<T>> process(value: Iterable<T>): T {
            val iterator = value.iterator()
            if (!iterator.hasNext()) throw IllegalArgumentException("Input is empty")

            var res = iterator.next()

            while (iterator.hasNext()) {
                val that = iterator.next()

                if (res > that) {
                    res = that
                }
            }

            return res
        }

        @JvmStatic
        fun <T : Comparable<T>> process(value: Array<T>): T {
            if (value.isEmpty()) throw IllegalArgumentException("Input is empty")

            var res = value[0]

            for (i in 1 until value.size) {
                val that = value[i]

                if (res > that) {
                    res = that
                }
            }

            return res
        }

    }

}