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
package com.github.lero4ka16.te4j.expression

import com.github.lero4ka16.te4j.template.compiler.path.PathAccessor
import com.github.lero4ka16.te4j.util.type.NullTypeInfo
import com.github.lero4ka16.te4j.util.type.TypeInfo

class ExpValue(val accessor: PathAccessor, negation: ExpNegation) : Exp() {
    private val value: String = negation.prefix + accessor.accessor
    override val objectType: TypeInfo = accessor.returnType

    override var returnType: ExpReturnType? = null

    override fun compile(compile: ExpCompile) {
        compile.appendFiltered(filter, value)
    }

    init {

        when {
            objectType is NullTypeInfo -> {
                returnType = ExpReturnType.NULL
            }

            objectType.type is Class<*> -> {
                val cls = objectType.type as Class<*>

                returnType = if (cls == String::class.java) {
                    ExpReturnType.STRING
                } else if (cls == Boolean::class.javaPrimitiveType || cls == Boolean::class.java) {
                    ExpReturnType.LOGICAL
                } else if (!cls.isArray && (cls.isPrimitive || Number::class.java.isAssignableFrom(cls))) {
                    ExpReturnType.NUMERICAL
                } else {
                    ExpReturnType.OBJECT
                }
            }

            else -> {
                returnType = ExpReturnType.OBJECT
            }
        }
    }
}