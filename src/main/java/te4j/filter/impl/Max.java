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

package te4j.filter.impl;

import te4j.filter.Filter;

import java.util.Iterator;

/**
 * @author lero4ka16
 */
public final class Max implements Filter {
    @Override
    public String getName() {
        return "max";
    }

    public static char process(char[] value) {
        if (value.length == 0)
            throw new IllegalStateException("Input is empty!");

        char res = value[0];

        for (int i = 1; i < value.length; i++) {
            if (res < value[i]) {
                res = value[i];
            }
        }

        return res;
    }


    public static byte process(byte[] value) {
        if (value.length == 0)
            throw new IllegalStateException("Input is empty!");

        byte res = value[0];

        for (int i = 1; i < value.length; i++) {
            if (res < value[i]) {
                res = value[i];
            }
        }

        return res;
    }

    public static short process(short[] value) {
        if (value.length == 0)
            throw new IllegalStateException("Input is empty!");

        short res = value[0];

        for (int i = 1; i < value.length; i++) {
            if (res < value[i]) {
                res = value[i];
            }
        }

        return res;
    }

    public static int process(int[] value) {
        if (value.length == 0)
            throw new IllegalStateException("Input is empty!");

        int res = value[0];

        for (int i = 1; i < value.length; i++) {
            if (res < value[i]) {
                res = value[i];
            }
        }

        return res;
    }

    public static long process(long[] value) {
        if (value.length == 0)
            throw new IllegalStateException("Input is empty!");

        long res = value[0];

        for (int i = 1; i < value.length; i++) {
            if (res < value[i]) {
                res = value[i];
            }
        }

        return res;
    }

    public static float process(float[] value) {
        if (value.length == 0)
            throw new IllegalStateException("Input is empty!");

        float res = value[0];

        for (int i = 1; i < value.length; i++) {
            if (res < value[i]) {
                res = value[i];
            }
        }

        return res;
    }

    public static double process(double[] value) {
        if (value.length == 0)
            throw new IllegalStateException("Input is empty!");

        double res = value[0];

        for (int i = 1; i < value.length; i++) {
            if (res < value[i]) {
                res = value[i];
            }
        }

        return res;
    }


    public static <N extends Number & Comparable<N>> Number process(Iterable<N> value) {
        Iterator<N> iterator = value.iterator();
        if (!iterator.hasNext())
            throw new IllegalStateException("Input is empty!");

        N res = iterator.next();

        while (iterator.hasNext()) {
            N that = iterator.next();

            if (res.compareTo(that) < 0) {
                res = that;
            }
        }

        return res;
    }

    public static <N extends Number & Comparable<N>> Number process(N[] value) {
        if (value.length == 0)
            throw new IllegalStateException("Input is empty!");

        N res = value[0];

        for (int i = 1; i < value.length; i++) {
            N that = value[i];

            if (res.compareTo(that) < 0) {
                res = that;
            }
        }

        return res;
    }

    
}
