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
import java.io.OutputStreamWriter;
import java.io.Writer;

public abstract class Text {

    protected boolean escaping = true;
    protected ReplaceStrategy replaceStrategy = ReplaceStrategy.NONE;

    public Text replaceStrategy(ReplaceStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("strategy");

        }
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
        compute(baos);

        out.append(baos.toString());
    }

    public void compute(OutputStream out) throws IOException {
        try (Writer writer = new OutputStreamWriter(out)) {
            compute(writer);
        }
    }

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
