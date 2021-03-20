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
package com.github.lero4ka16.te4j.include

import com.github.lero4ka16.te4j.template.path.TemplatePathIterator
import com.github.lero4ka16.te4j.util.resolver.DefaultMethodResolver
import java.lang.reflect.Method
import java.util.*

/**
 * @author lero4ka16
 */
class IncludeTarget(private val path: String) {
    private val values: MutableList<IncludeParameter>

    private fun add(from: Int, to: Int) {
        if (from == to) return
        values.add(IncludeParameter(this, from, to))
    }

    fun hasValues(): Boolean {
        return values.size != 1
    }

    fun format(): String {
        return path
    }

    fun resolve(path: String, replacement: Any): Any {
        if (path == "$") { // чтобы не делать пустые скобки
            return replacement
        }

        var i = replacement
        val iterator = TemplatePathIterator(path)

        while (iterator.hasNext()) {
            val element = iterator.next()
            var found: Method? = null

            for (resolver in DefaultMethodResolver.RESOLVERS) {
                found = resolver.findMethod(element, i.javaClass)
                if (found != null) break
            }

            checkNotNull(found) { "No path found: $path" }

            i = try {
                found.invoke(replacement)
            } catch (e: Exception) {
                throw IllegalStateException(e)
            }
        }

        return i
    }

    fun format(element: Any): String {
        val sb = StringBuilder()
        for (value in values) {
            if (value.isExpression) {
                sb.append(resolve(value.expression, element))
            } else {
                sb.append(value.value)
            }
        }
        return sb.toString()
    }

    fun substring(begin: Int, end: Int): String {
        return path.substring(begin, end)
    }

    fun charAt(i: Int): Char {
        return path[i]
    }

    override fun toString(): String {
        return path
    }

    init {
        values = ArrayList()

        var begin = 0

        for (i in path.indices) {
            val ch = path[i]
            if (ch == '[') {
                add(begin, i)
                begin = i + 1
            } else if (ch == ']') {
                add(begin - 1, i + 1)
                begin = i + 1
            }
        }

        add(begin, path.length)
    }
}