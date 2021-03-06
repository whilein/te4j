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

package com.github.lero4ka16.te4j.template.compiled.path;

import com.github.lero4ka16.te4j.template.method.TemplateMethod;
import com.github.lero4ka16.te4j.template.method.TemplateMethodType;
import com.github.lero4ka16.te4j.template.path.TemplatePath;
import com.github.lero4ka16.te4j.util.type.TypeInfo;

/**
 * @author lero4ka16
 */
public abstract class AbstractCompiledPath {

    private final String id;
    private final TemplatePath original;

    public AbstractCompiledPath(String id, TemplatePath original) {
        this.id = id;
        this.original = original;
    }

    public String getId() {
        return id;
    }

    public TemplatePath getOriginal() {
        return original;
    }

    public int getOffset() {
        return original.getOffset();
    }

    public int getLength() {
        return original.getLength();
    }

    public TemplateMethodType getMethodType() {
        return original.getMethodType();
    }

    public <T extends TemplateMethod> T getMethod() {
        return original.getMethod();
    }

    public boolean isStream() {
        return false;
    }

    public abstract TypeInfo getReturnType();
    public abstract String getAccessorValue();

}
