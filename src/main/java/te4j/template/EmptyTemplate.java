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

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

import java.io.OutputStream;

/**
 * @author lero4ka16
 */
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public final class EmptyTemplate<T> implements Template<T> {

    byte[] content = new byte[0];
    String[] includes = new String[0];

    private static class Singleton {
        @SuppressWarnings("rawtypes")
        private static final Template INSTANCE = new EmptyTemplate();
    }

    @SuppressWarnings("unchecked")
    public static @NonNull <T> Template<T> getInstance() {
        return Singleton.INSTANCE;
    }

    @Override
    public @NonNull String[] getIncludes() {
        return includes;
    }

    @Override
    public @NonNull String renderAsString(@NonNull Object object) {
        return "";
    }

    @Override
    public byte @NonNull [] renderAsBytes(@NonNull Object object) {
        return content;
    }

    @Override
    public void renderTo(@NonNull Object object, @NonNull OutputStream os) {
    }
}
