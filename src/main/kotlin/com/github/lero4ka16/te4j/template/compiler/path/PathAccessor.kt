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

import com.github.lero4ka16.te4j.util.type.GenericInfo
import com.github.lero4ka16.te4j.util.type.NullTypeInfo
import com.github.lero4ka16.te4j.util.type.TypeInfo

/**
 * @author lero4ka16
 */
class PathAccessor(val returnType: TypeInfo, val accessor: String) {
    companion object {
        @JvmStatic
        val TRUE = PathAccessor(GenericInfo.PRIMITIVE_BOOLEAN, "true")

        @JvmStatic
        val FALSE = PathAccessor(GenericInfo.PRIMITIVE_BOOLEAN, "false")

        @JvmStatic
        val NULL = PathAccessor(NullTypeInfo.INSTANCE, "null")
    }
}