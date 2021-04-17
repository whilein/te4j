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
import te4j.modifiable.watcher.ModifyWatcherManager;
import te4j.template.Template;
import te4j.util.type.ref.TypeReference;

import java.io.File;
import java.nio.file.Path;

/**
 * @author lero4ka16
 */
public interface TemplateLoader<T> {

    TypeReference<T> getType();

    /**
     * @return If true a template will be recompiled once file is modified
     */
    boolean isAutoReloadingEnabled();

    @NotNull TemplateLoader<T> withAutoReloadingEnabled(ModifyWatcherManager modifyWatcherManager, boolean value);

    /**
     * Compile new template from bytes
     *
     * @param binary Bytes
     * @return New compiled template
     */
    @NotNull Template<T> fromBytes(byte @NotNull [] binary);

    @NotNull Template<T> fromString(@NotNull String text);

    @NotNull Template<T> from(@NotNull String name);

    @NotNull Template<T> fromFile(@NotNull File file);

    @NotNull Template<T> fromFile(@NotNull Path path);

}
