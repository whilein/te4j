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

package te4j.template.context.loader;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import te4j.modifiable.watcher.ModifyWatcherManager;
import te4j.template.Template;
import te4j.template.context.TemplateContext;
import te4j.template.context.parser.TemplateParser;
import te4j.template.parser.ParsedTemplate;
import te4j.template.source.BytesSource;
import te4j.template.source.NameSource;
import te4j.template.source.PathSource;
import te4j.template.source.TemplateSource;
import te4j.util.type.ref.TypeReference;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.function.Function;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultTemplateLoader<T> implements TemplateLoader<T> {

    @NotNull TemplateContext ctx;

    @Getter
    @NotNull TypeReference<T> type;

    @Getter
    @Nullable ModifyWatcherManager modifyWatcherManager;

    @Override
    public @NonNull TemplateLoader<T> withDisabledAutoReloading() {
        return new DefaultTemplateLoader<>(ctx, type, null);
    }

    /**
     * Create new loader
     *
     * @param ctx                  Template context
     * @param type                 Reference to type of future template
     * @param modifyWatcherManager If not null enables auto reloading
     * @param <T>                  Type of future template
     * @return Template loader
     */
    public static <T> TemplateLoader<T> create(
            final @NonNull TemplateContext ctx,
            final @NonNull TypeReference<T> type,
            final @Nullable ModifyWatcherManager modifyWatcherManager
    ) {
        return new DefaultTemplateLoader<>(ctx, type, modifyWatcherManager);
    }

    private Template<T> compile(
            Function<TemplateParser, ParsedTemplate> parse,
            ModifyWatcherManager modifyWatcherManager,
            String parentFile,
            TemplateSource src
    ) {
        TemplateParser parser = ctx.parse();
        ParsedTemplate template = parse.apply(parser);

        return template.compile(
                ctx.getFilters(), modifyWatcherManager, parser, ctx.getOutputTypes(),
                ctx.getMinifyOptions(), parentFile, src, this
        );
    }

    /**
     * Compile new template from bytes
     *
     * @param binary Bytes
     * @return New compiled template
     */
    public @NotNull Template<T> fromBytes(final byte @NonNull [] binary) {
        return compile(
                parser -> parser.fromBytes(binary), modifyWatcherManager,
                ".", BytesSource.create(binary)
        );
    }

    public @NotNull Template<T> fromString(final @NonNull String text) {
        return fromBytes(text.getBytes(StandardCharsets.UTF_8));
    }

    public @NotNull Template<T> from(final @NonNull String name) {
        return compile(
                parser -> parser.from(name), modifyWatcherManager,
                getParent(name), NameSource.create(name)
        );
    }

    public @NotNull Template<T> fromFile(final @NonNull File file) {
        return compile(
                parser -> parser.fromFile(file),
                modifyWatcherManager,
                file.getAbsoluteFile().getParent(),
                PathSource.create(file.getAbsoluteFile().toPath())
        );
    }

    public @NotNull Template<T> fromFile(final @NonNull Path path) {
        return compile(
                parser -> parser.fromFile(path),
                modifyWatcherManager,
                path.toAbsolutePath().getParent().toString(),
                PathSource.create(path.toAbsolutePath())
        );
    }


    private static String getParent(String name) {
        int separator = -1;

        for (int i = name.length() - 1; i >= 0; i--) {
            char ch = name.charAt(i);

            if (ch == '/' || ch == '\\') {
                separator = i;
                break;
            }
        }

        if (separator == -1) {
            return ".";
        }

        return name.substring(0, separator);
    }


}
