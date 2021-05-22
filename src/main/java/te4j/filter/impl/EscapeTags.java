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

package te4j.filter.impl;

import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import te4j.filter.Filter;

import java.lang.reflect.Type;

/**
 * @author whilein
 */
public final class EscapeTags implements Filter {

    public static @NonNull Filter create() {
        return new EscapeTags();
    }

    public static String process(String value) {
        StringBuilder out = null;

        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);

            if (c == '"' || c == '\'' || c == '<' || c == '>' || c == '&') {
                if (out == null) {
                    out = new StringBuilder(value.length());
                    out.append(value, 0, i);
                }

                out.append("&#");
                out.append((int) c);
                out.append(';');
            } else {
                if (out != null) {
                    out.append(c);
                }
            }
        }

        return out == null ? value : out.toString();
    }

    @Override
    public @NotNull String getName() {
        return "escapetags";
    }

    @Override
    public Type getWrappedType(@NonNull Type type) {
        return type == String.class ? type : null;
    }

}
