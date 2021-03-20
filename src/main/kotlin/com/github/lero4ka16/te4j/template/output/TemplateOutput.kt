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

import java.lang.reflect.Array
import java.nio.charset.StandardCharsets

/**
 * @author lero4ka16
 */
abstract class TemplateOutput {
    open fun put(value: String) {
        write(value.toByteArray(StandardCharsets.UTF_8))
    }

    private fun longLength(value: Long): Int {
        return if (value < 1000000000) {
            if (value < 100000) {
                if (value < 100) {
                    if (value < 10) {
                        1
                    } else {
                        2
                    }
                } else {
                    if (value < 1000) {
                        3
                    } else {
                        if (value < 10000) {
                            4
                        } else {
                            5
                        }
                    }
                }
            } else {
                if (value < 10000000) {
                    if (value < 1000000) {
                        6
                    } else {
                        7
                    }
                } else {
                    if (value < 100000000) {
                        8
                    } else {
                        9
                    }
                }
            }
        } else {
            if (value < 100000000000000L) {
                if (value < 100000000000L) {
                    if (value < 10000000000L) {
                        10
                    } else {
                        11
                    }
                } else {
                    if (value < 1000000000000L) {
                        12
                    } else {
                        if (value < 10000000000000L) {
                            13
                        } else {
                            14
                        }
                    }
                }
            } else {
                if (value < 10000000000000000L) {
                    if (value < 1000000000000000L) {
                        15
                    } else {
                        16
                    }
                } else {
                    if (value < 100000000000000000L) {
                        17
                    } else {
                        if (value < 1000000000000000000L) {
                            18
                        } else {
                            19
                        }
                    }
                }
            }
        }
    }

    // https://www.baeldung.com/java-number-of-digits-in-int#5-divide-and-conquer
    private fun intLength(value: Int): Int {
        return if (value < 100000) {
            if (value < 100) {
                if (value < 10) {
                    1
                } else {
                    2
                }
            } else {
                if (value < 1000) {
                    3
                } else {
                    if (value < 10000) {
                        4
                    } else {
                        5
                    }
                }
            }
        } else {
            if (value < 10000000) {
                if (value < 1000000) {
                    6
                } else {
                    7
                }
            } else {
                if (value < 100000000) {
                    8
                } else {
                    if (value < 1000000000) {
                        9
                    } else {
                        10
                    }
                }
            }
        }
    }

    open fun put(value: Any?) {
        if (value == null) {
            write(NULL)
            return
        }

        val cls: Class<*> = value.javaClass

        if (cls.isArray) {
            val type = cls.componentType

            if (type.isPrimitive) {
                when (type) {
                    Byte::class.javaPrimitiveType -> {
                        put((value as ByteArray).contentToString())
                    }
                    Short::class.javaPrimitiveType -> {
                        put((value as ShortArray).contentToString())
                    }
                    Int::class.javaPrimitiveType -> {
                        put((value as IntArray).contentToString())
                    }
                    Long::class.javaPrimitiveType -> {
                        put((value as LongArray).contentToString())
                    }
                    Float::class.javaPrimitiveType -> {
                        put((value as FloatArray).contentToString())
                    }
                    Double::class.javaPrimitiveType -> {
                        put((value as DoubleArray).contentToString())
                    }
                    Boolean::class.javaPrimitiveType -> {
                        put((value as BooleanArray).contentToString())
                    }
                    Char::class.javaPrimitiveType -> {
                        put((value as CharArray).contentToString())
                    }
                }
            } else {
                val len = Array.getLength(value)
                write('['.toInt())
                for (i in 0 until len) {
                    if (i != 0) write(ARRAY_DELIMITER)
                    val element = Array.get(value, i)
                    put(element)
                }
                write(']'.toInt())
            }
        } else {
            put(value.toString())
        }
    }

    open fun put(d: Double) {
        put(d.toString())
    }

    open fun put(f: Float) {
        put(f.toString())
    }

    open fun put(arg: Long) {
        var value = arg
        val negative = value < 0

        if (negative) {
            write('-'.toInt())
            value = -value
        }
        val length = longLength(value)
        for (i in length - 1 downTo 0) {
            writeDigit((value / LONG_UNITS[i] % 10L).toInt())
        }
    }

    open fun put(arg: Int) {
        var value = arg

        if (value == 0) {
            write('0'.toInt())
            return
        }

        val negative = value < 0

        if (negative) {
            write('-'.toInt())
            value = -value
        }
        val length = intLength(value)
        for (i in length - 1 downTo 0) {
            writeDigit(value / INT_UNITS[i] % 10)
        }
    }

    private fun writeDigit(digit: Int) {
        write('0'.toInt() + digit)
    }

    abstract fun write(bytes: ByteArray)
    abstract fun write(bytes: ByteArray, off: Int, len: Int)
    abstract fun flush()
    abstract fun close()
    abstract fun write(ch: Int)

    companion object {
        private val ARRAY_DELIMITER = ", ".toByteArray()
        private val NULL = "null".toByteArray()

        val INT_UNITS = intArrayOf(
            1, 10, 100,
            1000, 10000, 100000,
            1000000, 10000000, 100000000,
            1000000000
        )
        val LONG_UNITS = longArrayOf(
            1L, 10L, 100L,
            1000L, 10000L, 100000L,
            1000000L, 10000000L, 100000000L,
            1000000000L, 10000000000L, 100000000000L,
            1000000000000L, 10000000000000L, 100000000000000L,
            1000000000000000L, 10000000000000000L, 100000000000000000L,
            1000000000000000000L
        )
    }
}