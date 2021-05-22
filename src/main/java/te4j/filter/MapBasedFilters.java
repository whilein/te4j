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

package te4j.filter;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import te4j.filter.impl.*;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MapBasedFilters implements Filters {

    @NotNull Map<@NotNull String, @NotNull Filter> filters;

    public static @NotNull Filters create(final @NonNull Map<@NotNull String, @NotNull Filter> filters) {
        return new MapBasedFilters(filters);
    }

    public static @NotNull Filters createDefaults() {
        Filters filters = create(new ConcurrentHashMap<>());
        filters.add(Upper.create());
        filters.add(Lower.create());
        filters.add(Capitalize.create());
        filters.add(StripTags.create());
        filters.add(Trim.create());
        filters.add(Sort.create());
        filters.add(Shuffle.create());
        filters.add(Cast.create(byte.class));
        filters.add(Cast.create(boolean.class));
        filters.add(Cast.create(short.class));
        filters.add(Cast.create(char.class));
        filters.add(Cast.create(int.class));
        filters.add(Cast.create(long.class));
        filters.add(Cast.create(double.class));
        filters.add(Cast.create(float.class));
        filters.add(Wrap.create());
        filters.add(Floor.create());
        filters.add(Ceil.create());
        filters.add(Round.create());
        filters.add(Sum.create());
        filters.add(Max.create());
        filters.add(Min.create());
        filters.add(Average.create());
        filters.add(Hex.create());
        filters.add(EscapeTags.create());
        filters.add(ToString.create());

        return filters;
    }

    @Override
    public @NonNull Optional<? extends Filter> get(@NonNull String name) {
        return Optional.ofNullable(filters.get(name));
    }

    @Override
    public void add(@NonNull Filter filter) {
        filters.put(filter.getName(), filter);
    }

}