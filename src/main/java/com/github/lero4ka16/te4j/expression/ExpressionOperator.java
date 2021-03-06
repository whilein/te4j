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

import com.github.lero4ka16.te4j.util.type.TypeInfo;

public final class ExpressionOperator extends Expression {

    private final Operator operator;

    public ExpressionOperator(Operator operator) {
        this.operator = operator;
    }

    public Operator getOperator() {
        return operator;
    }

    @Override
    public ExpressionReturnType getReturnType() {
        return null;
    }

    @Override
    public TypeInfo getObjectType() {
        return null;
    }

    @Override
    public void compile(ExpCompile compile) {
        Expression prev = compile.getPrevious();
        Expression next = compile.getNext();

        if (operator.isComparison() && prev != null && next != null) {
            if ((prev.getReturnType() == ExpressionReturnType.OBJECT || prev.getReturnType() == ExpressionReturnType.STRING)
                    && (next.getReturnType() == ExpressionReturnType.OBJECT || next.getReturnType() == ExpressionReturnType.STRING)) {
                StringBuilder token = new StringBuilder();

                if (operator == Operator.NOT_EQUAL) {
                    compile.appendBeforePrevious("!");
                }

                token.append(".equals(");
                token.append(next.compile());
                token.append(")");

                compile.append(token.toString());
                compile.skipNext();

                return;
            }

            if (prev.getReturnType() == ExpressionReturnType.ENUM || next.getReturnType() == ExpressionReturnType.ENUM) {
                TypeInfo prevType = prev.getObjectType();
                TypeInfo nextType = next.getObjectType();

                if (prevType.isEnum()) {
                    compile.append(operator.getOperator());
                    compile.append(prevType.getName() + ".");
                    compile.append(next.compile());
                    compile.skipNext();
                    return;
                }

                if (nextType.isEnum()) {
                    compile.appendBeforePrevious(nextType.getName() + ".");
                    compile.append(operator.getOperator());
                    return;
                }
            }
        }

        compile.append(operator.getOperator());
    }

}
