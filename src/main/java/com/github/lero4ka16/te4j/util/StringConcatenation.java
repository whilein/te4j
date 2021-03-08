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

package com.github.lero4ka16.te4j.util;

import com.github.lero4ka16.te4j.template.compiler.TemplateCompileProcess;
import com.github.lero4ka16.te4j.template.output.TemplateOutputType;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lero4ka16
 */
public final class StringConcatenation {

    private final AtomicInteger textFieldCounter;
    private final Map<Integer, byte[]> fieldTextMap = new HashMap<>();

    private Integer field;

    private final ByteArrayOutputStream textBuffer = new ByteArrayOutputStream();
    private final StringBuilder sb;

    private final int outputType;
    private boolean prevText;

    public StringConcatenation(AtomicInteger textFieldCounter,
                               StringBuilder sb, int outputType) {
        this.textFieldCounter = textFieldCounter;
        this.outputType = outputType;
        this.sb = sb;
    }

    public void generateFields(TemplateCompileProcess<?> process) {
        flushText();

        for (Map.Entry<Integer, byte[]> entry : fieldTextMap.entrySet()) {
            process.addBytes(entry.getKey(), entry.getValue(), outputType);
        }
    }

    public void appendMethod(String method) {
        if (prevText) {
            prevText = false;
            flushText();
        }

        sb.append("out.put(").append(method).append(");");
    }

    public void appendBytes(byte[] text, int off, int len) {
        if (len == 0) return;
        if (text == null) throw new IllegalArgumentException("text");

        if (!prevText) {
            field = textFieldCounter.incrementAndGet();
        }

        prevText = true;
        textBuffer.write(text, off, len);
    }

    public void flushText() {
        if (field != null) {
            fieldTextMap.put(field, textBuffer.toByteArray());
            sb.append("out.write(");
            sb.append(TemplateOutputType.getPrefix(outputType));
            sb.append(field);
            sb.append(");");
            textBuffer.reset();
            field = null;
        }
    }

    public void appendRaw(String raw) {
        if (prevText) {
            prevText = false;
            flushText();
        }

        sb.append(raw);
    }

}
