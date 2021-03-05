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

package com.github.lero4ka16.te4j;

import com.github.lero4ka16.te4j.template.filter.Filters;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Лера
 */
public class FiltersTest {

    private static final byte[] SORTED_BYTE_ARRAY = new byte[]{1, 2, 3, 4, 5};
    private static final short[] SORTED_SHORT_ARRAY = new short[]{1, 2, 3, 4, 5};
    private static final int[] SORTED_INT_ARRAY = new int[]{1, 2, 3, 4, 5};
    private static final float[] SORTED_FLOAT_ARRAY = new float[]{1, 2, 3, 4, 5};
    private static final double[] SORTED_DOUBLE_ARRAY = new double[]{1, 2, 3, 4, 5};
    private static final long[] SORTED_LONG_ARRAY = new long[]{1, 2, 3, 4, 5};
    private static final char[] SORTED_CHAR_ARRAY = new char[]{1, 2, 3, 4, 5};
    private static final Object[] SORTED_OBJECT_ARRAY = new Object[]{1, 2, 3, 4, 5};
    private static final List<Object> SORTED_OBJECT_LIST = Arrays.asList(1, 2, 3, 4, 5);

    private static final byte[] SHUFFLED_BYTE_ARRAY = new byte[]{3, 4, 1, 5, 2};
    private static final short[] SHUFFLED_SHORT_ARRAY = new short[]{3, 4, 1, 5, 2};
    private static final int[] SHUFFLED_INT_ARRAY = new int[]{3, 4, 1, 5, 2};
    private static final float[] SHUFFLED_FLOAT_ARRAY = new float[]{3, 4, 1, 5, 2};
    private static final double[] SHUFFLED_DOUBLE_ARRAY = new double[]{3, 4, 1, 5, 2};
    private static final long[] SHUFFLED_LONG_ARRAY = new long[]{3, 4, 1, 5, 2};
    private static final char[] SHUFFLED_CHAR_ARRAY = new char[]{3, 4, 1, 5, 2};
    private static final Integer[] SHUFFLED_OBJECT_ARRAY = new Integer[]{3, 4, 1, 5, 2};
    private static final List<Integer> SHUFFLED_OBJECT_LIST = Arrays.asList(3, 4, 1, 5, 2);

    @Test
    public void testByteShuffle() {
        assertFalse(Arrays.equals(SORTED_BYTE_ARRAY, Filters.shuffle(SORTED_BYTE_ARRAY)));
    }

    @Test
    public void testShortShuffle() {
        assertFalse(Arrays.equals(SORTED_SHORT_ARRAY, Filters.shuffle(SORTED_SHORT_ARRAY)));
    }

    @Test
    public void testIntShuffle() {
        assertFalse(Arrays.equals(SORTED_INT_ARRAY, Filters.shuffle(SORTED_INT_ARRAY)));
    }

    @Test
    public void testLongShuffle() {
        assertFalse(Arrays.equals(SORTED_LONG_ARRAY, Filters.shuffle(SORTED_LONG_ARRAY)));
    }

    @Test
    public void testDoubleShuffle() {
        assertFalse(Arrays.equals(SORTED_DOUBLE_ARRAY, Filters.shuffle(SORTED_DOUBLE_ARRAY)));
    }

    @Test
    public void testFloatShuffle() {
        assertFalse(Arrays.equals(SORTED_FLOAT_ARRAY, Filters.shuffle(SORTED_FLOAT_ARRAY)));
    }

    @Test
    public void testCharShuffle() {
        assertFalse(Arrays.equals(SORTED_CHAR_ARRAY, Filters.shuffle(SORTED_CHAR_ARRAY)));
    }

    @Test
    public void testObjectShuffle() {
        assertFalse(Arrays.equals(SORTED_OBJECT_ARRAY, Filters.shuffle(SORTED_OBJECT_ARRAY)));
    }

    @Test
    public void testObjectListShuffle() {
        assertNotEquals(Filters.shuffle(SORTED_OBJECT_LIST), SORTED_OBJECT_LIST);
    }

    @Test
    public void testByteMin() {
        assertEquals(1, Filters.min(SHUFFLED_BYTE_ARRAY));
    }

    @Test
    public void testShortMin() {
        assertEquals(1, Filters.min(SHUFFLED_SHORT_ARRAY));
    }

    @Test
    public void testIntMin() {
        assertEquals(1, Filters.min(SHUFFLED_INT_ARRAY));
    }

    @Test
    public void testLongMin() {
        assertEquals(1, Filters.min(SHUFFLED_LONG_ARRAY));
    }

    @Test
    public void testFloatMin() {
        assertEquals(1, Filters.min(SHUFFLED_FLOAT_ARRAY));
    }

    @Test
    public void testDoubleMin() {
        assertEquals(1, Filters.min(SHUFFLED_DOUBLE_ARRAY));
    }

    @Test
    public void testCharMin() {
        assertEquals(1, Filters.min(SHUFFLED_CHAR_ARRAY));
    }

    @Test
    public void testObjectMin() {
        assertEquals(1, Filters.min(SHUFFLED_OBJECT_ARRAY));
    }

    @Test
    public void testObjectListMin() {
        assertEquals(1, Filters.min(SHUFFLED_OBJECT_LIST));
    }

    @Test
    public void testByteSum() {
        assertEquals(15, Filters.sum(SHUFFLED_BYTE_ARRAY));
    }

    @Test
    public void testShortSum() {
        assertEquals(15, Filters.sum(SHUFFLED_SHORT_ARRAY));
    }

