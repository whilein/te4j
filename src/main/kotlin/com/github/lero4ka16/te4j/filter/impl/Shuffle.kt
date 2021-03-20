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
import com.github.lero4ka16.te4j.util.Utils
import java.util.ArrayList
import java.util.Random

/**
 * @author lero4ka16
 */
class Shuffle : Filter {

    override val name: String = "shuffle"

    override fun apply(value: String): String {
        return "${javaClass.name}.process($value)"
    }

    companion object {
        private val RANDOM = if (Utils.isJUnitTest) Random(1) else Random()

        @JvmStatic
        fun <T> process(value: Array<T>): Array<T> {
            if (value.isEmpty()) return value

            val result = value.copyOf()

            for (i in value.size - 1 downTo 1) {
                val j = RANDOM.nextInt(i + 1)
                val element = result[j]

                result[j] = result[i]
                result[i] = element
            }
            return result
        }

        @JvmStatic
        fun process(value: ByteArray): ByteArray {
            if (value.isEmpty()) return value
            val result = value.copyOf(value.size)
            for (i in value.size - 1 downTo 1) {
                val j = RANDOM.nextInt(i + 1)
                val element = result[j]
                result[j] = result[i]
                result[i] = element
            }
            return result
        }

        @JvmStatic
        fun process(value: ShortArray): ShortArray {
            if (value.isEmpty()) return value
            val result = value.copyOf(value.size)
            for (i in value.size - 1 downTo 1) {
                val j = RANDOM.nextInt(i + 1)
                val element = result[j]
                result[j] = result[i]
                result[i] = element
            }
            return result
        }

        @JvmStatic
        fun process(value: IntArray): IntArray {
            if (value.isEmpty()) return value
            val result = value.copyOf(value.size)
            for (i in value.size - 1 downTo 1) {
                val j = RANDOM.nextInt(i + 1)
                val element = result[j]
                result[j] = result[i]
                result[i] = element
            }
            return result
        }

        @JvmStatic
        fun process(value: LongArray): LongArray {
            if (value.isEmpty()) return value
            val result = value.copyOf(value.size)
            for (i in value.size - 1 downTo 1) {
                val j = RANDOM.nextInt(i + 1)
                val element = result[j]
                result[j] = result[i]
                result[i] = element
            }
            return result
        }

        @JvmStatic
        fun process(value: FloatArray): FloatArray {
            if (value.isEmpty()) return value
            val result = value.copyOf(value.size)
            for (i in value.size - 1 downTo 1) {
                val j = RANDOM.nextInt(i + 1)
                val element = result[j]
                result[j] = result[i]
                result[i] = element
            }
            return result
        }

        @JvmStatic
        fun process(value: DoubleArray): DoubleArray {
            if (value.isEmpty()) return value
            val result = value.copyOf(value.size)
            for (i in value.size - 1 downTo 1) {
                val j = RANDOM.nextInt(i + 1)
                val element = result[j]
                result[j] = result[i]
                result[i] = element
            }
            return result
        }

        @JvmStatic
        fun process(value: CharArray): CharArray {
            if (value.isEmpty()) return value
            val result = value.copyOf(value.size)
            for (i in value.size - 1 downTo 1) {
                val j = RANDOM.nextInt(i + 1)
                val element = result[j]
                result[j] = result[i]
                result[i] = element
            }
            return result
        }

        @JvmStatic
        fun process(value: Collection<*>): Collection<*> {
            if (value.isEmpty()) return value
            val collection = ArrayList(value)
            collection.shuffle(RANDOM)

            return collection
        }
    }

}