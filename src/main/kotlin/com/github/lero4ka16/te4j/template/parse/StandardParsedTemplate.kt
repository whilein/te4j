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
package com.github.lero4ka16.te4j.template.parse

import com.github.lero4ka16.te4j.modifiable.watcher.ModifyWatcherManager
import com.github.lero4ka16.te4j.template.Template
import com.github.lero4ka16.te4j.template.compiler.TemplateCompiler
import com.github.lero4ka16.te4j.template.context.TemplateContext
import com.github.lero4ka16.te4j.template.path.TemplatePath
import com.github.lero4ka16.te4j.util.type.ref.ITypeRef
import java.nio.charset.StandardCharsets

/**
 * @author lero4ka16
 */
class StandardParsedTemplate(
    context: TemplateContext, paths: List<TemplatePath>,
    content: ByteArray, offset: Int, length: Int
) : ParsedTemplate(context, content, offset, length) {

    override val paths: List<TemplatePath>

    override fun <BoundType> compile(
        modifyWatcherManager: ModifyWatcherManager?,
        parentFile: String, file: String,
        type: ITypeRef<BoundType>
    ): Template<BoundType> {
        var result = TemplateCompiler.compile(context, this, type, parentFile)

        if (modifyWatcherManager != null) {
            result = Template.wrapHotReloading(modifyWatcherManager, context, result, type, file)
        }
        return result
    }

    override val hasPaths: Boolean
        get() = true

    override fun toString(): String {
        return "Template[content=b(" + String(
            content,
            offset,
            length,
            StandardCharsets.UTF_8
        ) + "), paths=" + paths + "]"
    }

    init {
        require(!paths.isEmpty()) { "paths" }
        this.paths = paths
    }
}