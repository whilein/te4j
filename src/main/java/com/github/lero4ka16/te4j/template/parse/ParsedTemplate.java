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

package com.github.lero4ka16.te4j.template.parse;

import com.github.lero4ka16.te4j.template.Template;
import com.github.lero4ka16.te4j.template.context.TemplateContext;
import com.github.lero4ka16.te4j.template.path.TemplatePath;
import com.github.lero4ka16.te4j.util.type.ref.TypeRef;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.IntPredicate;

/**
 * @author lero4ka16
 */
public abstract class ParsedTemplate {

    protected final TemplateContext context;

    protected final byte[] content;

    protected int offset;
    protected int length;

    private byte[] _content;

    public ParsedTemplate(TemplateContext context, byte[] content,
                          int offset, int length) {
        if (offset < 0 || offset >= content.length) {
            throw new IllegalArgumentException("offset must be between 0 and "
                    + content.length + " inclusive, but " + offset);
        }

        if (length < 0 || offset + length > content.length) {
            throw new IllegalArgumentException("length must be between 0 and "
                    + (content.length - offset) + ", but " + length);
        }

        this.context = context;
        this.content = content;
        this.offset = offset;
        this.length = length;

        trim();
    }

    public int getOffset() {
        return offset;
    }

    public int getLength() {
        return length;
    }

    public TemplateContext getContext() {
        return context;
    }

    private boolean hasNewlines() {
        for (int i = 0; i < length; i++) {
            if (content[offset + i] == '\n') {
                return true;
            }
        }

        return false;
    }

    public void trim() {
        boolean inline = !hasNewlines();
        if (inline) return;

        // trim spaces until crlf
        trim(this::space);
        // trim beginning crlf
        trim(this::crlf);

        _content = null;
    }

    private boolean space(int value) {
        return value == ' ' || value == '\t';
    }

    private boolean crlf(int value) {
        return value == '\r' || value == '\n';
    }

    private void trim(IntPredicate value) {
        while (length != 0 && value.test(content[offset] & 0xFF)) {
            offset++;
            length--;
        }
    }

    public byte @NotNull [] getRawContent() {
        return content;
    }

    public byte @NotNull [] getContent() {
        if (_content == null) {
            _content = offset != 0 || length != content.length
                    ? Arrays.copyOfRange(content, offset, offset + length)
                    : content;
        }

        return _content;
    }

    public abstract <BoundType> Template<BoundType> compile(boolean hotReloading,
                                                            String parentFile, String file,
                                                            TypeRef<BoundType> type);

    public abstract boolean hasPaths();

    public abstract List<TemplatePath> getPaths();

}
