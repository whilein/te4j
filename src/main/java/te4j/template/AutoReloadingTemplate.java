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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
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
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class AutoReloadingTemplate<T> implements Template<T>, Modifiable {

    private volatile Template<T> handle;

    private final TemplateLoader<T> loader;
    private final TemplateSource source;

    public static @NonNull <T> Template<T> create(
            @NonNull Template<T> handle,
            @NonNull TemplateLoader<T> loader,
            @NonNull TemplateSource src,
            @NonNull ModifyWatcherManager modifyWatcherManager
    ) {
        AutoReloadingTemplate<T> template = new AutoReloadingTemplate<>(
                handle, loader.withDisabledAutoReloading(), src
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
    public @NonNull List<Path> getFiles() {
        String[] includes = getIncludes();

        List<Path> result = Arrays.stream(includes)
                .map(include -> Paths.get(include).toAbsolutePath())
                .collect(Collectors.toList());

        source.getPath().ifPresent(result::add);

        return result;
    }

    @Override
    public @NonNull String[] getIncludes() {
        return getHandle().getIncludes();
    }

    @Override
    public @NonNull String renderAsString(@NonNull T object) {
        return getHandle().renderAsString(object);
    }

    @Override
    public byte @NonNull [] renderAsBytes(@NonNull T object) {
        return getHandle().renderAsBytes(object);
    }

    @Override
    public void renderTo(@NonNull T object, @NonNull OutputStream os) throws IOException {
        getHandle().renderTo(object, os);
    }


}