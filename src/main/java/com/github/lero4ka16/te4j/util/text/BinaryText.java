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
import java.io.OutputStream;

public class BinaryText extends Text {

    private final byte[] value;
    private final int off, len;

    public BinaryText(byte[] value, int off, int len) {
        this.value = value;

        this.off = off;
        this.len = len;
    }

    @Override
    public void compute(OutputStream out) throws IOException {
        boolean insertSpace = false;

        for (int i = 0; i < len; i++) {
            int ch = value[i + off];

            if (insertSpace && ch != ' ') {
                out.write(' ');
                insertSpace = false;
            }

            switch (ch) {
                case '"':
                    if (escaping) {
                        out.write('\\');
                        out.write(ch);
                        continue;
                    }

                    break;
                case '\\':
                    if (escaping) {
                        out.write(ch);
                        out.write(ch);
                        continue;
                    }

                    break;
                case '\n':
                    if (replaceStrategy.isRemoveLineFeed()) {
                        continue;
                    }

                    if (escaping) {
                        out.write('\\');
                        out.write('n');
                        continue;
                    }
                case '\t':
                    if (replaceStrategy.isRemoveTab()) {
                        continue;
                    }

                    if (escaping) {
                        out.write('\\');
                        out.write('t');
                        continue;
                    }
                case '\r':
                    if (replaceStrategy.isRemoveCarriageReturn()) {
                        continue;
                    }

                    if (escaping) {
                        out.write('\\');
                        out.write('n');
                        continue;
                    }
                case ' ':
                    if (replaceStrategy.isRemoveRepeatingSpaces()) {
                        insertSpace = true;
                        continue;
                    }
                    break;
            }

            out.write(ch);
        }

        if (insertSpace) {
            out.write(' ');
        }
    }
}
