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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import te4j.filter.Filter;
import te4j.util.TypeUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author lero4ka16
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Sort implements Filter {

    public static @NonNull Filter create() {
        return new Sort();
    }

    @Override
    public String getName() {
        return "sorted";
    }

    @Override
    public Type getWrappedType(@NonNull Type type) {
        Type component = TypeUtils.getComponentType(type);

        if (component instanceof Class<?>) {
            Class<?> componentClass = (Class<?>) component;

            if (TypeUtils.isPrimitiveOrWrapper(componentClass)
                    || Comparable.class.isAssignableFrom(componentClass)) {
                return type;
            }
        }

        return null;
    }

    public static byte[] process(byte[] value) {
        if (value.length == 0) return value;

        byte[] result = Arrays.copyOf(value, value.length);
        Arrays.sort(result);

        return result;
    }

    public static short[] process(short[] value) {
        if (value.length == 0) return value;

        short[] result = Arrays.copyOf(value, value.length);
        Arrays.sort(result);

        return result;
    }

    public static int[] process(int[] value) {
        if (value.length == 0) return value;

        int[] result = Arrays.copyOf(value, value.length);
        Arrays.sort(result);

        return result;
    }

    public static long[] process(long[] value) {
        if (value.length == 0) return value;

        long[] result = Arrays.copyOf(value, value.length);
        Arrays.sort(result);

        return result;
    }


    public static float[] process(float[] value) {
        if (value.length == 0) return value;

        float[] result = Arrays.copyOf(value, value.length);
        Arrays.sort(result);

        return result;
    }

    public static double[] process(double[] value) {
        if (value.length == 0) return value;

        double[] result = Arrays.copyOf(value, value.length);
        Arrays.sort(result);

        return result;
    }

    public static char[] process(char[] value) {
        if (value.length == 0) return value;

        char[] result = Arrays.copyOf(value, value.length);
        Arrays.sort(result);

        return result;
    }

    public static boolean[] process(boolean[] value) {
        if (value.length == 0) return value;

        boolean[] result = new boolean[value.length];

        int falseCount = 0;

        for (boolean b : value) {
            if (!b) falseCount++;
        }

        Arrays.fill(result, falseCount, result.length, true);

        return result;
    }

    public static <T extends Comparable<T>> Collection<T> process(Collection<T> value) {
        if (value.isEmpty()) return value;

        List<T> collection = new ArrayList<>(value);
        Collections.sort(collection);

        return collection;
    }

    public static <T extends Comparable<T>> T[] process(T[] value) {
        if (value.length == 0) return value;

        T[] result = Arrays.copyOf(value, value.length);
        Arrays.sort(result);

        return result;
    }

}
