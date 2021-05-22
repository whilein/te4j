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

package te4j.template.context;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import te4j.filter.Filters;
import te4j.modifiable.watcher.ModifyWatcherManager;
import te4j.template.context.loader.DefaultTemplateLoader;
import te4j.template.context.loader.TemplateLoader;
import te4j.template.context.parser.DefaultTemplateParser;
import te4j.template.context.parser.TemplateParser;
import te4j.template.option.minify.Minify;
import te4j.template.option.output.Output;
import te4j.template.resolver.TemplateResolver;
import te4j.util.type.ref.ClassReference;
import te4j.util.type.ref.TypeReference;

import java.util.Set;

/**
 * @author whilein
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultTemplateContext implements TemplateContext {

    @NotNull Filters filters;

    @NotNull TemplateResolver resolver;
    @Nullable ModifyWatcherManager modifyWatcherManager;

    @NotNull Set<@NotNull Output> outputTypes;
    @NotNull Set<@NotNull Minify> minifyOptions;

    public static @NotNull TemplateContextBuilder builder() {
        return DefaultTemplateContextBuilder.create();
    }

    public static @NotNull TemplateContext create(
            final @NonNull Filters filters,
            final @NonNull TemplateResolver resolver,
            final @Nullable ModifyWatcherManager modifyWatcherManager,
            final @NonNull Set<Output> outputTypes,
            final @NonNull Set<Minify> minifyOptions
    ) {
        return new DefaultTemplateContext(
                filters, resolver, modifyWatcherManager, outputTypes, minifyOptions
        );
    }

    @Override
    public @NotNull <T> TemplateLoader<T> load(final @NonNull TypeReference<T> type) {
        return DefaultTemplateLoader.create(this, type, modifyWatcherManager);
    }

    @Override
    public @NotNull <T> TemplateLoader<T> load(final @NonNull Class<T> cls) {
        return load(ClassReference.create(cls));
    }

    @Override
    public @NotNull TemplateParser parse() {
        return DefaultTemplateParser.create(resolver, minifyOptions);
    }

}
