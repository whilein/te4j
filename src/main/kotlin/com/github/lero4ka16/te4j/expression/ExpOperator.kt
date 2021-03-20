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

import com.github.lero4ka16.te4j.util.type.TypeInfo

class ExpOperator(val operator: Operator) : Exp() {
    override val returnType: ExpReturnType?
        get() = null
    override val objectType: TypeInfo
        get() = throw UnsupportedOperationException()

    override fun compile(compile: ExpCompile) {
        val prev = compile.previous
        val next = compile.next

        if (operator.isComparison && prev != null && next != null) {
            if ((prev.returnType == ExpReturnType.OBJECT || prev.returnType == ExpReturnType.STRING)
                && (next.returnType == ExpReturnType.OBJECT || next.returnType == ExpReturnType.STRING)
            ) {
                val token = StringBuilder()
                if (operator == Operator.NOT_EQUAL) {
                    compile.appendBeforePrevious("!")
                }
                token.append(".equals(")
                token.append(next.compile())
                token.append(")")
                compile.append(token.toString())
                compile.skipNext()
                return
            }
            if (prev.returnType == ExpReturnType.ENUM || next.returnType == ExpReturnType.ENUM) {
                val prevType = prev.objectType
                val nextType = next.objectType

                if (prevType.isEnum) {
                    compile.append(operator.operator)
                    compile.append(prevType.name + ".")
                    compile.append(next.compile())
                    compile.skipNext()
                    return
                }

                if (nextType.isEnum) {
                    compile.appendBeforePrevious(nextType.name + ".")
                    compile.append(operator.operator)
                    return
                }
            }
        }
        compile.append(operator.operator)
    }
}