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

import com.github.lero4ka16.te4j.util.type.GenericInfo
import com.github.lero4ka16.te4j.util.type.TypeInfo
import java.util.EnumSet

class ExpParentheses(private val inner: Array<Exp>) : Exp() {
    override val objectType: TypeInfo
    override val returnType: ExpReturnType?

    fun canOpenParentheses(): Boolean {
        return inner.size == 1
    }

    fun openParentheses(): Exp {
        val result = inner[0]
        if (result is ExpParentheses) {
            if (result.canOpenParentheses()) {
                return result.openParentheses()
            }
        }
        return result
    }

    override fun compile(compile: ExpCompile) {
        compile.append("(")
        val compileInner = ExpCompile(inner, compile.tokens)
        compileInner.compile()
        compile.append(")")
    }

    init {
        if (inner.size != 1) {
            val possibleTypes: MutableSet<ExpReturnType> = EnumSet.noneOf(
                ExpReturnType::class.java
            )
            for (i in inner.indices) {
                val exp = inner[i]
                val next = if (i == inner.size - 1) null else inner[i + 1]
                val prev = if (i == 0) null else inner[i - 1]
                if (exp is ExpOperator) {
                    val opType = exp.operator

                    if (opType.isComparison) {
                        possibleTypes.add(ExpReturnType.LOGICAL)
                        continue
                    }

                    if (prev == null || next == null) {
                        continue
                    }

                    if (opType == Operator.PLUS) {
                        // concat with object
                        if (prev.returnType == ExpReturnType.STRING
                            || next.returnType == ExpReturnType.STRING
                        ) {
                            possibleTypes.add(ExpReturnType.STRING)
                            continue
                        }
                    }

                    if (opType.isNumerical) {
                        // number sum
                        if (prev.returnType == ExpReturnType.NUMERICAL
                            && next.returnType == ExpReturnType.NUMERICAL
                        ) {
                            possibleTypes.add(ExpReturnType.NUMERICAL)
                            continue
                        }
                        throw IllegalStateException("Apply numerical operator to " + prev.returnType + " and " + next.returnType)
                    }
                }

                val returnType = exp.returnType

                if (returnType != null) {
                    possibleTypes.add(returnType)
                }
            }
            returnType = ExpReturnType.getPriorityType(possibleTypes)

            objectType = when (returnType) {
                ExpReturnType.STRING -> GenericInfo.STRING
                ExpReturnType.LOGICAL -> GenericInfo.PRIMITIVE_BOOLEAN
                ExpReturnType.NUMERICAL -> GenericInfo.NUMBER
                else -> throw IllegalStateException()
            }
        } else {
            val exp = inner[0]
            returnType = exp.returnType
            objectType = exp.objectType
        }
    }
}