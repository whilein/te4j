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

package te4j.util.lazy;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.function.Supplier;

/**
 * @author lero4ka16
 */
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Lazy<T> implements Supplier<T> {

    Supplier<@NonNull T> initializer;

    public static <T> @NonNull Lazy<T> threadsafe(
            final @NonNull Supplier<T> initializer
    ) {
        return new Threadsafe<>(null, initializer);
    }

    public static <T> @NonNull Lazy<T> threadsafe(
            final T initial,
            final @NonNull Supplier<T> initializer
    ) {
        return new Threadsafe<>(initial, initializer);
    }

    public abstract boolean isInitialized();

    public abstract void clear();

    @FieldDefaults(level = AccessLevel.PRIVATE)
    private static final class Threadsafe<T> extends Lazy<T> {

        private static final Object EMPTY = new Object();

        @SuppressWarnings("unchecked")
        final T empty = (T) EMPTY;

        volatile T value = empty;

        private Threadsafe(final T initial, final Supplier<T> initializer) {
            super(initializer);

            if (initial != null) {
                value = initial;
            }
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

}
