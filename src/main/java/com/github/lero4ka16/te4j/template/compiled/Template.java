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

import java.io.OutputStream;

/**
 * @author lero4ka16
 */
public abstract class Template<BoundType> {

    private final TemplateOutputBuffer buffer = new TemplateOutputBuffer();

    public abstract String[] getIncludes();

    public synchronized String renderAsString(BoundType object) {
        try {
            render(object, buffer);
            return buffer.toString();
        } finally {
            buffer.reset();
        }
    }

    public synchronized byte[] renderAsBytes(BoundType object) {
        try {
            render(object, buffer);
            return buffer.toByteArray();
        } finally {
            buffer.reset();
        }
    }

    public void render(BoundType object, OutputStream os) {
        render(object, new TemplateOutputStream(os));
    }

    public abstract void render(BoundType object, TemplateOutput out);

}
