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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import te4j.filter.Filters;
import te4j.modifiable.watcher.ModifyWatcherManager;
import te4j.template.AutoReloadingTemplate;
import te4j.template.Template;
import te4j.template.compiler.TemplateCompileProcess;
import te4j.template.context.loader.TemplateLoader;
import te4j.template.context.parser.TemplateParser;
import te4j.template.exception.TemplateLoadException;
import te4j.template.option.minify.Minify;
import te4j.template.option.output.Output;
import te4j.template.path.TemplatePath;
import te4j.template.source.TemplateSource;

import java.util.List;
import java.util.Set;

/**
 * @author whilein
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class StandardParsedTemplate extends AbstractParsedTemplate {

    private final List<TemplatePath> paths;

    private StandardParsedTemplate(
            final @NotNull List<@NotNull TemplatePath> paths,
            final byte @NotNull [] content,
            final int offset,
            final int length
    ) {
        super(content, offset, length);

        this.paths = paths;
    }

    public static ParsedTemplate create(
            @NonNull List<@NotNull TemplatePath> paths,
            byte @NonNull [] content,
            int offset,
            int length
    ) {
        checkArguments(content, offset, length);

        if (paths.isEmpty()) {
            throw new IllegalArgumentException("paths is empty");
        }

        val trimmedBytes = trim(content, offset, length);

        return new StandardParsedTemplate(
                paths,
                content,
                offset + trimmedBytes,
                length - trimmedBytes
        );
    }

    @Override
    public <T> @NotNull Template<T> compile(
            final @NonNull Filters filters,
            final @Nullable ModifyWatcherManager modifyWatcherManager,
            final @NonNull TemplateParser parser,
            final @NonNull Set<Output> outputTypes,
            final @NonNull Set<Minify> minifyOptions,
            final @NonNull String parentFile,
            final @NonNull TemplateSource src,
            final @NonNull TemplateLoader<T> loader
    ) {
        try {
            Template<T> result = new TemplateCompileProcess<>(
                    filters, loader.getType(), parser,
                    outputTypes, minifyOptions,
                    this, parentFile
            ).compile();

            if (modifyWatcherManager != null) {
                result = AutoReloadingTemplate.create(result, loader, src, modifyWatcherManager);
            }

            return result;
        } catch (Exception e) {
            throw new TemplateLoadException("Cannot compile template: " + src, e);
        }
    }

    @Override
    public boolean hasPaths() {
        return !paths.isEmpty();
    }

}
