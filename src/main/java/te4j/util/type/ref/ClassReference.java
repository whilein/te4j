/*
 *    Copyright 2021 Whilein
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

import java.lang.reflect.Type;

/**
 * @author whilein
 */
public final class ClassReference<T> implements TypeReference<T> {

    private final Class<T> cls;

    private ClassReference(Class<T> cls) {
        this.cls = cls;
    }

    public static <T> TypeReference<T> create(Class<T> cls) {
        return new ClassReference<>(cls);
    }

    @Override
    public Type getRawType() {
        return cls;
    }

    @Override
    public Class<T> getType() {
        return cls;
    }

    @Override
    public String getSimpleName() {
        return cls.getSimpleName();
    }

    @Override
    public String getCanonicalName() {
        return cls.getCanonicalName();
    }
}
