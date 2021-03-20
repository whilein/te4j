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
package com.github.lero4ka16.te4j.util.type

import java.lang.reflect.Type

/**
 * @author lero4ka16
 */
class NullTypeInfo : TypeInfo {

    override val isArray: Boolean
        get() = false

    override val isArrayList: Boolean
        get() = false

    override val annotations: Array<Annotation>
        get() = arrayOf()

    override fun isAnnotationPresent(annotation: Class<out Annotation>): Boolean {
        return false
    }

    override fun <T : Annotation> getAnnotation(cls: Class<T>): T? {
        return null
    }

    override val name: String
        get() = "null"

    override val type: Type
        get() = rawType

    override val rawType: Class<*>
        get() = Any::class.java

    override val componentType: Class<*>?
        get() = null

    override val isEnum: Boolean
        get() = false

    companion object {
        @JvmField val INSTANCE = NullTypeInfo()
    }
}