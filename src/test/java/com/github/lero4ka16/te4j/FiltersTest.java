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

import com.github.lero4ka16.te4j.filter.impl.Average;
import com.github.lero4ka16.te4j.filter.impl.Capitalize;
import com.github.lero4ka16.te4j.filter.impl.EscapeTags;
import com.github.lero4ka16.te4j.filter.impl.Max;
import com.github.lero4ka16.te4j.filter.impl.Min;
import com.github.lero4ka16.te4j.filter.impl.Shuffle;
import com.github.lero4ka16.te4j.filter.impl.Sort;
import com.github.lero4ka16.te4j.filter.impl.Sum;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * @author lero4ka16
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

    public <T> void testInTemplate(T pojo, Class<T> cls, String text, String expect, boolean notExpect) {
        String actual = Te4j.loadString(cls, text).renderAsString(pojo);

        if (notExpect) {
            assertNotEquals(expect, actual);
        } else {
            assertEquals(expect, actual);
        }
    }

    public static class NumberPojo {
        private final double number;

        public NumberPojo(double number) {
            this.number = number;
        }

        public double getNumber() {
            return number;
        }
    }

    public static class StringPojo {
        private final String text;

        public StringPojo(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    public static class ArrayPojo {

        private final int[] array;

        public ArrayPojo(int[] array) {
            this.array = array;
        }

        public int[] getArray() {
            return array;
        }
    }

    @Test
    public void hexTemplate() {
        testInTemplate(new StringPojo("Hello"), StringPojo.class, "^^ text:hex ^^",
                "48656C6C6F", false);
    }

    @Test
    public void upperTemplate() {
        testInTemplate(new StringPojo("Hello"), StringPojo.class, "^^ text:upper ^^",
                "HELLO", false);
    }

    @Test
    public void lowerTemplate() {
        testInTemplate(new StringPojo("Hello"), StringPojo.class, "^^ text:lower ^^",
                "hello", false);
    }

    @Test
    public void capitalizeTemplate() {
        testInTemplate(new StringPojo("not capitalized"), StringPojo.class, "^^ text:capitalize ^^",
                "Not capitalized", false);
    }

    @Test
    public void trimTemplate() {
        testInTemplate(new StringPojo("  12345  "), StringPojo.class, "^^ text:trim ^^",
                "12345", false);
    }

    @Test
    public void maxTemplate() {
        testInTemplate(new ArrayPojo(SORTED_INT_ARRAY), ArrayPojo.class, "^^ array:max ^^",
                "5", false);
    }

    @Test
    public void striptagsTemplate() {
        testInTemplate(new StringPojo("<a>Message</a>"), StringPojo.class, "^^ text:striptags ^^",
                "Message", false);
    }

    @Test
    public void escapetagsTemplate() {
        testInTemplate(new StringPojo("<a>Message</a>"), StringPojo.class, "^^ text:escapetags ^^",
                "&#60;a&#62;Message&#60;/a&#62;", false);
    }

    @Test
    public void sumTemplate() {
        testInTemplate(new ArrayPojo(SORTED_INT_ARRAY), ArrayPojo.class, "^^ array:sum ^^",
                "15", false);
    }

    @Test
    public void averageTemplate() {
        testInTemplate(new ArrayPojo(SORTED_INT_ARRAY), ArrayPojo.class, "^^ array:average:int ^^",
                "3", false);
    }

    @Test
    public void minTemplate() {
        testInTemplate(new ArrayPojo(SORTED_INT_ARRAY), ArrayPojo.class, "^^ array:min ^^",
                "1", false);
    }

    @Test
    public void floorTemplate() {
        testInTemplate(new NumberPojo(5), NumberPojo.class, "^^ number:floor ^^",
                "5.0", false);
        testInTemplate(new NumberPojo(5.01), NumberPojo.class, "^^ number:floor ^^",
                "5.0", false);
    }

    @Test
    public void ceilTemplate() {
        testInTemplate(new NumberPojo(5), NumberPojo.class, "^^ number:ceil ^^",
                "5.0", false);
        testInTemplate(new NumberPojo(5.01), NumberPojo.class, "^^ number:ceil ^^",
                "6.0", false);
    }

    @Test
    public void roundTemplate() {
        testInTemplate(new NumberPojo(5.1), NumberPojo.class, "^^ number:round ^^",
                "5", false);
        testInTemplate(new NumberPojo(5.5), NumberPojo.class, "^^ number:round ^^",
                "6", false);
    }

    @Test
    public void sortTemplate() {
        testInTemplate(new ArrayPojo(SHUFFLED_INT_ARRAY), ArrayPojo.class, "^^ array:sorted:tostr ^^",
                "[1, 2, 3, 4, 5]", false);
    }

    @Test
    public void shuffleTemplate() {
        testInTemplate(new ArrayPojo(SORTED_INT_ARRAY), ArrayPojo.class, "^^ array:shuffle:tostr ^^",
                "[1, 2, 3, 4, 5]", true);
    }

    @Test
    public void testByteShuffle() {
        assertFalse(Arrays.equals(SORTED_BYTE_ARRAY, Shuffle.process(SORTED_BYTE_ARRAY)));
    }

    @Test
    public void testShortShuffle() {
        assertFalse(Arrays.equals(SORTED_SHORT_ARRAY, Shuffle.process(SORTED_SHORT_ARRAY)));
    }

    @Test
    public void testIntShuffle() {
        assertFalse(Arrays.equals(SORTED_INT_ARRAY, Shuffle.process(SORTED_INT_ARRAY)));
    }

    @Test
    public void testLongShuffle() {
        assertFalse(Arrays.equals(SORTED_LONG_ARRAY, Shuffle.process(SORTED_LONG_ARRAY)));
    }

    @Test
    public void testDoubleShuffle() {
        assertFalse(Arrays.equals(SORTED_DOUBLE_ARRAY, Shuffle.process(SORTED_DOUBLE_ARRAY)));
    }

    @Test
    public void testFloatShuffle() {
        assertFalse(Arrays.equals(SORTED_FLOAT_ARRAY, Shuffle.process(SORTED_FLOAT_ARRAY)));
    }

    @Test
    public void testCharShuffle() {
        assertFalse(Arrays.equals(SORTED_CHAR_ARRAY, Shuffle.process(SORTED_CHAR_ARRAY)));
    }

    @Test
    public void testObjectShuffle() {
        assertFalse(Arrays.equals(SORTED_OBJECT_ARRAY, Shuffle.process(SORTED_OBJECT_ARRAY)));
    }

    @Test
    public void testObjectListShuffle() {
        assertNotEquals(Shuffle.process(SORTED_OBJECT_LIST), SORTED_OBJECT_LIST);
    }

    @Test
    public void testByteMin() {
        assertEquals(1, Min.process(SHUFFLED_BYTE_ARRAY));
    }

    @Test
    public void testShortMin() {
        assertEquals(1, Min.process(SHUFFLED_SHORT_ARRAY));
    }

    @Test
    public void testIntMin() {
        assertEquals(1, Min.process(SHUFFLED_INT_ARRAY));
    }

    @Test
    public void testLongMin() {
        assertEquals(1, Min.process(SHUFFLED_LONG_ARRAY));
    }

    @Test
    public void testFloatMin() {
        assertEquals(1, Min.process(SHUFFLED_FLOAT_ARRAY));
    }

    @Test
    public void testDoubleMin() {
        assertEquals(1, Min.process(SHUFFLED_DOUBLE_ARRAY));
    }

    @Test
    public void testCharMin() {
        assertEquals(1, Min.process(SHUFFLED_CHAR_ARRAY));
    }

    @Test
    public void testObjectMin() {
        assertEquals(1, Min.process(SHUFFLED_OBJECT_ARRAY));
    }

    @Test
    public void testObjectListMin() {
        assertEquals(1, Min.process(SHUFFLED_OBJECT_LIST));
    }

    @Test
    public void testByteSum() {
        assertEquals(15, Sum.process(SHUFFLED_BYTE_ARRAY));
    }

    @Test
    public void testShortSum() {
        assertEquals(15, Sum.process(SHUFFLED_SHORT_ARRAY));
    }

    @Test
    public void testIntSum() {
        assertEquals(15, Sum.process(SHUFFLED_INT_ARRAY));
    }

    @Test
    public void testLongSum() {
        assertEquals(15, Sum.process(SHUFFLED_LONG_ARRAY));
    }

    @Test
    public void testFloatSum() {
        assertEquals(15, Sum.process(SHUFFLED_FLOAT_ARRAY));
    }

    @Test
    public void testDoubleSum() {
        assertEquals(15, Sum.process(SHUFFLED_DOUBLE_ARRAY));
    }

    @Test
    public void testObjectSum() {
        assertEquals(15, Sum.process(SHUFFLED_OBJECT_ARRAY));
    }

    @Test
    public void testObjectListSum() {
        assertEquals(15, Sum.process(SHUFFLED_OBJECT_LIST));
    }

    @Test
    public void testByteAverage() {
        assertEquals(3, Average.process(SHUFFLED_BYTE_ARRAY));
    }

    @Test
    public void testShortAverage() {
        assertEquals(3, Average.process(SHUFFLED_SHORT_ARRAY));
    }

    @Test
    public void testIntAverage() {
        assertEquals(3, Average.process(SHUFFLED_INT_ARRAY));
    }

    @Test
    public void testLongAverage() {
        assertEquals(3, Average.process(SHUFFLED_LONG_ARRAY));
    }

    @Test
    public void testFloatAverage() {
        assertEquals(3, Average.process(SHUFFLED_FLOAT_ARRAY));
    }

    @Test
    public void testDoubleAverage() {
        assertEquals(3, Average.process(SHUFFLED_DOUBLE_ARRAY));
    }

    @Test
    public void testObjectAverage() {
        assertEquals(3, Average.process(SHUFFLED_OBJECT_ARRAY));
    }

    @Test
    public void testObjectListAverage() {
        assertEquals(3, Average.process(SHUFFLED_OBJECT_LIST));
    }

    @Test
    public void testByteMax() {
        assertEquals(5, Max.process(SHUFFLED_BYTE_ARRAY));
    }

    @Test
    public void testShortMax() {
        assertEquals(5, Max.process(SHUFFLED_SHORT_ARRAY));
    }

    @Test
    public void testIntMax() {
        assertEquals(5, Max.process(SHUFFLED_INT_ARRAY));
    }

    @Test
    public void testLongMax() {
        assertEquals(5, Max.process(SHUFFLED_LONG_ARRAY));
    }

    @Test
    public void testFloatMax() {
        assertEquals(5, Max.process(SHUFFLED_FLOAT_ARRAY));
    }

    @Test
    public void testDoubleMax() {
        assertEquals(5, Max.process(SHUFFLED_DOUBLE_ARRAY));
    }

    @Test
    public void testCharMax() {
        assertEquals(5, Max.process(SHUFFLED_CHAR_ARRAY));
    }

    @Test
    public void testObjectMax() {
        assertEquals(5, Max.process(SHUFFLED_OBJECT_ARRAY));
    }

    @Test
    public void testObjectListMax() {
        assertEquals(5, Max.process(SHUFFLED_OBJECT_LIST));
    }

    @Test
    public void testByteSort() {
        assertArrayEquals(SORTED_BYTE_ARRAY, Sort.process(SHUFFLED_BYTE_ARRAY));
    }

    @Test
    public void testShortSort() {
        assertArrayEquals(SORTED_SHORT_ARRAY, Sort.process(SHUFFLED_SHORT_ARRAY));
    }

    @Test
    public void testIntSort() {
        assertArrayEquals(SORTED_INT_ARRAY, Sort.process(SHUFFLED_INT_ARRAY));
    }

    @Test
    public void testLongSort() {
        assertArrayEquals(SORTED_LONG_ARRAY, Sort.process(SHUFFLED_LONG_ARRAY));
    }

    @Test
    public void testDoubleSort() {
        assertArrayEquals(SORTED_DOUBLE_ARRAY, Sort.process(SHUFFLED_DOUBLE_ARRAY));
    }

    @Test
    public void testFloatSort() {
        assertArrayEquals(SORTED_FLOAT_ARRAY, Sort.process(SHUFFLED_FLOAT_ARRAY));
    }

    @Test
    public void testCharSort() {
        assertArrayEquals(SORTED_CHAR_ARRAY, Sort.process(SHUFFLED_CHAR_ARRAY));
    }

    @Test
    public void testObjectSort() {
        assertArrayEquals(SORTED_OBJECT_ARRAY, Sort.process(SHUFFLED_OBJECT_ARRAY));
    }

    @Test
    public void testObjectListSort() {
        assertEquals(Sort.process(SHUFFLED_OBJECT_LIST), SORTED_OBJECT_LIST);
    }

    @Test
    public void testCapitalize() {
        assertEquals("Hello world", Capitalize.process("hello world"));
        assertEquals("~hello world", Capitalize.process("~hello world"));
        assertEquals("Привет мир", Capitalize.process("Привет мир"));
    }

    @Test
    public void testEscapeTags() {
        assertEquals("&#60;a&#62;Hello world&#60;/a&#62;", EscapeTags.process("<a>Hello world</a>"));
    }

}
