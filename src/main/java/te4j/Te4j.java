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

package te4j;

import org.jetbrains.annotations.NotNull;
import te4j.filter.Filters;
import te4j.filter.MapBasedFilters;
import te4j.modifiable.watcher.ModifyWatcherManager;
import te4j.template.context.DefaultTemplateContextBuilder;
import te4j.template.context.TemplateContext;
import te4j.template.context.TemplateContextBuilder;
import te4j.template.context.loader.TemplateLoader;
import te4j.template.context.parser.TemplateParser;
import te4j.util.type.ref.TypeReference;

/**
 * @author lero4ka16
 */
public final class Te4j {

    private static final Filters FILTERS
            = MapBasedFilters.createDefaults();

    private static final ModifyWatcherManager DEFAULT_MODIFY_WATCHER
            = new ModifyWatcherManager();

    private static final TemplateContext DEFAULTS = custom()
            .minifyAll()
            .outputAll()
            .enableAutoReloading(DEFAULT_MODIFY_WATCHER)
            .build();

    private Te4j() {
        throw new UnsupportedOperationException();
    }

    public static ModifyWatcherManager getDefaultModifyWatcher() {
        return DEFAULT_MODIFY_WATCHER;
    }

    public static @NotNull Filters getFilters() {
        return FILTERS;
    }

    public static <T> TemplateLoader<T> load(@NotNull TypeReference<T> type) {
        return DEFAULTS.load(type);
    }

    public static <T> TemplateLoader<T> load(@NotNull Class<T> cls) {
        return DEFAULTS.load(cls);
    }

    public static TemplateParser parse() {
        return DEFAULTS.parse();
    }

    /**
     * @return Default template context
     */
    public static @NotNull TemplateContext defaults() {
        return DEFAULTS;
    }

    /**
     * @return New custom template context builder
     */
    public static @NotNull TemplateContextBuilder custom() {
        return DefaultTemplateContextBuilder.create();
    }
}
