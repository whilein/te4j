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

import te4j.util.TypeUtils;

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
        this.rawType = TypeUtils.toClass(type);
    }

    @Override
    public final String getSimpleName() {
        return rawType.getSimpleName();
    }

    @Override
    public final String getCanonicalName() {
        return TypeUtils.getCanonicalName(type);
    }

    @Override
    public final Type getRawType() {
        return type;
    }

    @Override
    public final Class<T> getType() {
        return rawType;
    }

}