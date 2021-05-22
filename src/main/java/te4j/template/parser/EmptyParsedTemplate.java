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

package te4j.template.parser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import te4j.filter.Filters;
import te4j.modifiable.watcher.ModifyWatcherManager;
import te4j.template.EmptyTemplate;
import te4j.template.Template;
import te4j.template.context.loader.TemplateLoader;
import te4j.template.context.parser.TemplateParser;
import te4j.template.option.minify.Minify;
import te4j.template.option.output.Output;
import te4j.template.path.TemplatePath;
import te4j.template.source.TemplateSource;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author whilein
 */
public final class EmptyParsedTemplate implements ParsedTemplate {

    private final byte[] content = new byte[0];

    private static class Singleton {
        private static final ParsedTemplate INSTANCE = new EmptyParsedTemplate();
    }

    public static @NotNull ParsedTemplate getInstance() {
        return Singleton.INSTANCE;
    }

    @Override
    public int getOffset() {
        return 0;
    }

    @Override
    public int getLength() {
        return 0;
    }

    @Override
    public byte @NotNull [] getRawContent() {
        return content;
    }

    @Override
    public byte @NotNull [] getContent() {
        return content;
    }

    @Override
    public <T> @NotNull Template<T> compile(
            final @NotNull Filters filters,
            final @Nullable ModifyWatcherManager modifyWatcherManager,
            final @NotNull TemplateParser parser,
            final @NotNull Set<Output> outputTypes,
            final @NotNull Set<Minify> minifyOptions,
            final @NotNull String parentFile,
            final @NotNull TemplateSource src,
            final @NotNull TemplateLoader<T> loader
    ) {
        return EmptyTemplate.getInstance();
    }

    @Override
    public boolean hasPaths() {
        return false;
    }

    @Override
    public @NotNull List<@NotNull TemplatePath> getPaths() {
        return Collections.emptyList();
    }
}
