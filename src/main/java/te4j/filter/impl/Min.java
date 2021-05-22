/*
 *    Copyright 2021 Whilein
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
import org.jetbrains.annotations.NotNull;
import te4j.filter.Filter;
import te4j.util.TypeUtils;

import java.lang.reflect.Type;
import java.util.Iterator;

/**
 * @author whilein
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Min implements Filter {

    public static @NonNull Filter create() {
        return new Min();
    }

    @Override
    public @NotNull String getName() {
        return "min";
    }

    @Override
    public Type getWrappedType(@NonNull Type type) {
        Type component = TypeUtils.getComponentType(type);

        if (component instanceof Class<?>) {
            Class<?> componentClass = (Class<?>) component;

            if (!componentClass.isArray() // byte[][] or Collection<byte[]> is forbidden
                    && TypeUtils.isPrimitiveOrWrapper(componentClass)) {
                return componentClass;
            }
        }

        return null;
    }

    public static boolean process(boolean[] value) {
        if (value == null || value.length == 0)
            throw new IllegalStateException("Input is empty!");

        for (boolean element : value) {
            if (!element) return false;
        }

        return true;
    }

    public static char process(char[] value) {
        if (value == null || value.length == 0)
            throw new IllegalStateException("Input is empty!");

        char res = value[0];

        for (int i = 1; i < value.length; i++) {
            if (res > value[i]) {
                res = value[i];
            }
        }

        return res;
    }

    public static byte process(byte[] value) {
        if (value == null || value.length == 0)
            throw new IllegalStateException("Input is empty!");

        byte res = value[0];

        for (int i = 1; i < value.length; i++) {
            if (res > value[i]) {
                res = value[i];
            }
        }

        return res;
    }

    public static short process(short[] value) {
        if (value == null || value.length == 0)
            throw new IllegalStateException("Input is empty!");

        short res = value[0];

        for (int i = 1; i < value.length; i++) {
            if (res > value[i]) {
                res = value[i];
            }
        }

        return res;
    }

    public static int process(int[] value) {
        if (value == null || value.length == 0)
            throw new IllegalStateException("Input is empty!");

        int res = value[0];

        for (int i = 1; i < value.length; i++) {
            if (res > value[i]) {
                res = value[i];
            }
        }

        return res;
    }

    public static long process(long[] value) {
        if (value == null || value.length == 0)
            throw new IllegalStateException("Input is empty!");

        long res = value[0];

        for (int i = 1; i < value.length; i++) {
            if (res > value[i]) {
                res = value[i];
            }
        }

        return res;
    }

    public static float process(float[] value) {
        if (value == null || value.length == 0)
            throw new IllegalStateException("Input is empty!");

        float res = value[0];

        for (int i = 1; i < value.length; i++) {
            if (res > value[i]) {
                res = value[i];
            }
        }

        return res;
    }

    public static double process(double[] value) {
        if (value == null || value.length == 0)
            throw new IllegalStateException("Input is empty!");

        double res = value[0];

        for (int i = 1; i < value.length; i++) {
            if (res > value[i]) {
                res = value[i];
            }
        }

        return res;
    }

    public static <T extends Comparable<T>> T process(Iterable<T> value) {
        Iterator<T> iterator = value.iterator();

        if (!iterator.hasNext())
            throw new IllegalStateException("Input is empty!");

        T res = iterator.next();

        while (iterator.hasNext()) {
            T that = iterator.next();

            if (res.compareTo(that) > 0) {
                res = that;
            }
        }

        return res;
    }

    public static <T extends Comparable<T>> T process(T[] value) {
        if (value == null || value.length == 0)
            throw new IllegalStateException("Input is empty!");

        T res = value[0];

        for (int i = 1, j = value.length; i < j; i++) {
            T that = value[i];

            if (res.compareTo(that) > 0) {
                res = that;
            }
        }

        return res;
    }

}
