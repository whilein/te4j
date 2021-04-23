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

package te4j.template.compiler.exp.impl;

import lombok.NonNull;
import te4j.template.compiler.exp.AbstractExp;
import te4j.template.compiler.exp.Exp;
import te4j.template.compiler.exp.ExpList;
import te4j.template.compiler.exp.ExpString;
import te4j.template.compiler.exp.filter.ExpDefaultFilters;
import te4j.template.compiler.exp.output.ExpOutput;
import te4j.template.compiler.exp.output.ExpOutputWrite;
import te4j.util.formatter.TextFormatter;

import java.lang.reflect.Array;

public final class ImmutableExpList extends AbstractExp implements ExpList {

    private final Class<?> elementType;
    private final Exp[] inner;

    private ImmutableExpList(Class<?> arrayType, Class<?> elementType, Exp[] inner) {
        super(ExpDefaultFilters.create(arrayType));

        this.elementType = elementType;
        this.inner = inner;
    }

    public static @NonNull ExpList create(Class<?> elementType, @NonNull Exp[] inner) {
        if (elementType == null) {
            elementType = Object.class;
            //elementType = detectType(inner); todo
        }

        return new ImmutableExpList(Array.newInstance(elementType, 0).getClass(), elementType, inner);
    }

    @Override
    public Class<?> getElementType() {
        return elementType;
    }

    @Override
    public Object[] getValues() {
        Object[] result = new Object[inner.length];

        for (int i = 0; i < result.length; i++) {
            Exp element = inner[i];

            if (element instanceof ExpString) {
                result[i] = new TextFormatter(((ExpString) element).getValue()).format();
            } else {
                throw new UnsupportedOperationException(element.getClass().getSimpleName() + " is unsupported, sorry");
            }
        }

        return result;
    }

    @Override
    public void write(ExpOutput output) {
        ExpOutput token = output.next(this);
        token.applyFilters(filters);

        token.append("new ");
        token.append(elementType.getName());
        token.append("[] {");

        try (ExpOutputWrite write = output.startWrite(inner)) {
            do {
                Exp exp = write.current();

                if (write.getIndex() != 0)
                    token.append(",");

                exp.write(token);
            } while (write.moveNext());
        }

        token.append("}");
    }


}
