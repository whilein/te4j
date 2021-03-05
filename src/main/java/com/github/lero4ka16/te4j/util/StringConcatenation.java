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

import com.github.lero4ka16.te4j.template.compiled.TemplateCompileProcess;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lero4ka16
 */
@RequiredArgsConstructor
public final class StringConcatenation {

    private final AtomicInteger textFieldCounter;
    private final Map<String, byte[]> fieldTextMap = new HashMap<>();

    private String field;

    private final ByteArrayOutputStream textBuffer = new ByteArrayOutputStream();
    private final StringBuilder sb;

    private boolean prevText;

    public void generateFields(TemplateCompileProcess<?> process) {
        saveText();

        for (Map.Entry<String, byte[]> entry : fieldTextMap.entrySet()) {
            process.addBytes(entry.getKey(), entry.getValue());
        }
    }

    public void appendMethod(String method) {
        if (prevText) {
            prevText = false;
            saveText();
        }

        sb.append("out.put(").append(method).append(");");
    }

    public void appendBytes(byte[] text, int off, int len) {
        if (len == 0) return;
        if (text == null) throw new IllegalArgumentException("text");

        if (!prevText) {
            field = "_" + textFieldCounter.incrementAndGet();
        }

        prevText = true;
        textBuffer.write(text, off, len);
    }

    public void saveText() {
        if (field != null) {
            fieldTextMap.put(field, textBuffer.toByteArray());
            sb.append("out.write(").append(field).append(");");
            textBuffer.reset();
            field = null;
        }
    }

    public void appendRaw(String raw) {
        if (prevText) {
            prevText = false;
            saveText();
        }

        sb.append(raw);
    }

}
