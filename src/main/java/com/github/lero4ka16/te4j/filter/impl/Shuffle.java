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

package com.github.lero4ka16.te4j.filter.impl;

import com.github.lero4ka16.te4j.filter.Filter;
import com.github.lero4ka16.te4j.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author lero4ka16
 */
public final class Shuffle implements Filter {

    private static final Random RANDOM = Utils.isJUnitTest() ? new Random(1) : new Random();

    @Override
    public String getName() {
        return "shuffle";
    }

    public static Object[] process(Object[] value) {
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

    public static byte[] process(byte[] value) {
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

    public static short[] process(short[] value) {
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

    public static int[] process(int[] value) {
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

    public static long[] process(long[] value) {
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


    public static float[] process(float[] value) {
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

    public static double[] process(double[] value) {
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

    public static char[] process(char[] value) {
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
    public static Collection<Object> process(Collection<Object> value) {
        if (value.isEmpty()) return value;
        List collection = new ArrayList<>(value);
        Collections.shuffle(collection, RANDOM);

        return collection;
    }


    
}
