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

package com.github.lero4ka16.te4j.filter;

import com.github.lero4ka16.te4j.util.Utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lero4ka16
 */
public class Filters {

    private static final String FILTERS_CLASS = Filters.class.getName();
    private static final String UTILS_CLASS = Utils.class.getName();

    private static final Random RANDOM = Utils.isJUnitTest() ? new Random(1) : new Random();

    private final Map<String, Filter> filters = new ConcurrentHashMap<>();

    public Filters() {
        addFilter("upper", value -> value + ".toUpperCase()");
        addFilter("lower", value -> value + ".toLowerCase()");
        addFilter("capitalize", value -> FILTERS_CLASS + ".capitalize(" + value + ")");
        addFilter("striptags", value -> FILTERS_CLASS + ".striptags(" + value + ")");
        addFilter("trim", value -> value + ".trim()");
        addFilter("sorted", value -> FILTERS_CLASS + ".sort(" + value + ")");
        addFilter("double", value -> "(double) " + value);
        addFilter("int", value -> "(int) " + value);
        addFilter("char", value -> "(char) " + value);
        addFilter("byte", value -> "(byte) " + value);
        addFilter("short", value -> "(short) " + value);
        addFilter("float", value -> "(float) " + value);
        addFilter("long", value -> "(long) " + value);
        addFilter("double", value -> "(double) " + value);
        addFilter("wrap", value -> FILTERS_CLASS + ".wrap(" + value + ")");
        addFilter("floor", value -> FILTERS_CLASS + ".floor(" + value + ")");
        addFilter("ceil", value -> FILTERS_CLASS + ".ceil(" + value + ")");
        addFilter("round", value -> FILTERS_CLASS + ".round(" + value + ")");
        addFilter("sum", value -> FILTERS_CLASS + ".sum(" + value + ")");
        addFilter("max", value -> FILTERS_CLASS + ".max(" + value + ")");
        addFilter("min", value -> FILTERS_CLASS + ".min(" + value + ")");
        addFilter("average", value -> FILTERS_CLASS + ".average(" + value + ")");
        addFilter("hex", value -> UTILS_CLASS + ".toHexString(" + value + ")");
        addFilter("escapetags", value -> UTILS_CLASS + ".escapeTags(" + value + ")");
    }

    public Filter getFilter(String name) {
        return filters.get(name);
    }

    public void addFilter(String name, Filter value) {
        filters.put(name, value);
    }

    public String applyFilters(String filters, String value) {
        if (filters == null) {
            return value;
        }

        String[] filterArray = filters.split(":");

        for (String filterName : filterArray) {
            Filter filter = getFilter(filterName);

            if (filter == null) {
                throw new IllegalStateException("Filter not found: " + filterName);
            }

            value = filter.wrap(value);
        }

        return value;
    }

    public static float round(float value) {
        return Math.round(value);
    }

    public static double round(double value) {
        return Math.round(value);
    }

    public static double ceil(double value) {
        return Math.ceil(value);
    }

    public static double floor(double value) {
        return Math.floor(value);
    }

    public static Byte wrap(byte i) {
        return i;
    }

    public static Character wrap(char i) {
        return i;
    }

    public static Boolean wrap(boolean i) {
        return i;
    }

    public static Short wrap(short i) {
        return i;
    }

    public static Double wrap(double i) {
        return i;
    }

    public static Integer wrap(int i) {
        return i;
    }

    public static Long wrap(long i) {
        return i;
    }

    public static Float wrap(float i) {
        return i;
    }

    public static Object[] sort(Object[] value) {
        if (value.length == 0) return value;
        Object[] result = Arrays.copyOf(value, value.length);
        Arrays.sort(result);

        return result;
    }

    public static byte[] sort(byte[] value) {
        if (value.length == 0) return value;
        byte[] result = Arrays.copyOf(value, value.length);
        Arrays.sort(result);

        return result;
    }

    public static short[] sort(short[] value) {
        if (value.length == 0) return value;
        short[] result = Arrays.copyOf(value, value.length);
        Arrays.sort(result);

        return result;
    }

    public static int[] sort(int[] value) {
        if (value.length == 0) return value;
        int[] result = Arrays.copyOf(value, value.length);
        Arrays.sort(result);

        return result;
    }

    public static long[] sort(long[] value) {
        if (value.length == 0) return value;
        long[] result = Arrays.copyOf(value, value.length);
        Arrays.sort(result);

        return result;
    }


    public static float[] sort(float[] value) {
        if (value.length == 0) return value;
        float[] result = Arrays.copyOf(value, value.length);
        Arrays.sort(result);

        return result;
    }

    public static double[] sort(double[] value) {
        if (value.length == 0) return value;
        double[] result = Arrays.copyOf(value, value.length);
        Arrays.sort(result);

        return result;
    }

