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

import com.github.lero4ka16.te4j.util.replace.ReplaceStrategy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public abstract class Text {

    protected boolean escaping = true;
    protected int replaceStrategy;

    public Text replaceStrategy(int strategy) {
        this.replaceStrategy = strategy;
        return this;
    }

    public Text disableEscaping() {
        escaping = false;
        return this;
    }

    public byte[] computeAsBytes() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            compute(baos);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    public String computeAsString() {
        StringBuilder sb = new StringBuilder();

        try {
            compute(sb);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    public void compute(Appendable out) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        compute(new TextOutputStream(baos));

        out.append(baos.toString());
    }

    public void compute(OutputStream out) throws IOException {
        compute(new TextOutputStream(out));
    }

    public void compute(TextOutput out) throws IOException {
        boolean insertSpace = false;

        for (int i = 0; i < length(); i++) {
            int ch = charAt(i);

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
                    if ((replaceStrategy & ReplaceStrategy.DEL_LF) != 0) {
                        continue;
                    }

                    if (escaping) {
                        out.write('\\');
                        out.write('n');
                        continue;
                    }
                case '\t':
                    if ((replaceStrategy & ReplaceStrategy.DEL_TAB) != 0) {
                        continue;
                    }

                    if (escaping) {
                        out.write('\\');
                        out.write('t');
                        continue;
                    }
                case '\r':
                    if ((replaceStrategy & ReplaceStrategy.DEL_CR) != 0) {
                        continue;
                    }

                    if (escaping) {
                        out.write('\\');
                        out.write('r');
                        continue;
                    }
                case ' ':
                    if ((replaceStrategy & ReplaceStrategy.DEL_REPEATING_SPACES) != 0) {
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

    public abstract int length();

    public abstract int charAt(int i);

    public static Text of(String text) {
        return new StringText(text);
    }

    public static Text of(byte[] text, int off, int len) {
        return new BinaryText(text, off, len);
    }

    public static Text of(byte[] text) {
        return of(text, 0, text.length);
    }

}
