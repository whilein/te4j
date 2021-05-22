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

package te4j.util.lazy;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConcurrentLazy<T> implements Lazy<T> {

    static final Object EMPTY = new Object();

    volatile T value;

    final @NotNull Supplier<T> initializer;
    final @NotNull T empty;

    @SuppressWarnings("unchecked")
    private static <T> @NotNull Lazy<T> _new(
            final @Nullable T initial,
            final @NotNull Supplier<T> initializer
    ) {
        val empty = (T) EMPTY;

        val value = initial == null
                ? empty : initial;

        return new ConcurrentLazy<>(value, initializer, empty);
    }

    public static <T> @NotNull Lazy<T> from(
            final @NonNull Supplier<T> initializer
    ) {
        return _new(null, initializer);
    }

    public static <T> @NotNull Lazy<T> from(
            final @Nullable T initial,
            final @NonNull Supplier<T> initializer
    ) {
        return _new(initial, initializer);
    }

    @Override
    public T get() {
        T result = value;

        if (result == EMPTY) {
            synchronized (this) {
                result = value;

                if (result == EMPTY) {
                    result = value = initializer.get();
                }
            }
        }

        return result;
    }

    @Override
    public synchronized boolean isInitialized() {
        return value != empty;
    }

    @Override
    public synchronized void clear() {
        value = empty;
    }

}
