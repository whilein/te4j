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

import lombok.NonNull;
import te4j.Te4j;
import te4j.filter.Filters;
import te4j.modifiable.watcher.ModifyWatcherManager;
import te4j.template.option.minify.Minify;
import te4j.template.option.output.Output;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

/**
 * @author lero4ka16
 */
public final class DefaultTemplateContextBuilder implements TemplateContextBuilder {

    private boolean useResources;
    private ModifyWatcherManager modifyWatcherManager;
    private Filters filters;

    private final Set<Output> outputTypes = EnumSet.noneOf(Output.class);
    private final Set<Minify> minifyOptions = EnumSet.noneOf(Minify.class);

    public static TemplateContextBuilder create() {
        return new DefaultTemplateContextBuilder();
    }

    public @NonNull TemplateContextBuilder filters(Filters filters) {
        this.filters = filters;
        return this;
    }

    @Override
    public TemplateContextBuilder output(Output... output) {
        return output(Arrays.asList(output));
    }

    @Override
    public TemplateContextBuilder output(Collection<Output> types) {
        this.outputTypes.addAll(types);
        return this;
    }

    @Override
    public TemplateContextBuilder outputAll() {
        return output(Output.getValues());
    }

    @Override
    public TemplateContextBuilder disableAutoReloading() {
        this.modifyWatcherManager = null;
        return this;
    }

    @Override
    public TemplateContextBuilder enableAutoReloading(ModifyWatcherManager modifyWatcherManager) {
        this.modifyWatcherManager = modifyWatcherManager;
        return this;
    }

    @Override
    public TemplateContextBuilder enableAutoReloading() {
        return enableAutoReloading(Te4j.getDefaultModifyWatcher());
    }

    @Override
    public TemplateContextBuilder useResources() {
        this.useResources = true;
        return this;
    }

    @Override
    public TemplateContextBuilder minify(Minify... options) {
        return minify(Arrays.asList(options));
    }

    @Override
    public TemplateContextBuilder minify(Collection<Minify> options) {
        this.minifyOptions.addAll(options);
        return this;
    }

    @Override
    public TemplateContextBuilder minifyAll() {
        return minify(Minify.getValues());
    }

    @Override
    public TemplateContext build() {
        if (outputTypes.isEmpty()) {
            outputAll();
        }

        if (filters == null) {
            filters = Te4j.getFilters();
        }

        return DefaultTemplateContext.create(filters, useResources, modifyWatcherManager, outputTypes, minifyOptions);
    }


}
