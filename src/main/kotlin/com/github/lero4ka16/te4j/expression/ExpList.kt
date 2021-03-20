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

import com.github.lero4ka16.te4j.util.formatter.TextFormatter
import com.github.lero4ka16.te4j.util.type.GenericInfo
import com.github.lero4ka16.te4j.util.type.TypeInfo
import java.lang.StringBuilder

class ExpList(type: Class<*>, inner: Array<Exp>) : Exp() {
    override val objectType: TypeInfo
    private val inner: Array<Exp>

    fun toArray(): Array<Any?> {
        val result = arrayOfNulls<Any>(inner.size)

        for (i in result.indices) {
            var element = inner[i]

            if (element is ExpParentheses) {
                val parentheses = element

                if (parentheses.canOpenParentheses()) {
                    element = parentheses.openParentheses()
                }
            }

            if (element is ExpString) {
                result[i] = TextFormatter(element.value).format()
            } else {
                throw UnsupportedOperationException(element.javaClass.simpleName + " is unsupported, sorry")
            }
        }

        return result
    }

    override val returnType: ExpReturnType
        get() = ExpReturnType.OBJECT

    override fun compile(compile: ExpCompile) {
        val sb = StringBuilder()
        sb.append("new ").append(objectType.componentType!!.name).append("[] {")
        for (i in inner.indices) {
            if (i != 0) sb.append(",")
            sb.append(ExpCompile.singleton(inner[i]))
        }
        sb.append('}')
        compile.appendFiltered(filter, sb.toString())
    }

    init {
        val arrayType: Class<*> = if (type == Any::class.java) {
            Array<Any>::class.java
        } else {
            java.lang.reflect.Array.newInstance(type, 0).javaClass
        }

        this.objectType = GenericInfo(arrayType, arrayType, type)
        this.inner = inner
    }
}