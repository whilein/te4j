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

package com.github.lero4ka16.te4j.template.path;

import com.github.lero4ka16.te4j.template.method.TemplateMethod;
import com.github.lero4ka16.te4j.template.method.TemplateMethodType;

/**
 * @author lero4ka16
 */
public class TemplatePath {

    private final int offset;
    private final int length;

    private final TemplateMethod method;

    public TemplatePath(int offset, int length, TemplateMethod method) {
        this.offset = offset;
        this.length = length;
        this.method = method;
    }

    public int getOffset() {
        return offset;
    }

    public int getLength() {
        return length;
    }

    public TemplateMethodType getMethodType() {
        return method.getType();
    }

    @SuppressWarnings("unchecked")
    public <T extends TemplateMethod> T getMethod() {
        return (T) method;
    }

    public String toString() {
        return "Path[method=" + method.toString() + ", off=" + offset + ", len=" + length + "]";
    }

}
