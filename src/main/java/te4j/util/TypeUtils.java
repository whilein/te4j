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

package te4j.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author whilein
 */
@UtilityClass
public class TypeUtils {

    public static Type getComponentType(@NonNull Type type) {
        if (type instanceof Class<?>) {
            Class<?> cls = (Class<?>) type;

            if (Iterable.class.isAssignableFrom(cls)) {
                return Object.class;
            }

            return cls.getComponentType(); // may return null
        }

        if (type instanceof ParameterizedType) {
            ParameterizedType parameterized = (ParameterizedType) type;
            Type raw = parameterized.getRawType();

            if (raw instanceof Class<?>) {
                if (Iterable.class.isAssignableFrom((Class<?>) raw)) {
                    return parameterized.getActualTypeArguments()[0];
                }
            }
        }

        return null;
    }

    public static Class<?> getPrimitive(@NonNull Class<?> wrapper) {
        if (wrapper == Byte.class) {
            return byte.class;
        } else if (wrapper == Character.class) {
            return char.class;
        } else if (wrapper == Boolean.class) {
            return boolean.class;
        } else if (wrapper == Short.class) {
            return short.class;
        } else if (wrapper == Integer.class) {
            return int.class;
        } else if (wrapper == Long.class) {
            return long.class;
        } else if (wrapper == Double.class) {
            return double.class;
        } else if (wrapper == Float.class) {
            return float.class;
        } else {
            return null;
        }
    }

    public static Class<?> getWrapper(@NonNull Class<?> primitive) {
        if (primitive == byte.class) {
            return Byte.class;
        } else if (primitive == char.class) {
            return Character.class;
        } else if (primitive == boolean.class) {
            return Boolean.class;
        } else if (primitive == short.class) {
            return Short.class;
        } else if (primitive == int.class) {
            return Integer.class;
        } else if (primitive == long.class) {
            return Long.class;
        } else if (primitive == double.class) {
            return Double.class;
        } else if (primitive == float.class) {
            return Float.class;
        } else {
            return null;
        }
    }

    public static boolean isPrimitive(@NonNull Type type) {
        return type instanceof Class<?> && ((Class<?>) type).isPrimitive();
    }

    public static boolean isWrapper(@NonNull Type type) {
        return type == Byte.class || type == Short.class || type == Integer.class
                || type == Long.class || type == Float.class || type == Double.class
                || type == Character.class || type == Boolean.class;
    }

    public static boolean isPrimitiveOrWrapper(@NonNull Class<?> cls) {
        return cls.isPrimitive() || isWrapper(cls);
    }

    /**
     * Check if class is number (not a <code>Number</code>, i.e. for BigInteger it will return false)
     *
     * @param cls Class
     * @return true if class is primitive or wrapper and not boolean
     */
    public static boolean isNumber(@NonNull Class<?> cls) {
        return isPrimitiveOrWrapper(cls) && cls != boolean.class && cls != Boolean.class;
    }

    public static Class<?> forName(@NonNull String name) throws ClassNotFoundException {
        switch (name) {
            case "byte":
                return byte.class;
            case "short":
                return short.class;
            case "int":
                return int.class;
            case "long":
                return long.class;
            case "float":
                return float.class;
            case "char":
                return char.class;
            case "boolean":
                return boolean.class;
            case "double":
                return double.class;
            default:
                return Class.forName(name);
        }
    }

    public static boolean isNumberOrExtends(@NonNull Class<?> cls) {
        return isNumber(cls) || Number.class.isAssignableFrom(cls);
    }

    public static @NonNull Class<?> getDominatingNumber(@NonNull Class<?> first, @NonNull Class<?> second) {
        if (!first.isPrimitive() || !second.isPrimitive()) {
            first = first.isPrimitive() ? first : getPrimitive(first);
            second = second.isPrimitive() ? second : getPrimitive(second);

            // an implementation of number doesn't have primitive type
            // e.g. BigInteger or AtomicInteger
            if (first == null || second == null) {
                return Number.class;
            }

            return getDominatingNumber(first, second);
        }

        if (first == double.class || second == double.class) {
            return double.class;
        } else if (first == float.class || second == float.class) {
            return float.class;
        } else if (first == long.class || second == long.class) {
            return long.class;
        } else {
            // short + byte = int, short + short = int
            return int.class;
        }
    }

    public static @NonNull String getCanonicalName(@NonNull Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType generic = (ParameterizedType) type;

            StringBuilder sb = new StringBuilder(getCanonicalName(generic.getRawType()));
            boolean b = false;

            sb.append('<');
            for (Type param : generic.getActualTypeArguments()) {
                if (b) {
                    sb.append(',');
                } else {
                    b = true;
                }

                sb.append(getCanonicalName(param));
            }
            sb.append('>');

            return sb.toString();
        }

        if (type instanceof Class<?>) {
            return ((Class<?>) type).getCanonicalName();
        }

        throw new IllegalArgumentException(type.getClass().getSimpleName());
    }

    @SuppressWarnings("unchecked")
    public static <T> @NonNull Class<T> toClass(@NonNull Type type) {
        if (type instanceof Class) {
            return (Class<T>) type;
        } else if (type instanceof ParameterizedType) {
            return (Class<T>) ((ParameterizedType) type).getRawType();
        } else {
            throw new IllegalArgumentException(type.getClass().getSimpleName());
        }
    }

}
