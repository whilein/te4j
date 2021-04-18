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

package te4j.template.parser;

import lombok.NonNull;
import te4j.modifiable.watcher.ModifyWatcherManager;
import te4j.template.AutoReloadingTemplate;
import te4j.template.PlainTemplate;
import te4j.template.Template;
import te4j.template.context.loader.TemplateLoader;
import te4j.template.context.parser.TemplateParser;
import te4j.template.option.minify.Minify;
import te4j.template.option.output.Output;
import te4j.template.path.TemplatePath;
import te4j.template.source.TemplateSource;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author lero4ka16
 */
public final class PlainParsedTemplate extends AbstractParsedTemplate {

    private PlainParsedTemplate(byte[] content, int offset, int length) {
        super(content, offset, length);
    }

    public static ParsedTemplate create(byte @NonNull [] content, int offset, int length) {
        checkArguments(content, offset, length);

        int trimmedBytes = trim(content, offset, length);

        return new PlainParsedTemplate(
                content,
                offset + trimmedBytes,
                length - trimmedBytes
        );
    }

    @Override
    public <T> Template<T> compile(
            ModifyWatcherManager modifyWatcherManager,
            @NonNull TemplateParser parser,
            @NonNull Set<Output> outputTypes,
            @NonNull Set<Minify> minifyOptions,
            @NonNull String parentFile,
            @NonNull TemplateSource src,
            @NonNull TemplateLoader<T> loader
    ) {
        Template<T> result = PlainTemplate.create(content, offset, length);

        if (modifyWatcherManager != null) {
            result = AutoReloadingTemplate.create(result, loader, src, modifyWatcherManager);
        }

        return result;
    }

    @Override
    public boolean hasPaths() {
        return false;
    }

    @Override
    public List<TemplatePath> getPaths() {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return "Template.Plain[" + new String(content, offset, length, StandardCharsets.UTF_8) + "]";
    }
}
