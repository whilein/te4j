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
package com.github.lero4ka16.te4j.template.compiler.path

import com.github.lero4ka16.te4j.template.path.TemplatePath
import com.github.lero4ka16.te4j.util.type.TypeInfo

/**
 * @author lero4ka16
 */
class DefaultCompiledPath(
    id: String, original: TemplatePath,
    private val value: PathAccessor
) : AbstractCompiledPath(id, original) {

    override val returnType: TypeInfo
        get() = value.returnType

    override val accessorValue: String
        get() = value.accessor
}