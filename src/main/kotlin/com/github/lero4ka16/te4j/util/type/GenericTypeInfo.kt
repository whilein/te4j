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

import com.github.lero4ka16.te4j.annotation.ReturnsArrayList
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType
import java.util.ArrayList

/**
 * @author lero4ka16
 */
class GenericInfo : TypeInfo {
    override val type: Type
    override val rawType: Class<*>
    override val componentType: Class<*>?
    override val annotations: Array<Annotation>

    @JvmOverloads
    constructor(
        type: Type,
        rawType: Class<*> = type as Class<*>,
        component: Class<*>? = null,
        annotations: Array<Annotation> = arrayOf()
    ) {
        this.type = type
        this.rawType = rawType
        this.componentType = component
        this.annotations = annotations
    }

    constructor(type: Type, annotations: Array<Annotation>) {
        this.type = type

        if (type is ParameterizedType) {
            val parameterizedType = type

            rawType = parameterizedType.rawType as Class<*>

            componentType = if (Iterable::class.java.isAssignableFrom(rawType)) {
                val genericType = parameterizedType.actualTypeArguments[0]

                if (genericType is WildcardType) {
                    genericType.upperBounds[0] as Class<*>
                } else {
                    genericType as Class<*>
                }
            } else {
                null
            }
        } else {
            rawType = type as Class<*>
            componentType = rawType.componentType
        }

        this.annotations = annotations
    }

    override val isArray: Boolean
        get() {
            return type is Class<*> && type.isArray
        }

    override val isArrayList: Boolean
        get() = ArrayList::class.java.isAssignableFrom(rawType) || isAnnotationPresent(ReturnsArrayList::class.java)

    override fun isAnnotationPresent(annotation: Class<out Annotation>): Boolean {
        return getAnnotation(annotation) != null
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Annotation> getAnnotation(cls: Class<T>): T? {
        for (annotation in annotations) {
            if (annotation.annotationClass == cls) {
                return annotation as T
            }
        }
        return null
    }

    override val name: String
        get() = rawType.name

    override val isEnum: Boolean
        get() = type is Class<*> && type.isEnum

    companion object {
        @JvmField val STRING = GenericInfo(String::class.java)
        @JvmField val NUMBER = GenericInfo(Number::class.java)
        @JvmField val PRIMITIVE_BOOLEAN = GenericInfo(Boolean::class.javaPrimitiveType!!)
        @JvmField val PRIMITIVE_INT = GenericInfo(Int::class.javaPrimitiveType!!)
    }
}