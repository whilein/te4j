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

package te4j.template.source;

import te4j.template.Template;
import te4j.template.context.loader.TemplateLoader;

import java.nio.file.Path;
import java.util.Objects;

/**
 * @author lero4ka16
 */
public final class BytesSource implements TemplateSource {

    private final byte[] bytes;

    private BytesSource(byte[] bytes) {
        this.bytes = bytes;
    }

    public static TemplateSource create(byte[] bytes) {
        Objects.requireNonNull(bytes, "bytes");

        return new BytesSource(bytes);
    }

    @Override
    public boolean hasPath() {
        return false;
    }

    @Override
    public Path getPath() {
        return null;
    }

    @Override
    public <T> Template<T> load(TemplateLoader<T> loader) {
        return loader.fromBytes(bytes);
    }

    @Override
    public String toString() {
        return "Bytes[" + new String(bytes) + "]";
    }
}
