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

import com.github.lero4ka16.te4j.util.type.GenericInfo;
import com.github.lero4ka16.te4j.util.type.TypeInfo;

import java.util.EnumSet;
import java.util.Set;

final class ExpParentheses extends Exp {

    private final Exp[] inner;

    private final TypeInfo objectType;
    private final ExpReturnType returnType;

    public ExpParentheses(Exp[] inner) {
        this.inner = inner;

        if (inner.length != 1) {
            Set<ExpReturnType> possibleTypes = EnumSet.noneOf(ExpReturnType.class);

            for (int i = 0; i < inner.length; i++) {
                Exp exp = inner[i];

                Exp next = i == inner.length - 1 ? null : inner[i + 1];
                Exp prev = i == 0 ? null : inner[i - 1];

                if (exp instanceof ExpOperator) {
                    ExpOperator op = (ExpOperator) exp;
                    Operator opType = op.getOperator();

                    if (opType.isComparison()) {
                        possibleTypes.add(ExpReturnType.LOGICAL);
                        continue;
                    }

                    if (prev == null || next == null) {
                        continue;
                    }

                    if (opType == Operator.PLUS) {
                        // concat with object
                        if (prev.getReturnType() == ExpReturnType.STRING
                                || next.getReturnType() == ExpReturnType.STRING) {
                            possibleTypes.add(ExpReturnType.STRING);

                            continue;
                        }
                    }

                    if (opType.isNumerical()) {
                        // number sum
                        if (prev.getReturnType() == ExpReturnType.NUMERICAL
                                && next.getReturnType() == ExpReturnType.NUMERICAL) {
                            possibleTypes.add(ExpReturnType.NUMERICAL);
                            continue;
                        }

                        throw new IllegalStateException("Apply numerical operator to " + prev.getReturnType() + " and " + next.getReturnType());
                    }
                }

                ExpReturnType returnType = exp.getReturnType();

                if (returnType != null) {
                    possibleTypes.add(returnType);
                }
            }

            this.returnType = ExpReturnType.getPriorityType(possibleTypes);

            if (returnType == null) {
                throw new IllegalStateException();
            }

            switch (returnType) {
                default:
                    throw new IllegalStateException();
                case STRING:
                    objectType = GenericInfo.STRING;
                    break;
                case LOGICAL:
                    objectType = GenericInfo.PRIMITIVE_BOOLEAN;
                    break;
                case NUMERICAL:
                    objectType = GenericInfo.NUMBER;
                    break;
            }
        } else {
            Exp exp = inner[0];
            returnType = exp.getReturnType();
            objectType = exp.getObjectType();
        }
    }

    public boolean canOpenParentheses() {
        return inner.length == 1;
    }

    public Exp openParentheses() {
        Exp result = inner[0];

        if (result instanceof ExpParentheses) {
            ExpParentheses parentheses = (ExpParentheses) result;

            if (parentheses.canOpenParentheses()) {
                return parentheses.openParentheses();
            }
        }

        return result;
    }

    @Override
    public ExpReturnType getReturnType() {
        return returnType;
    }

    @Override
    public TypeInfo getObjectType() {
        return objectType;
    }

    @Override
    protected void compile(ExpCompile compile) {
        compile.append("(");

        ExpCompile compileInner = new ExpCompile(inner, compile.getTokens());
        compileInner.compile();

        compile.append(")");
    }


}