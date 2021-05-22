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
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import te4j.util.lazy.ConcurrentLazy;
import te4j.util.lazy.Lazy;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class PlainTemplate<T> implements Template<T> {

    byte @NotNull [] value;

    int offset;
    int length;

    @NotNull Lazy<@NotNull String> chars;
    @NotNull String @NotNull [] includes;

    public static @NonNull <T> Template<T> create(
            final byte @NonNull [] value,
            final int offset,
            final int length
    ) {
        return new PlainTemplate<>(
                value, offset, length,
                ConcurrentLazy.from(() -> new String(value, offset, length)),
                new String[0]
        );
    }

    @Override
    public @NotNull String @NotNull [] getIncludes() {
        return includes;
    }

    @Override
    public @NotNull String renderAsString(final @NonNull T object) {
        return chars.get();
    }

    @Override
    public byte @NotNull [] renderAsBytes(final @NotNull T object) {
        return Arrays.copyOfRange(value, offset, offset + length);
    }

    @Override
    public void renderTo(final @NonNull T object, final @NonNull OutputStream os) throws IOException {
        os.write(value, offset, offset + length);
    }

}
