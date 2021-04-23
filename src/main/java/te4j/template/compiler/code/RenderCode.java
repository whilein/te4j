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

package te4j.template.compiler.code;

import te4j.template.compiler.TemplateCompileProcess;
import te4j.template.compiler.path.AbstractCompiledPath;
import te4j.template.parser.ParsedTemplate;
import te4j.template.path.TemplatePath;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lero4ka16
 */
public final class RenderCode {

    private final TemplateCompileProcess<?> process;

    private final Map<Integer, byte[]> fieldTextMap = new HashMap<>();

    private Integer field;
    private boolean prevText;

    private final ByteArrayOutputStream textBuffer = new ByteArrayOutputStream();

    private final StringBuilder out = new StringBuilder();
    private int position = -1;

    public RenderCode(TemplateCompileProcess<?> process) {
        this.process = process;
    }

    public void appendTemplate(ParsedTemplate template) {
        appendTemplate(
                template.getRawContent(), template.getOffset(),
                template.getLength(), template.getPaths()
        );
    }

    public void appendTemplate(byte[] template, int off, int len, List<TemplatePath> paths) {
        List<AbstractCompiledPath> compiled = process.compilePaths(paths);

        int startIndex = off;

        for (AbstractCompiledPath path : compiled) {
            appendTextSegment(template, startIndex, path.getOffset() - startIndex);
            process.writePath(path, this);

            startIndex = path.getOffset() + path.getLength();
        }

        appendTextSegment(template, startIndex, len + off - startIndex);
        flushText();
    }

    public void flushTemplates() {
        for (Map.Entry<Integer, byte[]> entry : fieldTextMap.entrySet()) {
            process.addBytes(entry.getKey(), entry.getValue());
        }
    }

    public RenderCode append(String code) {
        appendCodeSegment(code);
        return this;
    }

    public int position() {
        return position == -1 ? out.length() : position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void resetPosition() {
        this.position = -1;
    }

    public void appendCodeSegment(String codeSegment) {
        if (prevText) {
            prevText = false;
            flushText();
        }

        if (position == -1) {
            out.append(codeSegment);
        } else {
            out.insert(position, codeSegment);
            position += codeSegment.length();
        }
    }

    public void appendTextSegment(ParsedTemplate template) {
        appendTextSegment(template.getRawContent(), template.getOffset(), template.getLength());
    }

    public void appendTextSegment(byte[] buf, int off, int len) {
        if (len == 0) {
            return;
        }

        if (buf == null) {
            throw new IllegalArgumentException("buf is null");
        }

        if (off < 0 || off >= buf.length) {
            throw new IllegalArgumentException("off is less than zero or more than " + buf.length);
        }

        if (len < 0 || len + off > buf.length) {
            throw new IllegalArgumentException("off is less than zero or more than " + (buf.length - off));
        }

        if (!prevText) {
            field = process.getNameCounter().incrementAndGet();
        }

        prevText = true;
        textBuffer.write(buf, off, len);
    }

    public void flushText() {
        if (field != null) {
            fieldTextMap.put(field, textBuffer.toByteArray());
            out.append("out.").append(process.getPutTemplateContent()).append('(');
            out.append(process.getOutputType().getPrefix());
            out.append(field);
            out.append(");");
            textBuffer.reset();
            field = null;
        }
    }

    @Override
    public String toString() {
        return out.toString();
    }
}
