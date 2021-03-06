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

package com.github.lero4ka16.te4j.template.environment;

import com.github.lero4ka16.te4j.template.compiled.path.PathAccessor;
import com.github.lero4ka16.te4j.template.path.TemplatePathIterator;
import com.github.lero4ka16.te4j.util.type.GenericInfo;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import static com.github.lero4ka16.te4j.util.Utils.getMethod;
import static com.github.lero4ka16.te4j.util.Utils.toCamelCase;

public class DefaultEnvironment implements Environment {

    private final String javaObject;

    private final Type type;
    private final Class<?> cls;

    public DefaultEnvironment(String javaObject, Type type, Class<?> cls) {
        this.javaObject = javaObject;

        this.type = type;
        this.cls = cls;
    }

    @Override
    public final PathAccessor resolve(TemplatePathIterator iterator) {
        if (!iterator.hasNext()) {
            return new PathAccessor(new GenericInfo(type), javaObject, false);
        }

        Class<?> currentType = cls;
        StringBuilder sb = new StringBuilder(javaObject);

        boolean stream;
        Method found;

        do {
            sb.append('.');

            String element = iterator.next();
            String upperCamelCase_1 = "get" + toCamelCase(true, element);
            String upperCamelCase_2 = "is" + toCamelCase(true, element);

            String lowerCamelCase = toCamelCase(false, element);

            String name;

            if ((found = getMethod(currentType, upperCamelCase_1)) != null) {
                name = upperCamelCase_1;
            } else if ((found = getMethod(currentType, upperCamelCase_2)) != null) {
                name = upperCamelCase_2;
            } else if ((found = getMethod(currentType, lowerCamelCase)) != null) {
                name = lowerCamelCase;
            } else {
                return null;
            }

            sb.append(name);

            stream = found.getParameterCount() == 1 && OutputStream.class.isAssignableFrom(found.getParameterTypes()[0]);

            if (stream) {
                sb.append("(out)");
            } else {
                sb.append("()");
            }

            currentType = found.getReturnType();
        } while (iterator.hasNext());

        return new PathAccessor(new GenericInfo(found.getGenericReturnType()), sb.toString(), stream);
    }
}
