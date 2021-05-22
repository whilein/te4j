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

/**
 * @author whilein
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Floor implements Filter {

    public static @NonNull Filter create() {
        return new Floor();
    }

    @Override
    public @NotNull String getName() {
        return "floor";
    }

    @Override
    public Type getWrappedType(@NonNull Type type) {
        return (type instanceof Class<?> && TypeUtils.isNumber((Class<?>) type))
                ? double.class
                : null;
    }

    @Override
    public @NonNull @NotNull String apply(@NonNull String value, @NonNull Type type) {
        return "Math.floor(" + value + ")";
    }
}
