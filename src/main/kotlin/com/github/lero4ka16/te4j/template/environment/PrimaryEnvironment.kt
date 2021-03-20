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
package com.github.lero4ka16.te4j.template.environment

import com.github.lero4ka16.te4j.template.compiler.path.PathAccessor
import com.github.lero4ka16.te4j.template.path.TemplatePathIterator
import com.github.lero4ka16.te4j.util.resolver.DefaultMethodResolver
import com.github.lero4ka16.te4j.util.type.GenericInfo
import java.lang.reflect.Method
import java.lang.reflect.Type

class PrimaryEnvironment(
    private val javaObject: String,
    private val type: Type,
    private val cls: Class<*>) :
    Environment {
    override fun resolve(iterator: TemplatePathIterator): PathAccessor? {
        if (!iterator.hasNext()) {
            return PathAccessor(GenericInfo(type, arrayOf()), javaObject)
        }

        val sb = StringBuilder(javaObject)

        var currentType = cls
        var found: Method? = null

        do {
            sb.append('.')
            val element = iterator.next()
            for (resolver in DefaultMethodResolver.RESOLVERS) {
                found = resolver.findMethod(element, currentType)
                if (found != null) break
            }
            if (found == null) {
                return null
            }
            sb.append(found.name).append("()")
            currentType = found.returnType
        } while (iterator.hasNext())

        return PathAccessor(GenericInfo(found!!.genericReturnType, found.annotations), sb.toString())
    }
}