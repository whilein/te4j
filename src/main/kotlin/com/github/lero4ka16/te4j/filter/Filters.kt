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
package com.github.lero4ka16.te4j.filter

import com.github.lero4ka16.te4j.filter.impl.Average
import com.github.lero4ka16.te4j.filter.impl.Capitalize
import com.github.lero4ka16.te4j.filter.impl.Ceil
import com.github.lero4ka16.te4j.filter.impl.EscapeTags
import com.github.lero4ka16.te4j.filter.impl.Floor
import com.github.lero4ka16.te4j.filter.impl.Hex
import com.github.lero4ka16.te4j.filter.impl.Lower
import com.github.lero4ka16.te4j.filter.impl.Max
import com.github.lero4ka16.te4j.filter.impl.Min
import com.github.lero4ka16.te4j.filter.impl.Round
import com.github.lero4ka16.te4j.filter.impl.Shuffle
import com.github.lero4ka16.te4j.filter.impl.Sort
import com.github.lero4ka16.te4j.filter.impl.StripTags
import com.github.lero4ka16.te4j.filter.impl.Sum
import com.github.lero4ka16.te4j.filter.impl.Trim
import com.github.lero4ka16.te4j.filter.impl.Upper
import java.util.concurrent.ConcurrentHashMap

/**
 * @author lero4ka16
 */
class Filters {
    private val filters: MutableMap<String, Filter> = ConcurrentHashMap()

    fun search(name: String): Filter? {
        return filters[name]
    }

    fun add(filter: Filter) {
        filters[filter.name] = filter
    }

    fun applyFilters(filters: String?, arg: String): String {
        var value = arg

        if (filters == null) {
            return value
        }

        for (filterName in filters.split(":")) {
            val filter = search(filterName) ?: throw IllegalStateException("Filter not found: $filterName")
            value = filter.apply(value)
        }

        return value
    }

    init {
        add(Upper())
        add(Lower())
        add(Capitalize())
        add(Trim())
        add(Ceil())
        add(Floor())
        add(Round())
        add(Sort())
        add(Shuffle())
        add(Sum())
        add(Average())
        add(Max())
        add(Min())
        add(Hex())
        add(StripTags())
        add(EscapeTags())
    }

}