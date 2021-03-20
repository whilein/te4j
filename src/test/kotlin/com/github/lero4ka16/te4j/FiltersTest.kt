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
package com.github.lero4ka16.te4j

import com.github.lero4ka16.te4j.filter.impl.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * @author lero4ka16
 */
class FiltersTest {
    @Test
    fun testByteShuffle() {
        assertFalse(SORTED_BYTE_ARRAY.contentEquals(Shuffle.process(SORTED_BYTE_ARRAY)))
    }

    @Test
    fun testShortShuffle() {
        assertFalse(SORTED_SHORT_ARRAY.contentEquals(Shuffle.process(SORTED_SHORT_ARRAY)))
    }

    @Test
    fun testIntShuffle() {
        assertFalse(SORTED_INT_ARRAY.contentEquals(Shuffle.process(SORTED_INT_ARRAY)))
    }

    @Test
    fun testLongShuffle() {
        assertFalse(SORTED_LONG_ARRAY.contentEquals(Shuffle.process(SORTED_LONG_ARRAY)))
    }

    @Test
    fun testDoubleShuffle() {
        assertFalse(SORTED_DOUBLE_ARRAY.contentEquals(Shuffle.process(SORTED_DOUBLE_ARRAY)))
    }

    @Test
    fun testFloatShuffle() {
        assertFalse(SORTED_FLOAT_ARRAY.contentEquals(Shuffle.process(SORTED_FLOAT_ARRAY)))
    }

    @Test
    fun testCharShuffle() {
        assertFalse(SORTED_CHAR_ARRAY.contentEquals(Shuffle.process(SORTED_CHAR_ARRAY)))
    }

    @Test
    fun testObjectShuffle() {
        assertFalse(SORTED_OBJECT_ARRAY.contentEquals(Shuffle.process(SORTED_OBJECT_ARRAY)))
    }

    @Test
    fun testObjectListShuffle() {
        assertNotEquals(Shuffle.process(SORTED_OBJECT_LIST), SORTED_OBJECT_LIST)
    }

    @Test
    fun testByteMin() {
        assertEquals(1, Min.process(SHUFFLED_BYTE_ARRAY))
    }

    @Test
    fun testShortMin() {
        assertEquals(1, Min.process(SHUFFLED_SHORT_ARRAY))
    }

    @Test
    fun testIntMin() {
        assertEquals(1, Min.process(SHUFFLED_INT_ARRAY))
    }

    @Test
    fun testLongMin() {
        assertEquals(1, Min.process(SHUFFLED_LONG_ARRAY))
    }

    @Test
    fun testFloatMin() {
        assertEquals(1.0F, Min.process(SHUFFLED_FLOAT_ARRAY))
    }

    @Test
    fun testDoubleMin() {
        assertEquals(1.0, Min.process(SHUFFLED_DOUBLE_ARRAY))
    }

    @Test
    fun testCharMin() {
        assertEquals(1, Min.process(SHUFFLED_CHAR_ARRAY).toInt())
    }

    @Test
    fun testObjectMin() {
        assertEquals(1, Min.process(SHUFFLED_OBJECT_ARRAY))
    }

    @Test
    fun testObjectListMin() {
        assertEquals(1, Min.process(SHUFFLED_OBJECT_LIST))
    }

    @Test
    fun testByteSum() {
        assertEquals(15.0, Sum.process(SHUFFLED_BYTE_ARRAY))
    }

    @Test
    fun testShortSum() {
        assertEquals(15.0, Sum.process(SHUFFLED_SHORT_ARRAY))
    }

    @Test
    fun testIntSum() {
        assertEquals(15.0, Sum.process(SHUFFLED_INT_ARRAY))
    }

    @Test
    fun testLongSum() {
        assertEquals(15.0, Sum.process(SHUFFLED_LONG_ARRAY))
    }

    @Test
    fun testFloatSum() {
        assertEquals(15.0, Sum.process(SHUFFLED_FLOAT_ARRAY))
    }

    @Test
    fun testDoubleSum() {
        assertEquals(15.0, Sum.process(SHUFFLED_DOUBLE_ARRAY))
    }

    @Test
    fun testObjectSum() {
        assertEquals(15.0, Sum.process(SHUFFLED_OBJECT_ARRAY))
    }

    @Test
    fun testObjectListSum() {
        assertEquals(15.0, Sum.process(SHUFFLED_OBJECT_LIST))
    }

    @Test
    fun testByteAverage() {
        assertEquals(3.0, Average.process(SHUFFLED_BYTE_ARRAY))
    }

    @Test
    fun testShortAverage() {
        assertEquals(3.0, Average.process(SHUFFLED_SHORT_ARRAY))
    }

    @Test
    fun testIntAverage() {
        assertEquals(3.0, Average.process(SHUFFLED_INT_ARRAY))
    }

    @Test
    fun testLongAverage() {
        assertEquals(3.0, Average.process(SHUFFLED_LONG_ARRAY))
    }

    @Test
    fun testFloatAverage() {
        assertEquals(3.0, Average.process(SHUFFLED_FLOAT_ARRAY))
    }

    @Test
    fun testDoubleAverage() {
        assertEquals(3.0, Average.process(SHUFFLED_DOUBLE_ARRAY))
    }

    @Test
    fun testObjectAverage() {
        assertEquals(3.0, Average.process(SHUFFLED_OBJECT_ARRAY))
    }

    @Test
    fun testObjectListAverage() {
        assertEquals(3.0, Average.process(SHUFFLED_OBJECT_LIST))
    }

