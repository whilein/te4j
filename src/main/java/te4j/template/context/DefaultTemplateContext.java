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

package te4j.template.context;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import te4j.modifiable.watcher.ModifyWatcherManager;
import te4j.template.context.loader.DefaultTemplateLoader;
import te4j.template.context.loader.TemplateLoader;
import te4j.template.context.parser.DefaultTemplateParser;
import te4j.template.context.parser.TemplateParser;
import te4j.template.option.minify.Minify;
import te4j.template.option.output.Output;
import te4j.util.type.ref.ClassReference;
import te4j.util.type.ref.TypeReference;

import java.util.Set;

/**
 * @author lero4ka16
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultTemplateContext implements TemplateContext {

    private final boolean useResources;
    private final ModifyWatcherManager modifyWatcherManager;

    private final Set<Output> outputTypes;
    private final Set<Minify> minifyOptions;

    public static @NonNull TemplateContextBuilder builder() {
        return new DefaultTemplateContextBuilder();
    }

    public static @NonNull TemplateContext create(
            boolean useResources,
            ModifyWatcherManager modifyWatcherManager,
            @NonNull Set<Output> outputTypes,
            @NonNull Set<Minify> minifyOptions
    ) {
        return new DefaultTemplateContext(useResources, modifyWatcherManager, outputTypes, minifyOptions);
    }

    @Override
    public @NonNull <T> TemplateLoader<T> load(@NonNull TypeReference<T> type) {
        return load(type, true);
    }

    @Override
    public @NonNull <T> TemplateLoader<T> load(@NonNull Class<T> cls) {
        return load(cls, true);
    }

    @Override
    public @NonNull <T> TemplateLoader<T> load(@NonNull TypeReference<T> type, boolean enableAutoReloading) {
        return DefaultTemplateLoader.create(this, type, modifyWatcherManager, useResources, enableAutoReloading);
    }

    @Override
    public @NonNull <T> TemplateLoader<T> load(@NonNull Class<T> cls, boolean enableAutoReloading) {
        return load(ClassReference.create(cls), enableAutoReloading);
    }

    @Override
    public @NonNull TemplateParser parse() {
        return DefaultTemplateParser.create(useResources, minifyOptions);
    }

    @Override
    public boolean useResources() {
        return useResources;
    }

    @Override
    public ModifyWatcherManager getModifyWatcherManager() {
        return modifyWatcherManager;
    }

    @Override
    public @NonNull Set<Output> getOutputTypes() {
        return outputTypes;
    }

    @Override
    public @NonNull Set<Minify> getMinifyOptions() {
        return minifyOptions;
    }

}
