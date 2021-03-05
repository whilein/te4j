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

package com.github.lero4ka16.te4j.util.expression;

import com.github.lero4ka16.te4j.util.type.info.TypeInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ExpOp extends Exp {

    private final ExpOpType type;

    @Override
    public ExpReturnType getReturnType() {
        return null;
    }

    @Override
    public TypeInfo getObjectType() {
        return null;
    }

    @Override
    public void compile(ExpCompile compile) {
        Exp prev = compile.getPrevious();
        Exp next = compile.getNext();

        if (type.isComparison() && prev != null && next != null) {
            if ((prev.getReturnType() == ExpReturnType.OBJECT || prev.getReturnType() == ExpReturnType.STRING)
                    && (next.getReturnType() == ExpReturnType.OBJECT || next.getReturnType() == ExpReturnType.STRING)) {
                StringBuilder token = new StringBuilder();

                if (type == ExpOpType.NOT_EQUAL) {
                    compile.appendBeforePrevious("!");
                }

                token.append(".equals(");
                token.append(next.compile());
                token.append(")");

                compile.append(token.toString());
                compile.skipNext();

                return;
            }

            if (prev.getReturnType() == ExpReturnType.ENUM || next.getReturnType() == ExpReturnType.ENUM) {
                TypeInfo prevType = prev.getObjectType();
                TypeInfo nextType = next.getObjectType();

                if (prevType.isEnum()) {
                    compile.append(type.getOperator());
                    compile.append(prevType.getName() + ".");
                    compile.append(next.compile());
                    compile.skipNext();
                    return;
                }

                if (nextType.isEnum()) {
                    compile.appendBeforePrevious(nextType.getName() + ".");
                    compile.append(type.getOperator());
                    return;
                }
            }
        }

        compile.append(type.getOperator());
    }

}
