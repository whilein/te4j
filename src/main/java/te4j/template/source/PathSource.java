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

package te4j.template.source;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import te4j.template.Template;
import te4j.template.context.loader.TemplateLoader;

import java.nio.file.Path;
import java.util.Optional;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class PathSource implements TemplateSource {

    Path path;

    public static @NonNull TemplateSource create(@NonNull Path path) {
        return new PathSource(path.toAbsolutePath());
    }

    @Override
    public Optional<Path> getPath() {
        return Optional.of(path);
    }

    @Override
    public <T> Template<T> load(TemplateLoader<T> loader) {
        return loader.fromFile(path);
    }

    @Override
    public String toString() {
        return "Source[path=" + path + "]";
    }
}