    public static char[] sort(char[] value) {
        if (value.length == 0) return value;
        char[] result = Arrays.copyOf(value, value.length);
        Arrays.sort(result);

        return result;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Collection<?> sort(Collection<?> value) {
        if (value.isEmpty()) return value;
        List collection = new ArrayList<>(value);
        Collections.sort(collection);

        return collection;
    }


    public static Object[] shuffle(Object[] value) {
        if (value.length == 0) return value;

        Object[] result = Arrays.copyOf(value, value.length);

        for (int i = value.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);

            Object element = result[j];
            result[j] = result[i];
            result[i] = element;
        }

        return result;
    }

    public static byte[] shuffle(byte[] value) {
        if (value.length == 0) return value;

        byte[] result = Arrays.copyOf(value, value.length);

        for (int i = value.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);

            byte element = result[j];
            result[j] = result[i];
            result[i] = element;
        }

        return result;
    }

    public static short[] shuffle(short[] value) {
        if (value.length == 0) return value;

        short[] result = Arrays.copyOf(value, value.length);

        for (int i = value.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);

            short element = result[j];
            result[j] = result[i];
            result[i] = element;
        }

        return result;
    }

    public static int[] shuffle(int[] value) {
        if (value.length == 0) return value;

        int[] result = Arrays.copyOf(value, value.length);

        for (int i = value.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);

            int element = result[j];
            result[j] = result[i];
            result[i] = element;
        }

        return result;
    }

    public static long[] shuffle(long[] value) {
        if (value.length == 0) return value;

        long[] result = Arrays.copyOf(value, value.length);

        for (int i = value.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);

            long element = result[j];
            result[j] = result[i];
            result[i] = element;
        }

        return result;
    }


    public static float[] shuffle(float[] value) {
        if (value.length == 0) return value;

        float[] result = Arrays.copyOf(value, value.length);

        for (int i = value.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);

            float element = result[j];
            result[j] = result[i];
            result[i] = element;
        }

        return result;
    }

    public static double[] shuffle(double[] value) {
        if (value.length == 0) return value;

        double[] result = Arrays.copyOf(value, value.length);

        for (int i = value.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);

            double element = result[j];
            result[j] = result[i];
            result[i] = element;
        }

        return result;
    }

    public static char[] shuffle(char[] value) {
        if (value.length == 0) return value;

        char[] result = Arrays.copyOf(value, value.length);

        for (int i = value.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);

            char element = result[j];
            result[j] = result[i];
            result[i] = element;
        }

        return result;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Collection<Object> shuffle(Collection<Object> value) {
        if (value.isEmpty()) return value;
        List collection = new ArrayList<>(value);
        Collections.shuffle(collection, RANDOM);

        return collection;
    }

    public static double average(byte[] value) {
        return value.length == 0 ? 0 : sum(value) / value.length;
    }

    public static double average(short[] value) {
        return value.length == 0 ? 0 : sum(value) / value.length;
    }

    public static double average(int[] value) {
        return value.length == 0 ? 0 : sum(value) / value.length;
    }

    public static double average(long[] value) {
        return value.length == 0 ? 0 : sum(value) / value.length;
    }

    public static double average(float[] value) {
        return value.length == 0 ? 0 : sum(value) / value.length;
    }

    public static double average(double[] value) {
        return value.length == 0 ? 0 : sum(value) / value.length;
    }

    public static double average(Object[] value) {
        return value.length == 0 ? 0 : sum(value) / value.length;
    }

    public static double average(Collection<?> value) {
        return value.isEmpty() ? 0 : sum(value) / value.size();
    }

    public static char max(char[] value) {
        if (value.length == 0) return 0;

        char res = value[0];

        for (int i = 1; i < value.length; i++) {
            if (res < value[i]) {
                res = value[i];
            }
        }

        return res;
    }


    public static byte max(byte[] value) {
        if (value.length == 0) return 0;

        byte res = value[0];

        for (int i = 1; i < value.length; i++) {
            if (res < value[i]) {
                res = value[i];
            }
        }

        return res;
    }

    public static short max(short[] value) {
        if (value.length == 0) return 0;

        short res = value[0];

        for (int i = 1; i < value.length; i++) {
            if (res < value[i]) {
                res = value[i];
            }
        }

        return res;
    }

    public static int max(int[] value) {
        if (value.length == 0) return 0;

        int res = value[0];

        for (int i = 1; i < value.length; i++) {
            if (res < value[i]) {
                res = value[i];
            }
        }

        return res;
    }

    public static long max(long[] value) {
        if (value.length == 0) return 0;

        long res = value[0];

        for (int i = 1; i < value.length; i++) {
            if (res < value[i]) {
                res = value[i];
            }
        }

        return res;
    }

    public static float max(float[] value) {
        if (value.length == 0) return 0;

        float res = value[0];

        for (int i = 1; i < value.length; i++) {
            if (res < value[i]) {
                res = value[i];
            }
        }

        return res;
    }

    public static double max(double[] value) {
        if (value.length == 0) return 0;

        double res = value[0];

        for (int i = 1; i < value.length; i++) {
            if (res < value[i]) {
                res = value[i];
            }
        }

        return res;
    }

    public static char min(char[] value) {
        if (value.length == 0) return 0;

        char res = value[0];

        for (int i = 1; i < value.length; i++) {
            if (res > value[i]) {
                res = value[i];
            }
        }

        return res;
    }

    public static byte min(byte[] value) {
        if (value.length == 0) return 0;

        byte res = value[0];

        for (int i = 1; i < value.length; i++) {
            if (res > value[i]) {
                res = value[i];
            }
        }

        return res;
    }

    public static short min(short[] value) {
        if (value.length == 0) return 0;

        short res = value[0];

        for (int i = 1; i < value.length; i++) {
            if (res > value[i]) {
                res = value[i];
            }
        }

        return res;
    }

    public static int min(int[] value) {
        if (value.length == 0) return 0;

        int res = value[0];

        for (int i = 1; i < value.length; i++) {
            if (res > value[i]) {
                res = value[i];
            }
        }

        return res;
    }

    public static long min(long[] value) {
        if (value.length == 0) return 0;

        long res = value[0];

        for (int i = 1; i < value.length; i++) {
            if (res > value[i]) {
                res = value[i];
            }
        }

        return res;
    }

    public static float min(float[] value) {
        if (value.length == 0) return 0;

        float res = value[0];

        for (int i = 1; i < value.length; i++) {
            if (res > value[i]) {
                res = value[i];
            }
        }

        return res;
    }

    public static double min(double[] value) {
        if (value.length == 0) return 0;

        double res = value[0];

        for (int i = 1; i < value.length; i++) {
            if (res > value[i]) {
                res = value[i];
            }
        }

        return res;
    }

    public static <N extends Number & Comparable<N>> Number min(Iterable<N> value) {
        Iterator<N> iterator = value.iterator();
        if (!iterator.hasNext()) return BigDecimal.ZERO;

        N res = iterator.next();

        while (iterator.hasNext()) {
            N that = iterator.next();

            if (res.compareTo(that) > 0) {
                res = that;
            }
        }

        return res;
    }

    public static <N extends Number & Comparable<N>> Number min(N[] value) {
        if (value.length == 0) return BigDecimal.ZERO;

        N res = value[0];

        for (int i = 1; i < value.length; i++) {
            N that = value[i];

            if (res.compareTo(that) > 0) {
                res = that;
            }
        }

        return res;
    }

    public static <N extends Number & Comparable<N>> Number max(Iterable<N> value) {
        Iterator<N> iterator = value.iterator();
        if (!iterator.hasNext()) return BigDecimal.ZERO;

        N res = iterator.next();

        while (iterator.hasNext()) {
            N that = iterator.next();

            if (res.compareTo(that) < 0) {
                res = that;
            }
        }

        return res;
    }

    public static <N extends Number & Comparable<N>> Number max(N[] value) {
        if (value.length == 0) return BigDecimal.ZERO;

        N res = value[0];

        for (int i = 1; i < value.length; i++) {
            N that = value[i];

            if (res.compareTo(that) < 0) {
                res = that;
            }
        }

        return res;
    }

    public static double sum(byte[] value) {
        double res = 0;

        for (byte i : value) {
            res += i;
        }

        return res;
    }

    public static double sum(short[] value) {
        double res = 0;

        for (short i : value) {
            res += i;
        }

        return res;
    }

    public static double sum(int[] value) {
        double res = 0;

        for (int i : value) {
            res += i;
        }

        return res;
    }

    public static double sum(long[] value) {
        double res = 0;

        for (long i : value) {
            res += i;
        }

        return res;
    }

    public static double sum(float[] value) {
        double res = 0;

        for (float i : value) {
            res += i;
        }

        return res;
    }

    public static double sum(double[] value) {
        double res = 0;

        for (double i : value) {
            res += i;
        }

        return res;
    }

    public static double sum(Iterable<?> value) {
        double res = 0;

        for (Object object : value) {
            res += ((Number) object).doubleValue();
        }

        return res;
    }

    public static double sum(Object[] value) {
        double res = 0;

        for (Object object : value) {
            res += ((Number) object).doubleValue();
        }

        return res;
    }

    public static String striptags(String value) {
        return Utils.stripTags(value);
    }

    public static String escapetags(String value) {
        return Utils.escapeTags(value);
    }

    public static String capitalize(String value) {
        char ch = value.charAt(0);
        char mod = Character.toTitleCase(ch);

        if (ch == mod) return value;

        char[] chars = value.toCharArray();
        chars[0] = mod;

        return new String(chars);
    }

}
