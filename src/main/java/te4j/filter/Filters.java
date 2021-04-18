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

import lombok.NonNull;

import java.util.List;
import java.util.Optional;

/**
 * @author lero4ka16
 */

public interface Filters {

    /**
     * Add filter
     *
     * @param filter Filter
     */
    void add(@NonNull Filter filter);

    /**
     * Add default filters
     */
    void addDefaults();

    /**
     * Get filter by name (case sensitive)
     *
     * @param name Filter's name
     * @return Filter
     */
    @NonNull Optional<? extends Filter> get(@NonNull String name);

    /**
     * Apply filters to string
     *
     * @param filters List of filters
     * @param target  Target string
     * @return String with applied filters
     */

    // TODO return string with applied filters and return type
    @NonNull String applyFilters(List<String> filters, @NonNull String target);

}