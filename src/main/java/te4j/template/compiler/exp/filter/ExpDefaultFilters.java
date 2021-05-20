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

package te4j.template.compiler.exp.filter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import te4j.filter.Filter;
import te4j.filter.Filters;

import java.lang.reflect.Type;

/**
 * @author whilein
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExpDefaultFilters implements ExpFilters {

    private Type type;
    private String value;

    public static @NonNull ExpFilters create(@NonNull Type type) {
        return new ExpDefaultFilters(type, "%s");
    }


    public static @NonNull ExpFilters create(@NonNull Type type, @NonNull String format) {
        return new ExpDefaultFilters(type, format);
    }

    @Override
    public @NonNull Type getType() {
        return type;
    }

    @Override
    public @NonNull String format(@NonNull String accessor) {
        return String.format(value, accessor);
    }

    @Override
    public void add(@NonNull Filters filters, @NonNull String name) {
        Filter filter = filters.get(name).orElseThrow(() -> new IllegalStateException("Unknown filter: " + name));

        Type newType = filter.getWrappedType(type);

        if (newType == null) {
            throw new IllegalStateException("Filter " + name + " is not applicable to " + type);
        }

        value = filter.apply(value, type);
        type = newType;
    }
}
