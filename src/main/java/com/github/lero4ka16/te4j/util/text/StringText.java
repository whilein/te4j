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

package com.github.lero4ka16.te4j.util.text;

import java.io.IOException;

public class StringText extends Text {

    private final String value;

    public StringText(String value) {
        this.value = value;
    }

    @Override
    public void compute(Appendable out) throws IOException {
        boolean insertSpace = false;

        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);

            if (insertSpace && ch != ' ') {
                out.append(' ');
                insertSpace = false;
            }

            switch (ch) {
                case '"':
                    if (escaping) {
                        out.append('\\');
                        out.append(ch);
                        continue;
                    }

                    break;
                case '\\':
                    if (escaping) {
                        out.append(ch);
                        out.append(ch);
                        continue;
                    }

                    break;
                case '\n':
                    if (replaceStrategy.isRemoveLineFeed()) {
                        continue;
                    }

                    if (escaping) {
                        out.append('\\');
                        out.append('n');
                        continue;
                    }
                case '\t':
                    if (replaceStrategy.isRemoveTab()) {
                        continue;
                    }

                    if (escaping) {
                        out.append('\\');
                        out.append('t');
                        continue;
                    }
                case '\r':
                    if (replaceStrategy.isRemoveCarriageReturn()) {
                        continue;
                    }

                    if (escaping) {
                        out.append('\\');
                        out.append('n');
                        continue;
                    }
                case ' ':
                    if (replaceStrategy.isRemoveRepeatingSpaces()) {
                        insertSpace = true;
                        continue;
                    }
                    break;
            }

            out.append(ch);
        }

        if (insertSpace) {
            out.append(' ');
        }
    }

}