    @Test
    fun testByteMax() {
        assertEquals(5, Max.process(SHUFFLED_BYTE_ARRAY))
    }

    @Test
    fun testShortMax() {
        assertEquals(5, Max.process(SHUFFLED_SHORT_ARRAY))
    }

    @Test
    fun testIntMax() {
        assertEquals(5, Max.process(SHUFFLED_INT_ARRAY))
    }

    @Test
    fun testLongMax() {
        assertEquals(5, Max.process(SHUFFLED_LONG_ARRAY))
    }

    @Test
    fun testFloatMax() {
        assertEquals(5.0F, Max.process(SHUFFLED_FLOAT_ARRAY))
    }

    @Test
    fun testDoubleMax() {
        assertEquals(5.0, Max.process(SHUFFLED_DOUBLE_ARRAY))
    }

    @Test
    fun testCharMax() {
        assertEquals(5, Max.process(SHUFFLED_CHAR_ARRAY).toInt())
    }

    @Test
    fun testObjectMax() {
        assertEquals(5, Max.process(SHUFFLED_OBJECT_ARRAY))
    }

    @Test
    fun testObjectListMax() {
        assertEquals(5, Max.process(SHUFFLED_OBJECT_LIST))
    }

    @Test
    fun testByteSort() {
        assertArrayEquals(SORTED_BYTE_ARRAY, Sort.process(SHUFFLED_BYTE_ARRAY))
    }

    @Test
    fun testShortSort() {
        assertArrayEquals(SORTED_SHORT_ARRAY, Sort.process(SHUFFLED_SHORT_ARRAY))
    }

    @Test
    fun testIntSort() {
        assertArrayEquals(SORTED_INT_ARRAY, Sort.process(SHUFFLED_INT_ARRAY))
    }

    @Test
    fun testLongSort() {
        assertArrayEquals(SORTED_LONG_ARRAY, Sort.process(SHUFFLED_LONG_ARRAY))
    }

    @Test
    fun testDoubleSort() {
        assertArrayEquals(SORTED_DOUBLE_ARRAY, Sort.process(SHUFFLED_DOUBLE_ARRAY))
    }

    @Test
    fun testFloatSort() {
        assertArrayEquals(SORTED_FLOAT_ARRAY, Sort.process(SHUFFLED_FLOAT_ARRAY))
    }

    @Test
    fun testCharSort() {
        assertArrayEquals(SORTED_CHAR_ARRAY, Sort.process(SHUFFLED_CHAR_ARRAY))
    }

    @Test
    fun testObjectSort() {
        assertArrayEquals(SORTED_OBJECT_ARRAY, Sort.process(SHUFFLED_OBJECT_ARRAY))
    }

    @Test
    fun testObjectListSort() {
        assertEquals(Sort.process(SHUFFLED_OBJECT_LIST), SORTED_OBJECT_LIST)
    }

    @Test
    fun testCapitalize() {
        assertEquals("Hello world", Capitalize.process("hello world"))
        assertEquals("~hello world", Capitalize.process("~hello world"))
        assertEquals("Привет мир", Capitalize.process("Привет мир"))
    }

    @Test
    fun testEscapeTags() {
        assertEquals("&#60;a&#62;Hello world&#60;/a&#62;", EscapeTags.process("<a>Hello world</a>"))
    }

    @Test
    fun testStripTags() {
        assertEquals("Hello", StripTags.process("<a>Hello</a>"))
    }

    companion object {
        private val SORTED_BYTE_ARRAY = byteArrayOf(1, 2, 3, 4, 5)
        private val SORTED_SHORT_ARRAY = shortArrayOf(1, 2, 3, 4, 5)
        private val SORTED_INT_ARRAY = intArrayOf(1, 2, 3, 4, 5)
        private val SORTED_FLOAT_ARRAY = floatArrayOf(1f, 2f, 3f, 4f, 5f)
        private val SORTED_DOUBLE_ARRAY = doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0)
        private val SORTED_LONG_ARRAY = longArrayOf(1, 2, 3, 4, 5)
        private val SORTED_CHAR_ARRAY = charArrayOf(1.toChar(), 2.toChar(), 3.toChar(), 4.toChar(), 5.toChar())
        private val SORTED_OBJECT_ARRAY = arrayOf(1, 2, 3, 4, 5)
        private val SORTED_OBJECT_LIST = listOf(1, 2, 3, 4, 5)
        private val SHUFFLED_BYTE_ARRAY = byteArrayOf(3, 4, 1, 5, 2)
        private val SHUFFLED_SHORT_ARRAY = shortArrayOf(3, 4, 1, 5, 2)
        private val SHUFFLED_INT_ARRAY = intArrayOf(3, 4, 1, 5, 2)
        private val SHUFFLED_FLOAT_ARRAY = floatArrayOf(3f, 4f, 1f, 5f, 2f)
        private val SHUFFLED_DOUBLE_ARRAY = doubleArrayOf(3.0, 4.0, 1.0, 5.0, 2.0)
        private val SHUFFLED_LONG_ARRAY = longArrayOf(3, 4, 1, 5, 2)
        private val SHUFFLED_CHAR_ARRAY = charArrayOf(3.toChar(), 4.toChar(), 1.toChar(), 5.toChar(), 2.toChar())
        private val SHUFFLED_OBJECT_ARRAY = arrayOf(3, 4, 1, 5, 2)
        private val SHUFFLED_OBJECT_LIST = listOf(3, 4, 1, 5, 2)
    }
}