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

package te4j.template.context.parser;

import lombok.NonNull;
import te4j.template.exception.TemplateLoadException;
import te4j.template.option.minify.Minify;
import te4j.template.parser.EmptyParsedTemplate;
import te4j.template.parser.ParsedTemplate;
import te4j.template.reader.DefaultTemplateReader;
import te4j.util.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

/**
 * @author lero4ka16
 */
public final class DefaultTemplateParser implements TemplateParser {

    private final boolean useResources;
    private final Set<Minify> minifyOptions;

    public DefaultTemplateParser(boolean useResources, Set<Minify> minifyOptions) {
        this.useResources = useResources;
        this.minifyOptions = minifyOptions;
    }

    public static TemplateParser create(boolean useResources, Set<Minify> minifyOptions) {
        return new DefaultTemplateParser(useResources, minifyOptions);
    }

    @Override
    public @NonNull ParsedTemplate fromBytes(byte @NonNull [] binary) {
        if (binary.length == 0) {
            return EmptyParsedTemplate.getInstance();
        }

        return DefaultTemplateReader.create(binary, minifyOptions).readTemplate();
    }

    @Override
    public @NonNull ParsedTemplate fromString(@NonNull String text) {
        if (text.isEmpty()) {
            return EmptyParsedTemplate.getInstance();
        }

        return fromBytes(text.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public @NonNull ParsedTemplate from(@NonNull String name) {
        try {
            return fromBytes(Utils.read(name, useResources));
        } catch (IOException e) {
            return EmptyParsedTemplate.getInstance();
        }
    }

    @Override
    public @NonNull ParsedTemplate fromFile(@NonNull File file) {
        try {
            return fromBytes(Utils.readFile(file));
        } catch (IOException e) {
            throw new TemplateLoadException("Cannot read template", e);
        }
    }

    @Override
    public @NonNull ParsedTemplate fromFile(@NonNull Path path) {
        try {
            return fromBytes(Files.readAllBytes(path));
        } catch (IOException e) {
            throw new TemplateLoadException("Cannot read template", e);
        }
    }
}
