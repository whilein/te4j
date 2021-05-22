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

package te4j.template;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import te4j.modifiable.Modifiable;
import te4j.modifiable.watcher.ModifyWatcherManager;
import te4j.template.context.loader.TemplateLoader;
import te4j.template.source.TemplateSource;
import te4j.util.lazy.ConcurrentLazy;
import te4j.util.lazy.Lazy;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class AutoReloadingTemplate<T> implements Template<T>, Modifiable {

    @NotNull Lazy<@NotNull Template<T>> handle;
    @NotNull TemplateSource source;

    public static @NotNull <T> Template<T> create(
            final @NonNull Template<T> handle,
            final @NonNull TemplateLoader<T> loader,
            final @NonNull TemplateSource src,
            final @NonNull ModifyWatcherManager modifyWatcherManager
    ) {
        val disabledAutoReloading = loader.withDisabledAutoReloading();

        val template = new AutoReloadingTemplate<>(
                ConcurrentLazy.from(handle, () -> src.load(disabledAutoReloading)),
                src
        );

        modifyWatcherManager.register(template);

        return template;
    }

    @Override
    public void handleModify() {
        handle.clear();
    }

    @Override
    public @NotNull List<@NotNull String> getFiles() {
        val files = new ArrayList<>(Arrays.asList(getIncludes()));
        source.getFile().ifPresent(files::add);

        return files;
    }

    @Override
    public @NotNull String @NotNull [] getIncludes() {
        return handle.get().getIncludes();
    }

    @Override
    public @NotNull String renderAsString(final @NotNull T object) {
        return handle.get().renderAsString(object);
    }

    @Override
    public byte @NotNull [] renderAsBytes(final @NotNull T object) {
        return handle.get().renderAsBytes(object);
    }

    @Override
    public void renderTo(final @NotNull T object, final @NotNull OutputStream os) throws IOException {
        handle.get().renderTo(object, os);
    }


}