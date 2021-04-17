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

package te4j.template;

import org.jetbrains.annotations.NotNull;
import te4j.modifiable.Modifiable;
import te4j.modifiable.watcher.ModifyWatcherManager;
import te4j.template.context.loader.TemplateLoader;
import te4j.template.source.TemplateSource;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class AutoReloadingTemplate<T> implements Template<T>, Modifiable {

    private final TemplateLoader<T> loader;
    private final TemplateSource source;

    private volatile Template<T> handle;

    private AutoReloadingTemplate(Template<T> handle, TemplateLoader<T> loader, TemplateSource src) {
        this.loader = loader;
        this.source = src;
        this.handle = handle;
    }

    public static <T> Template<T> create(
            @NotNull Template<T> handle,
            @NotNull TemplateLoader<T> loader,
            @NotNull TemplateSource src,
            @NotNull ModifyWatcherManager modifyWatcherManager
    ) {
        Objects.requireNonNull(handle, "handle");
        Objects.requireNonNull(loader, "loader");
        Objects.requireNonNull(src, "src");
        Objects.requireNonNull(modifyWatcherManager, "modifyWatcherManager");

        AutoReloadingTemplate<T> template = new AutoReloadingTemplate<>(
                handle, loader.withAutoReloadingEnabled(null, false), src
        );

        modifyWatcherManager.register(template);

        return template;
    }

    private Template<T> getHandle() {
        if (handle == null) {
            synchronized (this) {
                if (handle == null) {
                    handle = source.load(loader);
                }
            }
        }

        return handle;
    }

    public synchronized void handleModify() {
        handle = null;
    }

    @Override
    public List<Path> getFiles() {
        String[] includes = getIncludes();

        List<Path> result = Arrays.stream(includes)
                .map(include -> Paths.get(include).toAbsolutePath())
                .collect(Collectors.toList());

        if (source.hasPath()) {
            result.add(source.getPath());
        }

        return result;
    }

    @Override
    public @NotNull String[] getIncludes() {
        return getHandle().getIncludes();
    }

    @Override
    public @NotNull String renderAsString(@NotNull T object) {
        return getHandle().renderAsString(object);
    }

    @Override
    public byte @NotNull [] renderAsBytes(@NotNull T object) {
        return getHandle().renderAsBytes(object);
    }

    @Override
    public void renderTo(@NotNull T object, @NotNull OutputStream os) throws IOException {
        getHandle().renderTo(object, os);
    }


}