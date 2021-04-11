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

package te4j.template.environment;

import te4j.template.compiler.path.PathAccessor;
import te4j.template.path.TemplatePathIterator;
import te4j.util.resolver.DefaultMethodResolver;
import te4j.util.resolver.MethodResolver;
import te4j.util.type.GenericInfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public final class PrimaryEnvironment implements Environment {

    private final String javaObject;

    private final Type type;
    private final Class<?> cls;

    public PrimaryEnvironment(String javaObject, Type type, Class<?> cls) {
        this.javaObject = javaObject;

        this.type = type;
        this.cls = cls;
    }

    @Override
    public final PathAccessor resolve(TemplatePathIterator iterator) {
        if (!iterator.hasNext()) {
            return new PathAccessor(new GenericInfo(type, new Annotation[0]), javaObject);
        }

        Class<?> currentType = cls;
        StringBuilder sb = new StringBuilder(javaObject);

        Method found = null;

        do {
            sb.append('.');

            String element = iterator.next();

            for (MethodResolver resolver : DefaultMethodResolver.RESOLVERS) {
                found = resolver.findMethod(element, currentType);
                if (found != null) break;
            }

            if (found == null) {
                return null;
            }

            sb.append(found.getName()).append("()");
            currentType = found.getReturnType();
        } while (iterator.hasNext());

        return new PathAccessor(new GenericInfo(found.getGenericReturnType(), found.getAnnotations()), sb.toString());
    }

}
