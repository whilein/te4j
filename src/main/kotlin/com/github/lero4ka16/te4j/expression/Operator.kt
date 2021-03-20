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

import java.util.*

enum class Operator(val operator: String) {
    MULTIPLY("*"), PLUS("+"), MINUS("-"), DIVIDE("/"), OR("||"), AND("&&"), BITWISE_OR("|"), BITWISE_XOR("^"), BITWISE_AND(
        "&"
    ),
    BITWISE_LSHIFT("<<"), BITWISE_RSHIFT(">>"), EQUAL("=="), MORE_THAN(">"), MORE_THAN_OR_EQUAL(">="), LESS_THAN("<"), LESS_THAN_OR_EQUAL(
        "<="
    ),
    NOT_EQUAL("!="), REMAINDER("%");

    val isComparison: Boolean
        get() = this == EQUAL || this == NOT_EQUAL
    val isLogical: Boolean
        get() = this == OR || this == AND
    val isNumerical: Boolean
        get() = this == MULTIPLY || this == DIVIDE || this == PLUS || this == MINUS || this == BITWISE_OR || this == BITWISE_AND || this == BITWISE_LSHIFT || this == BITWISE_RSHIFT || this == BITWISE_XOR

    companion object {
        val VALUES: Array<Operator> = values()

        fun isOperator(i: Int): Boolean {
            // в пизду оптимизацию
            return filter(VALUES, 0, i).isNotEmpty()
        }

        fun search(types: Array<Operator>, value: String): Optional<Operator> {
            return Arrays.stream(types)
                .filter { element: Operator -> element.operator == value }
                .findAny()
        }

        fun filter(type: Array<Operator>, position: Int, value: Int): Array<Operator> {
            return Arrays.stream(type)
                .filter { element: Operator -> (element.operator.length > position
                        && element.operator[position].toInt() == value) }
                .toArray { arrayOfNulls<Operator>(it) }
        }
    }
}