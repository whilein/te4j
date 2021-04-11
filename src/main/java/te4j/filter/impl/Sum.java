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

/**
 * @author lero4ka16
 */
public final class Sum implements Filter {
    @Override
    public String getName() {
        return "sum";
    }

    public static int process(byte[] value) {
        int res = 0;

        for (byte i : value) {
            res += i;
        }

        return res;
    }

    public static int process(short[] value) {
        int res = 0;

        for (short i : value) {
            res += i;
        }

        return res;
    }

    public static int process(int[] value) {
        int res = 0;

        for (int i : value) {
            res += i;
        }

        return res;
    }

    public static long process(long[] value) {
        long res = 0;

        for (long i : value) {
            res += i;
        }

        return res;
    }

    public static double process(float[] value) {
        double res = 0;

        for (float i : value) {
            res += i;
        }

        return res;
    }

    public static double process(double[] value) {
        double res = 0;

        for (double i : value) {
            res += i;
        }

        return res;
    }

    public static double process(Iterable<?> value) {
        double res = 0;

        for (Object object : value) {
            res += ((Number) object).doubleValue();
        }

        return res;
    }

    public static double process(Object[] value) {
        double res = 0;

        for (Object object : value) {
            res += ((Number) object).doubleValue();
        }

        return res;
    }


}
