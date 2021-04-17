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

package te4j.template.context.loader;

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
import java.util.Objects;
import java.util.function.Function;

/**
 * @author lero4ka16
 */
public final class DefaultTemplateLoader<T> implements TemplateLoader<T> {

    private final TemplateContext ctx;
    private final TypeReference<T> type;
    private final ModifyWatcherManager modifyWatcherManager;
    private final boolean useResources;
    private final boolean enableAutoReloading;

    private DefaultTemplateLoader(
            TemplateContext ctx,
            TypeReference<T> type,
            ModifyWatcherManager modifyWatcherManager,
            boolean useResources,
            boolean enableAutoReloading
    ) {
        this.ctx = ctx;
        this.type = type;
        this.modifyWatcherManager = modifyWatcherManager;
        this.useResources = useResources;
        this.enableAutoReloading = enableAutoReloading;
    }

    @Override
    public @NotNull TemplateLoader<T> withAutoReloadingEnabled(
            ModifyWatcherManager modifyWatcherManager,
            boolean value
    ) {
        if (value == enableAutoReloading) {
            return this;
        }

        if (value) {
            Objects.requireNonNull(modifyWatcherManager, "modifyWatcherManager");
        }

        return new DefaultTemplateLoader<>(ctx, type, modifyWatcherManager, useResources, value);
    }

    @Override
    public TypeReference<T> getType() {
        return type;
    }

    @Override
    public boolean isAutoReloadingEnabled() {
        return enableAutoReloading;
    }

    /**
     * Create new loader
     *
     * @param ctx                  Template context
     * @param type                 Reference to type of future template
     * @param modifyWatcherManager If not null enables auto reloading
     * @param useResources         If true files will be supplied from resources
     * @param enableAutoReloading  If true template will be automatically recompiled once file is modified
     * @param <T>                  Type of future template
     * @return Template loader
     */
    public static <T> TemplateLoader<T> create(
            @NotNull TemplateContext ctx,
            @NotNull TypeReference<T> type,
            @Nullable ModifyWatcherManager modifyWatcherManager,
            boolean useResources,
            boolean enableAutoReloading
    ) {
        Objects.requireNonNull(ctx);
        Objects.requireNonNull(type);

        return new DefaultTemplateLoader<>(ctx, type, modifyWatcherManager, useResources,
                modifyWatcherManager != null && enableAutoReloading);
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
                modifyWatcherManager, type, parser, ctx.getOutputTypes(),
                ctx.getMinifyOptions(), parentFile, src, this
        );
    }

    /**
     * Compile new template from bytes
     *
     * @param binary Bytes
     * @return New compiled template
     */
    public @NotNull Template<T> fromBytes(byte @NotNull [] binary) {
        return compile(
                parser -> parser.fromBytes(binary),
                !enableAutoReloading || useResources ? null : modifyWatcherManager,
                ".", BytesSource.create(binary)
        );
    }

    public @NotNull Template<T> fromString(@NotNull String text) {
        return fromBytes(text.getBytes(StandardCharsets.UTF_8));
    }

    public @NotNull Template<T> from(@NotNull String name) {
        return compile(
                parser -> parser.from(name),
                !enableAutoReloading || useResources ? null : modifyWatcherManager,
                getParent(name), NameSource.create(name)
        );
    }

    public @NotNull Template<T> fromFile(@NotNull File file) {
        return compile(
                parser -> parser.fromFile(file),
                !enableAutoReloading ? null : modifyWatcherManager,
                file.getAbsoluteFile().getParent(),
                PathSource.create(file.getAbsoluteFile().toPath())
        );
    }

    public @NotNull Template<T> fromFile(@NotNull Path path) {
        return compile(
                parser -> parser.fromFile(path),
                !enableAutoReloading ? null : modifyWatcherManager,
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