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

package te4j;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import te4j.template.resolver.TemplateResolver;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class FakeTemplateResolver implements TemplateResolver {

    @NotNull Map<@NotNull String, byte @NotNull []> files;

    public static @NonNull TemplateResolver create(final @NotNull Map<@NotNull String, byte @NotNull []> files) {
        return new FakeTemplateResolver(files);
    }

    @Override
    public @NotNull InputStream resolve(final @NotNull String name) throws IOException {
        val bytes = files.get(name);

        if (bytes == null) {
            throw new FileNotFoundException(name);
        }

        return new ByteArrayInputStream(bytes);
    }
}
