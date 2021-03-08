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

package com.github.lero4ka16.te4j.template;

import com.github.lero4ka16.te4j.template.output.TemplateOutputBuffer;
import com.github.lero4ka16.te4j.template.output.TemplateOutputString;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author lero4ka16
 */
public abstract class Template<BoundType> {

    protected static final ThreadLocal<TemplateOutputBuffer> bytesOptimized
            = ThreadLocal.withInitial(TemplateOutputBuffer::new);
    protected static final ThreadLocal<TemplateOutputString> stringOptimized
            = ThreadLocal.withInitial(TemplateOutputString::new);

    public abstract String[] getIncludes();

    public abstract String renderAsString(BoundType object);

    public abstract byte[] renderAsBytes(BoundType object);

    public abstract void render(BoundType object, OutputStream os) throws IOException;

}
