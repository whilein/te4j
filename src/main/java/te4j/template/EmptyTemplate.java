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

import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;

/**
 * @author lero4ka16
 */
public final class EmptyTemplate<T> implements Template<T> {

    private final byte[] bytes = new byte[0];
    private final String[] includes = new String[0];

    private static class Singleton {
        @SuppressWarnings("rawtypes")
        private static final Template INSTANCE = new EmptyTemplate();
    }

    @SuppressWarnings("unchecked")
    public static <T> Template<T> getInstance() {
        return Singleton.INSTANCE;
    }

    @Override
    public @NotNull String[] getIncludes() {
        return includes;
    }

    @Override
    public @NotNull String renderAsString(@NotNull Object object) {
        return "";
    }

    @Override
    public byte @NotNull [] renderAsBytes(@NotNull Object object) {
        return bytes;
    }

    @Override
    public void renderTo(@NotNull Object object, @NotNull OutputStream os) {
    }
}