    @Test
    public void testIntSum() {
        assertEquals(15, Filters.sum(SHUFFLED_INT_ARRAY));
    }

    @Test
    public void testLongSum() {
        assertEquals(15, Filters.sum(SHUFFLED_LONG_ARRAY));
    }

    @Test
    public void testFloatSum() {
        assertEquals(15, Filters.sum(SHUFFLED_FLOAT_ARRAY));
    }

    @Test
    public void testDoubleSum() {
        assertEquals(15, Filters.sum(SHUFFLED_DOUBLE_ARRAY));
    }

    @Test
    public void testObjectSum() {
        assertEquals(15, Filters.sum(SHUFFLED_OBJECT_ARRAY));
    }

    @Test
    public void testObjectListSum() {
        assertEquals(15, Filters.sum(SHUFFLED_OBJECT_LIST));
    }

    @Test
    public void testByteAverage() {
        assertEquals(3, Filters.average(SHUFFLED_BYTE_ARRAY));
    }

    @Test
    public void testShortAverage() {
        assertEquals(3, Filters.average(SHUFFLED_SHORT_ARRAY));
    }

    @Test
    public void testIntAverage() {
        assertEquals(3, Filters.average(SHUFFLED_INT_ARRAY));
    }

    @Test
    public void testLongAverage() {
        assertEquals(3, Filters.average(SHUFFLED_LONG_ARRAY));
    }

    @Test
    public void testFloatAverage() {
        assertEquals(3, Filters.average(SHUFFLED_FLOAT_ARRAY));
    }

    @Test
    public void testDoubleAverage() {
        assertEquals(3, Filters.average(SHUFFLED_DOUBLE_ARRAY));
    }

    @Test
    public void testObjectAverage() {
        assertEquals(3, Filters.average(SHUFFLED_OBJECT_ARRAY));
    }

    @Test
    public void testObjectListAverage() {
        assertEquals(3, Filters.average(SHUFFLED_OBJECT_LIST));
    }

    @Test
    public void testByteMax() {
        assertEquals(5, Filters.max(SHUFFLED_BYTE_ARRAY));
    }

    @Test
    public void testShortMax() {
        assertEquals(5, Filters.max(SHUFFLED_SHORT_ARRAY));
    }

    @Test
    public void testIntMax() {
        assertEquals(5, Filters.max(SHUFFLED_INT_ARRAY));
    }

    @Test
    public void testLongMax() {
        assertEquals(5, Filters.max(SHUFFLED_LONG_ARRAY));
    }

    @Test
    public void testFloatMax() {
        assertEquals(5, Filters.max(SHUFFLED_FLOAT_ARRAY));
    }

    @Test
    public void testDoubleMax() {
        assertEquals(5, Filters.max(SHUFFLED_DOUBLE_ARRAY));
    }

    @Test
    public void testCharMax() {
        assertEquals(5, Filters.max(SHUFFLED_CHAR_ARRAY));
    }

    @Test
    public void testObjectMax() {
        assertEquals(5, Filters.max(SHUFFLED_OBJECT_ARRAY));
    }

    @Test
    public void testObjectListMax() {
        assertEquals(5, Filters.max(SHUFFLED_OBJECT_LIST));
    }

    @Test
    public void testByteSort() {
        assertArrayEquals(SORTED_BYTE_ARRAY, Filters.sort(SHUFFLED_BYTE_ARRAY));
    }

    @Test
    public void testShortSort() {
        assertArrayEquals(SORTED_SHORT_ARRAY, Filters.sort(SHUFFLED_SHORT_ARRAY));
    }

    @Test
    public void testIntSort() {
        assertArrayEquals(SORTED_INT_ARRAY, Filters.sort(SHUFFLED_INT_ARRAY));
    }

    @Test
    public void testLongSort() {
        assertArrayEquals(SORTED_LONG_ARRAY, Filters.sort(SHUFFLED_LONG_ARRAY));
    }

    @Test
    public void testDoubleSort() {
        assertArrayEquals(SORTED_DOUBLE_ARRAY, Filters.sort(SHUFFLED_DOUBLE_ARRAY));
    }

    @Test
    public void testFloatSort() {
        assertArrayEquals(SORTED_FLOAT_ARRAY, Filters.sort(SHUFFLED_FLOAT_ARRAY));
    }

    @Test
    public void testCharSort() {
        assertArrayEquals(SORTED_CHAR_ARRAY, Filters.sort(SHUFFLED_CHAR_ARRAY));
    }

    @Test
    public void testObjectSort() {
        assertArrayEquals(SORTED_OBJECT_ARRAY, Filters.sort(SHUFFLED_OBJECT_ARRAY));
    }

    @Test
    public void testObjectListSort() {
        assertEquals(Filters.sort(SHUFFLED_OBJECT_LIST), SORTED_OBJECT_LIST);
    }

    @Test
    public void testCapitalize() {
        assertEquals("Hello world", Filters.capitalize("hello world"));
        assertEquals("~hello world", Filters.capitalize("~hello world"));
        assertEquals("Привет мир", Filters.capitalize("Привет мир"));
    }

    @Test
    public void testEscapeTags() {
        assertEquals("&#60;a&#62;Hello world&#60;/a&#62;", Filters.escapetags("<a>Hello world</a>"));
    }

    @Test
    public void testStripTags() {
        assertEquals("Hello", Filters.striptags("<a>Hello</a>"));
    }

}
