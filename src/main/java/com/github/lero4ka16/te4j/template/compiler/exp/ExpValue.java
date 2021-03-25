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

package com.github.lero4ka16.te4j.template.compiler.exp;

import com.github.lero4ka16.te4j.template.compiler.path.PathAccessor;
import com.github.lero4ka16.te4j.util.type.NullTypeInfo;
import com.github.lero4ka16.te4j.util.type.TypeInfo;

final class ExpValue extends Exp {

    private final PathAccessor accessor;

    private final String value;
    private final TypeInfo objectType;

    private final ExpReturnType type;

    public ExpValue(PathAccessor accessor, ExpNegation negation) {
        this.accessor = accessor;
        this.value = negation.getPrefix() + accessor.getAccessor();

        this.objectType = accessor.getReturnType();

        if (objectType instanceof NullTypeInfo) {
            this.type = ExpReturnType.NULL;
        } else if (objectType.getType() instanceof Class) {
            Class<?> cls = (Class<?>) objectType.getType();

            if (cls == String.class) {
                this.type = ExpReturnType.STRING;
            } else if (cls == boolean.class || cls == Boolean.class) {
                this.type = ExpReturnType.LOGICAL;
            } else if (!cls.isArray() && (cls.isPrimitive() || Number.class.isAssignableFrom(cls))) {
                this.type = ExpReturnType.NUMERICAL;
            } else {
                this.type = ExpReturnType.OBJECT;
            }
        } else {
            this.type = ExpReturnType.OBJECT;
        }
    }

    public PathAccessor getAccessor() {
        return accessor;
    }

    @Override
    public ExpReturnType getReturnType() {
        return type;
    }

    @Override
    public TypeInfo getObjectType() {
        return objectType;
    }

    @Override
    protected void compile(ExpCompile compile) {
        compile.appendFiltered(filter, value);
    }

}
