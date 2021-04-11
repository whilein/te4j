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

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import te4j.modifiable.Modifiable;
import te4j.modifiable.watcher.ModifyWatcherManager;
import te4j.template.context.TemplateContext;
import te4j.template.output.TemplateOutputBuffer;
import te4j.template.source.TemplateSource;
import te4j.util.type.ref.ITypeRef;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lero4ka16
 */
public abstract class Template<T> {

    @ApiStatus.Internal
    protected static final ThreadLocal<TemplateOutputBuffer> bytesOptimized
            = ThreadLocal.withInitial(TemplateOutputBuffer::new);

    @ApiStatus.Internal
    protected static final ThreadLocal<StringBuilder> stringOptimized
            = ThreadLocal.withInitial(StringBuilder::new);

    public abstract @NotNull String[] getIncludes();

    public abstract @NotNull String renderAsString(@NotNull T object);
    public abstract byte @NotNull [] renderAsBytes(@NotNull T object);

    public abstract void renderTo(@NotNull T object, @NotNull OutputStream os) throws IOException;

    @ApiStatus.Internal
    public static <T> Template<T> wrapHotReloading(@NotNull ModifyWatcherManager modifyWatcherManager,
                                                   @NotNull TemplateContext context,
                                                   @NotNull Template<T> template,
                                                   @NotNull ITypeRef<T> type,
                                                   @NotNull TemplateSource source) {
        return new HotReloadingWrapper<>(modifyWatcherManager, context, type, template, source);
    }

    private static class HotReloadingWrapper<T> extends Template<T> implements Modifiable {

        private final TemplateContext context;
        private final ITypeRef<T> type;
        private final TemplateSource source;

        private volatile boolean locked;
        private volatile Template<T> handle;

        public HotReloadingWrapper(ModifyWatcherManager modifyWatcherManager,
                                   TemplateContext context, ITypeRef<T> type,
                                   Template<T> handle, TemplateSource source) {
            this.handle = handle;
            this.context = context.copy().disableHotReloading().build();
            this.source = source;
            this.type = type;

            modifyWatcherManager.register(this);
        }

        private Template<T> getHandle() {
            return handle;
        }

        public void handleModify() {
            awaitUnlock();

            locked = true;
            handle = source.load(context, type);
            locked = false;

            synchronized (this) {
                notifyAll();
            }
        }

        private void awaitUnlock() {
            if (locked) {
                synchronized (this) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
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
            awaitUnlock();
            return getHandle().getIncludes();
        }

        @Override
        public @NotNull String renderAsString(@NotNull T object) {
            awaitUnlock();
            return getHandle().renderAsString(object);
        }

        @Override
        public byte @NotNull [] renderAsBytes(@NotNull T object) {
            awaitUnlock();
            return getHandle().renderAsBytes(object);
        }

        @Override
        public void renderTo(@NotNull T object, @NotNull OutputStream os) throws IOException {
            awaitUnlock();
            getHandle().renderTo(object, os);
        }


    }

}
