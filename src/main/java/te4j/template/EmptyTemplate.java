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
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class EmptyTemplate<T> implements Template<T> {

    byte[] content;
    String[] includes;

    private static class Singleton {
        @SuppressWarnings("rawtypes")
        private static final Template INSTANCE = new EmptyTemplate(new byte[0], new String[0]);
    }

    @SuppressWarnings("unchecked")
    public static @NotNull <T> Template<T> getInstance() {
        return Singleton.INSTANCE;
    }

    @Override
    public @NotNull String @NotNull [] getIncludes() {
        return includes;
    }

    @Override
    public @NotNull String renderAsString(final @NotNull Object object) {
        return "";
    }

    @Override
    public byte @NotNull [] renderAsBytes(final @NotNull Object object) {
        return content;
    }

    @Override
    public void renderTo(final @NotNull Object object, final @NotNull OutputStream os) {
    }
}
