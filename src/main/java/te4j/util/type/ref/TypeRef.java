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

package te4j.util.type.ref;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author lero4ka16
 */
public abstract class TypeRef<T> implements TypeReference<T> {

    protected final Type type;
    protected final Class<T> rawType;

    protected TypeRef() {
        this.type = ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];
        this.rawType = getClass(type);
    }

    @Override
    public final String getSimpleName() {
        return rawType.getSimpleName();
    }

    @Override
    public final String getCanonicalName() {
        return getCanonicalName(rawType, type);
    }

    @Override
    public final Type getRawType() {
        return type;
    }

    @Override
    public final Class<T> getType() {
        return rawType;
    }

    private static String getCanonicalName(Class<?> cls, Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;

            StringBuilder sb = new StringBuilder(cls.getCanonicalName());
            boolean b = false;

            sb.append('<');
            for (Type param : parameterizedType.getActualTypeArguments()) {
                if (b) {
                    sb.append(',');
                } else {
                    b = true;
                }

                sb.append(getCanonicalName(getClass(param), param));
            }
            sb.append('>');

            return sb.toString();
        }

        return cls.getCanonicalName();
    }


    @SuppressWarnings("unchecked")
    private static <T> Class<T> getClass(Type type) {
        if (type instanceof Class) {
            return (Class<T>) type;
        } else {
            ParameterizedType genericType = (ParameterizedType) type;
            return (Class<T>) genericType.getRawType();
        }
    }

}