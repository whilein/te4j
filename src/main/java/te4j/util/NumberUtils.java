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

package te4j.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * @author lero4ka16
 */
@UtilityClass
public class NumberUtils {

    private static @NonNull Number parseNumber(@NonNull String value, int radix) {
        if (value.isEmpty()) {
            throw new IllegalStateException("Number is empty!");
        }

        int lastCharIndex = value.length() - 1;
        char lastChar = value.charAt(lastCharIndex);

        if (radix == 10) {
            // may be floating number

            if (lastChar == 'f' || lastChar == 'F') {
                return Float.valueOf(value.substring(0, lastCharIndex));
            } else if (lastChar == 'd' || lastChar == 'D') {
                return Double.valueOf(value.substring(0, lastCharIndex));
            }
        }

        if (lastChar == 'l' || lastChar == 'L') {
            return Long.parseLong(value.substring(0, lastCharIndex), radix);
        }

        return Integer.parseInt(value, radix);
    }

    public static @NonNull Number parseNumber(@NonNull String value) {
        char ch = value.charAt(0);

        if (ch == '0') {
            char radix = value.charAt(1);

            if (radix == 'x') {
                return parseNumber(value.substring(2), 16); // hex
            } else if (radix == 'b') {
                return parseNumber(value.substring(2), 2); // binary
            } else {
                return parseNumber(value.substring(1), 8); // octal
            }
        }

        return parseNumber(value, 10);
    }

}
