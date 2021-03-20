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
package com.github.lero4ka16.te4j.util.type.ref

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType

/**
 * @author lero4ka16
 */
abstract class TypeRef<T> : ITypeRef<T> {
    final override val type: Type = (javaClass.genericSuperclass as ParameterizedType)
        .actualTypeArguments[0]

    override val rawType: Class<T> = getClass(type)

    override val simpleName: String
        get() = rawType.simpleName

    override val canonicalName: String
        get() = getCanonicalName(rawType, type)

    companion object {
        private fun getCanonicalName(cls: Class<*>, type: Type): String {
            when (type) {
                is Class<*> -> {
                    return cls.canonicalName
                }
                is ParameterizedType -> {
                    val sb = StringBuilder(cls.canonicalName)
                    var b = false

                    sb.append('<')

                    for (param in type.actualTypeArguments) {
                        if (b) {
                            sb.append(',')
                        } else {
                            b = true
                        }

                        sb.append(getCanonicalName(getClass<Any>(param), param))
                    }

                    sb.append('>')

                    return sb.toString()
                }
                is WildcardType -> {
                    return getCanonicalName(cls, type.upperBounds[0])
                }
                else -> return cls.canonicalName
            }

        }

        @Suppress("UNCHECKED_CAST")
        private fun <T> getClass(type: Type): Class<T> {
            return when (type) {
                is Class<*> -> {
                    type as Class<T>
                }
                is ParameterizedType -> {
                    return getClass(type.rawType)
                }
                is WildcardType -> {
                    return getClass(type.upperBounds[0])
                }
                else -> {
                    throw IllegalStateException("Something went wrong... ${type.javaClass.name}")
                }
            }
        }
    }

}