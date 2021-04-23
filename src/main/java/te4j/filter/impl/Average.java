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
import java.util.Collection;

/**
 * @author lero4ka16
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Average implements Filter {

    public static @NonNull Filter create() {
        return new Average();
    }

    @Override
    public String getName() {
        return "average";
    }

    @Override
    public Type getWrappedType(@NonNull Type type) {
        Type component = TypeUtils.getComponentType(type);

        if (component instanceof Class<?>) {
            Class<?> componentClass = (Class<?>) component;

            if (TypeUtils.isNumber(componentClass)) {
                return double.class;
            }
        }

        return null;
    }

    public static double process(byte[] value) {
        return value.length == 0 ? 0 : (double) Sum.process(value) / value.length;
    }

    public static double process(short[] value) {
        return value.length == 0 ? 0 : (double) Sum.process(value) / value.length;
    }

    public static double process(int[] value) {
        return value.length == 0 ? 0 : (double) Sum.process(value) / value.length;
    }

    public static double process(long[] value) {
        return value.length == 0 ? 0 : (double) Sum.process(value) / value.length;
    }

    public static double process(float[] value) {
        return value.length == 0 ? 0 : Sum.process(value) / value.length;
    }

    public static double process(double[] value) {
        return value.length == 0 ? 0 : Sum.process(value) / value.length;
    }

    public static double process(Number[] value) {
        return value.length == 0 ? 0 : Sum.process(value) / value.length;
    }

    public static double process(Collection<? extends Number> value) {
        return value.isEmpty() ? 0 : Sum.process(value) / value.size();
    }

}
