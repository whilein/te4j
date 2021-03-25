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

package com.github.lero4ka16.te4j.template;

import com.github.lero4ka16.te4j.modifiable.Modifiable;
import com.github.lero4ka16.te4j.modifiable.watcher.ModifyWatcherManager;
import com.github.lero4ka16.te4j.template.context.TemplateContext;
import com.github.lero4ka16.te4j.template.output.TemplateOutputBuffer;
import com.github.lero4ka16.te4j.template.output.TemplateOutputString;
import com.github.lero4ka16.te4j.util.type.ref.ITypeRef;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author lero4ka16
 */
public abstract class Template<T> {

    @ApiStatus.Internal
    protected static final ThreadLocal<TemplateOutputBuffer> bytesOptimized
            = ThreadLocal.withInitial(TemplateOutputBuffer::new);

    @ApiStatus.Internal
    protected static final ThreadLocal<TemplateOutputString> stringOptimized
            = ThreadLocal.withInitial(TemplateOutputString::new);

    public abstract @NotNull String[] getIncludes();

    public abstract @NotNull String renderAsString(@NotNull T object);

    public abstract byte @NotNull [] renderAsBytes(@NotNull T object);

    public abstract void renderTo(@NotNull T object, @NotNull OutputStream os) throws IOException;

    @ApiStatus.Internal
    public static <T> Template<T> wrapHotReloading(@NotNull ModifyWatcherManager modifyWatcherManager,
                                                   @NotNull TemplateContext context,
                                                   @NotNull Template<T> template,
                                                   @NotNull ITypeRef<T> type,
                                                   @Nullable String file) {
        return new HotReloadingWrapper<>(modifyWatcherManager, context, type, template, file);
    }

    private static class HotReloadingWrapper<T> extends Template<T> implements Modifiable {

        private final TemplateContext context;
        private final ITypeRef<T> type;
        private final String file;

        private volatile boolean locked;
        private volatile Template<T> handle;

        public HotReloadingWrapper(ModifyWatcherManager modifyWatcherManager,
                                   TemplateContext context, ITypeRef<T> type,
                                   Template<T> handle, String file) {
            this.handle = handle;
            this.context = context;
            this.type = type;
            this.file = file;

            modifyWatcherManager.register(this);
        }

        private Template<T> getHandle() {
            return handle;
        }

        public void handleModify() {
            locked = true;
            handle = context.load(type, file);
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
        public Path[] getFiles() {
            String[] includes = getIncludes();

            boolean hasFile = file != null;
            Path[] files = new Path[includes.length + (hasFile ? 1 : 0)];

            for (int i = 0; i < includes.length; i++) {
                files[i] = Paths.get(includes[i]);
            }

            if (hasFile) {
                files[files.length - 1] = Paths.get(file);
            }

            return files;
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
