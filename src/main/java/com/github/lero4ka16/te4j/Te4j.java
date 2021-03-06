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

package com.github.lero4ka16.te4j;

import com.github.lero4ka16.te4j.filter.Filters;
import com.github.lero4ka16.te4j.template.provider.TemplateProvider;
import com.github.lero4ka16.te4j.template.provider.TemplateProviderBuilder;
import com.github.lero4ka16.te4j.util.replace.ReplaceStrategy;

/**
 * @author lero4ka16
 */
public final class Te4j {

    private Te4j() {
        throw new UnsupportedOperationException();
    }

    private static final Filters FILTERS = new Filters();

    private static final TemplateProvider DEFAULTS = custom()
            .useFiles()
            .replaceStrategy(ReplaceStrategy.NONE)
            .build();

    public static Filters getFilters() {
        return FILTERS;
    }

    /**
     * @return Default template provider
     */
    public static TemplateProvider defaults() {
        return DEFAULTS;
    }

    /**
     * @return New custom template provider builder
     */
    public static TemplateProviderBuilder custom() {
        return new TemplateProviderBuilder();
    }
}
