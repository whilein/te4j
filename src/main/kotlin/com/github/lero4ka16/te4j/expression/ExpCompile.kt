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

import com.github.lero4ka16.te4j.Te4j.Companion.getFilters
import java.util.*

class ExpCompile(val expression: Array<Exp>, val tokens: LinkedList<String>) {
    var position = 0
        private set

    fun skipNext() {
        position++
    }

    val previous: Exp?
        get() = if (position == 0) {
            null
        } else {
            expression[position - 1]
        }

    val next: Exp?
        get() = if (position == expression.size - 1) {
            null
        } else {
            expression[position + 1]
        }

    fun compile() {
        while (position < expression.size) {
            expression[position].compile(this)
            position++
        }
    }

    override fun toString(): String {
        return java.lang.String.join("", tokens)
    }

    fun appendBeforePrevious(value: String) {
        // костыль :)))
        val removed = tokens.removeLast()
        tokens.addLast(value)
        tokens.addLast(removed)
    }

    fun append(value: String) {
        tokens.add(value)
    }

    fun appendFiltered(filter: String?, value: String) {
        append(getFilters().applyFilters(filter, value))
    }

    companion object {
        fun singleton(exp: Exp): String {
            val compile = ExpCompile(arrayOf(exp), LinkedList())
            exp.compile(compile)

            return compile.toString()
        }
    }
}