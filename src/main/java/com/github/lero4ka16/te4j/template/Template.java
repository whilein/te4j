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

import com.github.lero4ka16.te4j.template.context.TemplateContext;
import com.github.lero4ka16.te4j.template.output.TemplateOutputBuffer;
import com.github.lero4ka16.te4j.template.output.TemplateOutputString;
import com.github.lero4ka16.te4j.util.type.ref.TypeRef;
import com.github.lero4ka16.te4j.watcher.FilesWatcherManager;
import com.github.lero4ka16.te4j.watcher.Modifiable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author lero4ka16
 */
public abstract class Template<BoundType> {

    @ApiStatus.Internal
    protected static final ThreadLocal<TemplateOutputBuffer> bytesOptimized
            = ThreadLocal.withInitial(TemplateOutputBuffer::new);

    @ApiStatus.Internal
    protected static final ThreadLocal<TemplateOutputString> stringOptimized
            = ThreadLocal.withInitial(TemplateOutputString::new);

    public abstract @NotNull String[] getIncludes();

    public abstract @NotNull String renderAsString(@NotNull BoundType object);

    public abstract @NotNull byte[] renderAsBytes(@NotNull BoundType object);

    public abstract void render(@NotNull BoundType object, @NotNull OutputStream os) throws IOException;

    @ApiStatus.Internal
    public HotReloadingWrapper<BoundType> enableHotReloading(TemplateContext context, TypeRef<BoundType> ref,
                                                             String file) {
        return new HotReloadingWrapper<>(context, ref, this, file);
    }

    private static class HotReloadingWrapper<BoundType> extends Template<BoundType> implements Modifiable {

        private final TemplateContext context;
        private final TypeRef<BoundType> ref;
        private final String file;

        private volatile boolean locked;
        private volatile boolean modified;

        private volatile Template<BoundType> handle;

        public HotReloadingWrapper(TemplateContext context, TypeRef<BoundType> ref,
                                   Template<BoundType> handle, String file) {
            this.handle = handle;
            this.context = context;
            this.ref = ref;
            this.file = file;

            FilesWatcherManager.INSTANCE.register(this);
        }

        private Template<BoundType> getHandle() {
            return handle;
        }

        private void updateHandle() {
            locked = true;
            handle = context.load(ref, file);
            locked = false;

            synchronized (this) {
                notifyAll();
            }
        }

        public void setModified() {
            awaitUnlock();
            modified = true;
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

        private void awaitAndCheck() {
            awaitUnlock();
            checkModified();
        }

        private void checkModified() {
            if (modified) {
                modified = false;

                updateHandle();
            }
        }

        @Override
        public Path[] getFiles() {
            String[] includes = getIncludes();

            Path[] files = new Path[includes.length + 1];
            files[0] = Paths.get(file);

            for (int i = 0; i < includes.length; i++) {
                files[i + 1] = Paths.get(includes[i]);
            }

            return files;
        }

        @Override
        public @NotNull String[] getIncludes() {
            awaitAndCheck();
            return getHandle().getIncludes();
        }

        @Override
        public @NotNull String renderAsString(@NotNull BoundType object) {
            awaitAndCheck();
            return getHandle().renderAsString(object);
        }

        @Override
        public @NotNull byte[] renderAsBytes(@NotNull BoundType object) {
            awaitAndCheck();
            return getHandle().renderAsBytes(object);
        }

        @Override
        public void render(@NotNull BoundType object, @NotNull OutputStream os) throws IOException {
            awaitAndCheck();
            getHandle().render(object, os);
        }


    }

}
