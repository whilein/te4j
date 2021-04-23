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

package te4j.filter;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import te4j.filter.impl.Average;
import te4j.filter.impl.Capitalize;
import te4j.filter.impl.Cast;
import te4j.filter.impl.Ceil;
import te4j.filter.impl.EscapeTags;
import te4j.filter.impl.Floor;
import te4j.filter.impl.Hex;
import te4j.filter.impl.Lower;
import te4j.filter.impl.Max;
import te4j.filter.impl.Min;
import te4j.filter.impl.Round;
import te4j.filter.impl.Shuffle;
import te4j.filter.impl.Sort;
import te4j.filter.impl.StripTags;
import te4j.filter.impl.Sum;
import te4j.filter.impl.ToString;
import te4j.filter.impl.Trim;
import te4j.filter.impl.Upper;
import te4j.filter.impl.Wrap;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MapBasedFilters implements Filters {

    Map<String, Filter> filters;

    public static @NonNull Filters create(@NonNull Map<String, Filter> filters) {
        return new MapBasedFilters(filters);
    }

    public static @NonNull Filters createDefaults() {
        Filters filters = create(new ConcurrentHashMap<>());
        filters.addDefaults();

        return filters;
    }

    @Override
    public void addDefaults() {
        add(Upper.create());
        add(Lower.create());
        add(Capitalize.create());
        add(StripTags.create());
        add(Trim.create());
        add(Sort.create());
        add(Shuffle.create());
        add(Cast.create(byte.class));
        add(Cast.create(boolean.class));
        add(Cast.create(short.class));
        add(Cast.create(char.class));
        add(Cast.create(int.class));
        add(Cast.create(long.class));
        add(Cast.create(double.class));
        add(Cast.create(float.class));
        add(Wrap.create());
        add(Floor.create());
        add(Ceil.create());
        add(Round.create());
        add(Sum.create());
        add(Max.create());
        add(Min.create());
        add(Average.create());
        add(Hex.create());
        add(EscapeTags.create());
        add(ToString.create());
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