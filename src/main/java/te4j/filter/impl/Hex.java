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
import java.nio.charset.StandardCharsets;

/**
 * @author whilein
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Hex implements Filter {
    private static final char[] HEX = "0123456789ABCDEF".toCharArray();

    public static @NonNull Filter create() {
        return new Hex();
    }

    @Override
    public String getName() {
        return "hex";
    }

    @Override
    public Type getWrappedType(@NonNull Type type) {
        return type == String.class || type == byte[].class ? String.class : null;
    }

    public static String process(String value) {
        return process(value.getBytes(StandardCharsets.UTF_8));
    }

    public static String process(byte[] value) {
        StringBuilder result = new StringBuilder(value.length * 2);

        for (byte b : value) {
            result.append(HEX[(b >> 4) & 0xF]);
            result.append(HEX[b & 0xF]);
        }

        return result.toString();
    }
}
