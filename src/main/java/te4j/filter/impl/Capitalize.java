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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import te4j.filter.Filter;

import java.lang.reflect.Type;

/**
 * @author whilein
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Capitalize implements Filter {

    public static @NonNull Filter create() {
        return new Capitalize();
    }

    @Override
    public String getName() {
        return "capitalize";
    }

    @Override
    public Type getWrappedType(@NonNull Type type) {
        return type == String.class ? type : null;
    }

    public static String process(String value) {
        char ch = value.charAt(0);
        char mod = Character.toTitleCase(ch);

        if (ch == mod) return value;

        char[] chars = value.toCharArray();
        chars[0] = mod;

        return new String(chars);
    }
}
