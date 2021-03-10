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
import com.github.lero4ka16.te4j.template.compiler.TemplateCompiler;
import com.github.lero4ka16.te4j.template.context.TemplateContext;
import com.github.lero4ka16.te4j.template.path.TemplatePath;
import com.github.lero4ka16.te4j.util.type.ref.ITypeRef;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author lero4ka16
 */
public final class StandardParsedTemplate extends ParsedTemplate {

    private final List<TemplatePath> paths;

    public StandardParsedTemplate(TemplateContext context, List<TemplatePath> paths,
                                  byte[] content, int offset, int length) {
        super(context, content, offset, length);

        if (paths.isEmpty()) {
            throw new IllegalArgumentException("paths");
        }

        this.paths = paths;
    }

    @Override
    public List<TemplatePath> getPaths() {
        return paths;
    }

    @Override
    public <BoundType> Template<BoundType> compile(boolean hotReloading,
                                                   String parentFile, String file,
                                                   ITypeRef<BoundType> type) {
        return TemplateCompiler.getInstance().compile(context, this, hotReloading, type, parentFile, file);
    }

    @Override
    public boolean hasPaths() {
        return true;
    }

    @Override
    public String toString() {
        return "Template[content=b(" + new String(content, offset, length, StandardCharsets.UTF_8) + "), paths=" + paths + "]";
    }
}
