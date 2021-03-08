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

import java.util.Arrays;
import java.util.List;

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

    public boolean newline(int ch) {
        return ch <= ' ';
    }

    public void trim() {
        while (length != 0 && newline(content[offset] & 0xFF)) {
            offset++;
            length--;
        }

        while (length != 0 && newline(content[offset + length - 1] & 0xFF)) {
            length--;
        }

        _content = null;
    }

    public byte[] getRawContent() {
        return content;
    }

    public byte[] getContent() {
        if (_content == null) {
            _content = offset != 0 || length != content.length
                    ? Arrays.copyOfRange(content, offset, offset + length)
                    : content;
        }

        return _content;
    }

    public abstract <BoundType> Template<BoundType> compile(String parent, TypeRef<BoundType> type);

    public abstract boolean hasPaths();
    public abstract List<TemplatePath> getPaths();

}
