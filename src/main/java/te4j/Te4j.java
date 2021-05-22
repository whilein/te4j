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

package te4j;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import te4j.filter.Filters;
import te4j.filter.MapBasedFilters;
import te4j.modifiable.watcher.ModifyWatcherManager;
import te4j.modifiable.watcher.SimpleModifyWatcherManager;
import te4j.template.context.DefaultTemplateContext;
import te4j.template.context.TemplateContext;
import te4j.template.context.TemplateContextBuilder;
import te4j.template.context.loader.TemplateLoader;
import te4j.template.context.parser.TemplateParser;
import te4j.util.type.ref.TypeReference;

/**
 * @author whilein
 */
@UtilityClass
public class Te4j {

    private final Filters FILTERS
            = MapBasedFilters.createDefaults();

    private final ModifyWatcherManager DEFAULT_MODIFY_WATCHER
            = SimpleModifyWatcherManager.create();

    private final TemplateContext DEFAULTS = custom()
            .minifyAll()
            .outputAll()
            .enableAutoReloading(DEFAULT_MODIFY_WATCHER)
            .build();

    public @NonNull ModifyWatcherManager getDefaultModifyWatcher() {
        return DEFAULT_MODIFY_WATCHER;
    }

    public @NonNull Filters getFilters() {
        return FILTERS;
    }

    public @NonNull <T> TemplateLoader<T> load(@NonNull TypeReference<T> type) {
        return DEFAULTS.load(type);
    }

    public @NonNull <T> TemplateLoader<T> load(@NonNull Class<T> cls) {
        return DEFAULTS.load(cls);
    }

    public @NonNull TemplateParser parse() {
        return DEFAULTS.parse();
    }

    /**
     * @return Default template context
     */
    public @NonNull TemplateContext defaults() {
        return DEFAULTS;
    }

    /**
     * @return New custom template context builder
     */
    public @NonNull TemplateContextBuilder custom() {
        return DefaultTemplateContext.builder();
    }
}
