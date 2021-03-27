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

package com.github.lero4ka16.te4j.filter;

import com.github.lero4ka16.te4j.filter.impl.Average;
import com.github.lero4ka16.te4j.filter.impl.Capitalize;
import com.github.lero4ka16.te4j.filter.impl.Cast;
import com.github.lero4ka16.te4j.filter.impl.Ceil;
import com.github.lero4ka16.te4j.filter.impl.EscapeTags;
import com.github.lero4ka16.te4j.filter.impl.Floor;
import com.github.lero4ka16.te4j.filter.impl.Hex;
import com.github.lero4ka16.te4j.filter.impl.Lower;
import com.github.lero4ka16.te4j.filter.impl.Max;
import com.github.lero4ka16.te4j.filter.impl.Min;
import com.github.lero4ka16.te4j.filter.impl.Round;
import com.github.lero4ka16.te4j.filter.impl.Shuffle;
import com.github.lero4ka16.te4j.filter.impl.Sort;
import com.github.lero4ka16.te4j.filter.impl.StripTags;
import com.github.lero4ka16.te4j.filter.impl.Sum;
import com.github.lero4ka16.te4j.filter.impl.ToString;
import com.github.lero4ka16.te4j.filter.impl.Trim;
import com.github.lero4ka16.te4j.filter.impl.Upper;
import com.github.lero4ka16.te4j.filter.impl.Wrap;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lero4ka16
 */
public final class Filters {

    private final Map<String, Filter> filters = new ConcurrentHashMap<>();

    public Filters() {
        add(new Upper());
        add(new Lower());
        add(new Capitalize());
        add(new StripTags());
        add(new Trim());
        add(new Sort());
        add(new Shuffle());
        add(new Cast("byte"));
        add(new Cast("boolean"));
        add(new Cast("short"));
        add(new Cast("char"));
        add(new Cast("int"));
        add(new Cast("long"));
        add(new Cast("double"));
        add(new Cast("float"));
        add(new Wrap());
        add(new Floor());
        add(new Ceil());
        add(new Round());
        add(new Sum());
        add(new Max());
        add(new Min());
        add(new Average());
        add(new Hex());
        add(new EscapeTags());
        add(new ToString());
    }

    public Filter get(String name) {
        return filters.get(name);
    }

    public void add(Filter filter) {
        filters.put(filter.getName(), filter);
    }

    public String applyFilters(List<String> filters, String value) {
        if (filters == null) {
            return value;
        }

        for (String filterName : filters) {
            Filter filter = get(filterName);

            if (filter == null) {
                throw new IllegalStateException("Filter not found: " + filterName);
            }

            value = filter.wrap(value);
        }

        return value;
    }

}
