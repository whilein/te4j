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
import te4j.filter.Filter;
import te4j.util.TypeUtils;

import java.lang.reflect.Type;

/**
 * @author whilein
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Sum implements Filter {

    public static @NonNull Filter create() {
        return new Sum();
    }

    @Override
    public String getName() {
        return "sum";
    }

    @Override
    public Type getWrappedType(@NonNull Type type) {
        Type component = TypeUtils.getComponentType(type);

        if (component instanceof Class<?>) {
            boolean isArray = type instanceof Class<?> && ((Class<?>) type).isArray();

            Class<?> componentClass = (Class<?>) component;

            if (!componentClass.isArray()) {
                if (componentClass == byte.class
                        || componentClass == short.class
                        || componentClass == int.class
                        || componentClass == boolean.class
                        || (componentClass == Boolean.class && isArray)) {
                    return int.class;
                }

                return double.class;
            }
        }

        return null;
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

    public static int process(Boolean[] value) {
        int res = 0;

        for (boolean i : value) {
            res += i ? 1 : 0;
        }

        return res;
    }

    public static int process(boolean[] value) {
        int res = 0;

        for (boolean i : value) {
            res += i ? 1 : 0;
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
            res += object instanceof Number ? ((Number) object).doubleValue() : object == Boolean.TRUE ? 1 : 0;
        }

        return res;
    }

    public static double process(Object[] value) {
        double res = 0;

        for (Object object : value) {
            res += object instanceof Number ? ((Number) object).doubleValue() : object == Boolean.TRUE ? 1 : 0;
        }

        return res;
    }

}
