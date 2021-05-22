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
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Singular;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import te4j.Te4j;
import te4j.filter.Filters;
import te4j.modifiable.watcher.ModifyWatcherManager;
import te4j.template.option.minify.Minify;
import te4j.template.option.output.Output;
import te4j.template.resolver.FileTemplateResolver;
import te4j.template.resolver.ResourceTemplateResolver;
import te4j.template.resolver.TemplateResolver;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultTemplateContextBuilder implements TemplateContextBuilder {

    private TemplateResolver resolver;
    private ModifyWatcherManager modifyWatcherManager;
    private Filters filters;

    @Singular
    private final @NotNull Set<@NotNull Output> output;

    @Singular
    private final @NotNull Set<@NotNull Minify> minify;

    public static @NotNull TemplateContextBuilder create() {
        return new DefaultTemplateContextBuilder(
                EnumSet.noneOf(Output.class),
                EnumSet.noneOf(Minify.class)
        );
    }

    public @NotNull TemplateContextBuilder filters(final @NonNull Filters filters) {
        this.filters = filters;
        return this;
    }

    @Override
    public @NotNull TemplateContextBuilder resolver(final @NonNull TemplateResolver resolver) {
        this.resolver = resolver;
        return this;
    }

    @Override
    public @NotNull TemplateContextBuilder output(final @NotNull Output @NonNull ... output) {
        return output(Arrays.asList(output));
    }

    @Override
    public @NotNull TemplateContextBuilder output(final @NonNull Collection<@NotNull Output> types) {
        this.output.addAll(types);
        return this;
    }

    @Override
    public @NotNull TemplateContextBuilder outputAll() {
        return output(Output.getVALUES());
    }

    @Override
    public @NotNull TemplateContextBuilder disableAutoReloading() {
        this.modifyWatcherManager = null;
        return this;
    }

    @Override
    public @NotNull TemplateContextBuilder enableAutoReloading(
            final @NonNull ModifyWatcherManager modifyWatcherManager
    ) {
        this.modifyWatcherManager = modifyWatcherManager;
        return this;
    }

    @Override
    public @NotNull TemplateContextBuilder enableAutoReloading() {
        return enableAutoReloading(Te4j.getDefaultModifyWatcher());
    }

    @Override
    public @NotNull TemplateContextBuilder useResources() {
        this.resolver = ResourceTemplateResolver.INSTANCE;
        return this;
    }

    @Override
    public @NotNull TemplateContextBuilder minify(final @NotNull Minify @NonNull ... options) {
        return minify(Arrays.asList(options));
    }

    @Override
    public @NotNull TemplateContextBuilder minify(final @NonNull Collection<@NonNull Minify> options) {
        this.minify.addAll(options);
        return this;
    }

    @Override
    public @NotNull TemplateContextBuilder minifyAll() {
        return minify(Minify.getValues());
    }

    @Override
    public @NotNull TemplateContext build() {
        if (output.isEmpty()) {
            outputAll();
        }

        if (filters == null) {
            filters = Te4j.getFilters();
        }

        if (resolver == null) {
            resolver = FileTemplateResolver.INSTANCE;
        }

        return DefaultTemplateContext.create(
                filters, resolver, modifyWatcherManager, output, minify
        );
    }


}
