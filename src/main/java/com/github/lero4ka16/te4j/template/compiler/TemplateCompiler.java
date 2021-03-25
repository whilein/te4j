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

package com.github.lero4ka16.te4j.template.compiler;

import com.github.lero4ka16.te4j.template.Template;
import com.github.lero4ka16.te4j.template.context.TemplateContext;
import com.github.lero4ka16.te4j.template.exception.TemplateException;
import com.github.lero4ka16.te4j.template.parse.ParsedTemplate;
import com.github.lero4ka16.te4j.util.type.ref.ITypeRef;
import org.jetbrains.annotations.NotNull;

/**
 * @author lero4ka16
 */
public class TemplateCompiler {

    public static final TemplateCompiler INSTANCE = new TemplateCompiler();

    public <T> Template<T> compile(@NotNull TemplateContext context,
                                   @NotNull ParsedTemplate template,
                                   @NotNull ITypeRef<T> type,
                                   @NotNull String parentFile) {
        try {
            return new TemplateCompileProcess<>(context, template, type, parentFile)
                    .compile();
        } catch (Exception e) {
            throw new TemplateException("Cannot compile template for " + type.getSimpleName(), e);
        }
    }

}
