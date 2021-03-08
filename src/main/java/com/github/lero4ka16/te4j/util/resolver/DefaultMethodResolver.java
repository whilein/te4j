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

package com.github.lero4ka16.te4j.util.resolver;

import java.lang.reflect.Method;

import static com.github.lero4ka16.te4j.util.Utils.getMethod;

public class DefaultMethodResolver implements MethodResolver {

    private final String format;
    private final MethodNameCase nameCase;

    public DefaultMethodResolver(String format, MethodNameCase nameCase) {
        this.format = format;
        this.nameCase = nameCase;
    }

    @Override
    public Method findMethod(String value, Class<?> in) {
        return getMethod(in, String.format(format, nameCase.apply(value)));
    }
}