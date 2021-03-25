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

import com.github.lero4ka16.te4j.util.formatter.TextFormatter;
import com.github.lero4ka16.te4j.util.type.GenericInfo;
import com.github.lero4ka16.te4j.util.type.TypeInfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;

final class ExpList extends Exp {

    private final TypeInfo type;
    private final Exp[] inner;

    public ExpList(Class<?> type, Exp[] inner) {
        Class<?> arrayType;

        if (type == Object.class) {
            arrayType = Object[].class;
        } else {
            arrayType = Array.newInstance(type, 0).getClass();
        }

        this.type = new GenericInfo(arrayType, arrayType, type, new Annotation[0]);
        this.inner = inner;
    }

    public Object[] toArray() {
        Object[] result = new Object[inner.length];

        for (int i = 0; i < result.length; i++) {
            Exp element = inner[i];

            if (element instanceof ExpParentheses) {
                ExpParentheses parentheses = (ExpParentheses) element;

                if (parentheses.canOpenParentheses()) {
                    element = parentheses.openParentheses();
                }
            }

            if (element instanceof ExpString) {
                result[i] = new TextFormatter(((ExpString) element).getValue()).format();
            } else {
                throw new UnsupportedOperationException(element.getClass().getSimpleName() + " is unsupported, sorry");
            }
        }

        return result;
    }

    @Override
    public ExpReturnType getReturnType() {
        return ExpReturnType.OBJECT;
    }

    @Override
    public TypeInfo getObjectType() {
        return type;
    }

    @Override
    protected void compile(ExpCompile compile) {
        StringBuilder sb = new StringBuilder();
        sb.append("new ").append(type.getComponentType().getName()).append("[] {");
        for (int i = 0; i < inner.length; i++) {
            if (i != 0) sb.append(",");
            sb.append(ExpCompile.singleton(inner[i]));
        }

        sb.append('}');
        compile.appendFiltered(filter, sb.toString());
    }


}
