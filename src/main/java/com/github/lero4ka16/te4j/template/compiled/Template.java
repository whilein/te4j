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

package com.github.lero4ka16.te4j.template.compiled;

import com.github.lero4ka16.te4j.template.output.TemplateOutput;
import com.github.lero4ka16.te4j.template.output.TemplateOutputBuffer;
import com.github.lero4ka16.te4j.template.output.TemplateOutputStream;
import com.github.lero4ka16.te4j.template.output.TemplateOutputString;

import java.io.OutputStream;

/**
 * @author lero4ka16
 */
public abstract class Template<BoundType> {

    protected static final ThreadLocal<TemplateOutputBuffer> byteOptimized
            = ThreadLocal.withInitial(TemplateOutputBuffer::new);
    protected static final ThreadLocal<TemplateOutputString> stringOptimized
            = ThreadLocal.withInitial(TemplateOutputString::new);

    public abstract String[] getIncludes();

    public String renderAsString(BoundType object) {
        TemplateOutputString string = stringOptimized.get();
        string.reset();

        render(object, string);
        return string.toString();
    }

    public byte[] renderAsBytes(BoundType object) {
        TemplateOutputBuffer bytes = byteOptimized.get();
        bytes.reset();

        render(object, bytes);
        return bytes.toByteArray();
    }

    public void render(BoundType object, OutputStream os) {
        render(object, new TemplateOutputStream(os));
    }

    // optimized for strings
    public void render(BoundType object, TemplateOutputString out) {
        out.write(new String(renderAsBytes(object)));
    }

    // optimized for stream and buffers
    public void render(BoundType object, TemplateOutput out) {
        if (out instanceof TemplateOutputString) {
            render(object, (TemplateOutputString) out);
        } else {
            throw new UnsupportedOperationException("No implementation for bytes");
        }
    }

}
