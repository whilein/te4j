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
import com.github.lero4ka16.te4j.template.PlainTemplate
import com.github.lero4ka16.te4j.template.Template
import com.github.lero4ka16.te4j.template.context.TemplateContext
import com.github.lero4ka16.te4j.template.path.TemplatePath
import com.github.lero4ka16.te4j.util.type.ref.ITypeRef
import java.nio.charset.StandardCharsets

/**
 * @author lero4ka16
 */
class PlainParsedTemplate(
    context: TemplateContext,
    content: ByteArray,
    offset: Int,
    length: Int
) : ParsedTemplate(context, content, offset, length) {

    override fun <BoundType> compile(
        modifyWatcherManager: ModifyWatcherManager?,
        parentFile: String, file: String,
        type: ITypeRef<BoundType>
    ): Template<BoundType> {
        var result: Template<BoundType> = PlainTemplate(content, offset, length)

        if (modifyWatcherManager != null) {
            result = Template.wrapHotReloading(modifyWatcherManager, context, result, type, file)
        }

        return result
    }

    override val hasPaths: Boolean
        get() = false

    override val paths: List<TemplatePath>
        get() = emptyList()

    override fun toString(): String {
        return "Template.Plain[${String(content, offset, length, StandardCharsets.UTF_8)}]"
    }
}