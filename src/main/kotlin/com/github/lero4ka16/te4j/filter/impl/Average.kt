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
class Average : Filter {

    override val name: String = "average"

    override fun apply(value: String): String {
        return "${javaClass.name}.process($value)"
    }

    companion object {
        @JvmStatic
        fun process(value: ByteArray): Double {
            return if (value.isEmpty()) 0.0 else Sum.process(value) / value.size
        }

        @JvmStatic
        fun process(value: ShortArray): Double {
            return if (value.isEmpty()) 0.0 else Sum.process(value) / value.size
        }

        @JvmStatic
        fun process(value: IntArray): Double {
            return if (value.isEmpty()) 0.0 else Sum.process(value) / value.size
        }

        @JvmStatic
        fun process(value: LongArray): Double {
            return if (value.isEmpty()) 0.0 else Sum.process(value) / value.size
        }

        @JvmStatic
        fun process(value: FloatArray): Double {
            return if (value.isEmpty()) 0.0 else Sum.process(value) / value.size
        }

        @JvmStatic
        fun process(value: DoubleArray): Double {
            return if (value.isEmpty()) 0.0 else Sum.process(value) / value.size
        }

        @JvmStatic
        fun process(value: Array<*>): Double {
            return if (value.isEmpty()) 0.0 else Sum.process(value) / value.size
        }

        @JvmStatic
        fun process(value: Collection<*>): Double {
            return if (value.isEmpty()) 0.0 else Sum.process(value) / value.size
        }
    }

}