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
import com.github.lero4ka16.te4j.util.EventLocker;
import com.github.lero4ka16.te4j.util.type.ref.TypeRef;
import com.github.lero4ka16.te4j.watcher.FilesWatcherManager;
import com.github.lero4ka16.te4j.watcher.Modifiable;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author lero4ka16
 */
public abstract class Template<BoundType> {

    protected static final ThreadLocal<TemplateOutputBuffer> bytesOptimized
            = ThreadLocal.withInitial(TemplateOutputBuffer::new);
    protected static final ThreadLocal<TemplateOutputString> stringOptimized
            = ThreadLocal.withInitial(TemplateOutputString::new);

    public abstract String[] getIncludes();

    public abstract String renderAsString(BoundType object);

    public abstract byte[] renderAsBytes(BoundType object);

    public abstract void render(BoundType object, OutputStream os) throws IOException;

    public static class HotReloadingWrapper<BoundType> extends Template<BoundType> implements Modifiable {

        private final TemplateContext context;
        private final TypeRef<BoundType> ref;
        private final String thatFile;

        private final EventLocker locker;

        private volatile boolean modified;
        private volatile Template<BoundType> handle;

        public HotReloadingWrapper(TemplateContext context, TypeRef<BoundType> ref,
                                   Template<BoundType> handle, String thatFile) {
            this.handle = handle;
            this.context = context;
            this.ref = ref;
            this.locker = new EventLocker();
            this.thatFile = thatFile;

            FilesWatcherManager.INSTANCE.register(this);
        }

        private Template<BoundType> getHandle() {
            return handle;
        }

        private void updateHandle() {
            locker.lock();
            handle = context.load(ref, thatFile);
            locker.unlock();
        }

        public void setModified() {
            locker.awaitUnlock();
            modified = true;
        }

        private void awaitAndCheck() {
            if (modified) {
                modified = false;

                updateHandle();
            }
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
            files[0] = Paths.get(thatFile);

            for (int i = 0; i < includes.length; i++) {
                files[i + 1] = Paths.get(includes[i]);
            }

            return files;
        }

        @Override
        public String[] getIncludes() {
            locker.awaitUnlock();
            checkModified();
            return getHandle().getIncludes();
        }

        @Override
        public String renderAsString(BoundType object) {
            locker.awaitUnlock();
            checkModified();
            return getHandle().renderAsString(object);
        }

        @Override
        public byte[] renderAsBytes(BoundType object) {
            checkModified();
            return getHandle().renderAsBytes(object);
        }

        @Override
        public void render(BoundType object, OutputStream os) throws IOException {
            checkModified();
            getHandle().render(object, os);
        }


    }

}
