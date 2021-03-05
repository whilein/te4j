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

package com.github.lero4ka16.te4j.util.type.info;

import lombok.RequiredArgsConstructor;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

@RequiredArgsConstructor
public class GenericInfo implements TypeInfo {

    public static final GenericInfo STRING = new GenericInfo(String.class, String.class, null);
    public static final GenericInfo NUMBER = new GenericInfo(Number.class, Number.class, null);
    public static final GenericInfo PRIMITIVE_BOOLEAN = new GenericInfo(boolean.class, boolean.class, null);

    private final Type type;

    private final Class<?> rawType;
    private final Class<?> component;

    public GenericInfo(Type type) {
        this.type = type;

        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            rawType = (Class<?>) parameterizedType.getRawType();

            if (Iterable.class.isAssignableFrom(rawType)) {
                Type genericType = parameterizedType.getActualTypeArguments()[0];

                if (genericType instanceof WildcardType) {
                    component = (Class<?>) ((WildcardType) genericType).getUpperBounds()[0];
                } else {
                    component = (Class<?>) genericType;
                }
            } else {
                component = null;
            }
        } else {
            rawType = (Class<?>) type;
            component = rawType.getComponentType();
        }
    }

    @Override
    public boolean isArray() {
        return type instanceof Class && ((Class<?>) type).isArray();
    }

    @Override
    public String getName() {
        return rawType.getName();
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Class<?> getComponentType() {
        return component;
    }

    @Override
    public boolean isEnum() {
        return type instanceof Class && ((Class<?>) type).isEnum();
    }
}
