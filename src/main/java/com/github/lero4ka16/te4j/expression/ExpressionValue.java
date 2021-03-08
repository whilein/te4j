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

package com.github.lero4ka16.te4j.expression;

import com.github.lero4ka16.te4j.template.compiler.path.PathAccessor;
import com.github.lero4ka16.te4j.util.type.NullTypeInfo;
import com.github.lero4ka16.te4j.util.type.TypeInfo;

public final class ExpressionValue extends Expression {

    private final PathAccessor accessor;

    private final String value;
    private final TypeInfo objectType;

    private final ExpressionReturnType type;

    public ExpressionValue(PathAccessor accessor, ExpressionNegation negation) {
        this.accessor = accessor;
        this.value = negation.getPrefix() + accessor.getAccessor();

        this.objectType = accessor.getReturnType();

        if (objectType instanceof NullTypeInfo) {
            this.type = ExpressionReturnType.NULL;
        } else if (objectType.getType() instanceof Class) {
            Class<?> cls = (Class<?>) objectType.getType();

            if (cls == String.class) {
                this.type = ExpressionReturnType.STRING;
            } else if (cls == boolean.class || cls == Boolean.class) {
                this.type = ExpressionReturnType.LOGICAL;
            } else if (!cls.isArray() && (cls.isPrimitive() || Number.class.isAssignableFrom(cls))) {
                this.type = ExpressionReturnType.NUMERICAL;
            } else {
                this.type = ExpressionReturnType.OBJECT;
            }
        } else {
            this.type = ExpressionReturnType.OBJECT;
        }
    }

    public PathAccessor getAccessor() {
        return accessor;
    }

    @Override
    public ExpressionReturnType getReturnType() {
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
