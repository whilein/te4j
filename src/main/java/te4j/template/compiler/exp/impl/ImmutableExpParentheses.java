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

import lombok.Getter;
import lombok.NonNull;
import te4j.template.compiler.ExpOperator;
import te4j.template.compiler.exp.AbstractExp;
import te4j.template.compiler.exp.Exp;
import te4j.template.compiler.exp.ExpParentheses;
import te4j.template.compiler.exp.Operator;
import te4j.template.compiler.exp.filter.ExpDefaultFilters;
import te4j.template.compiler.exp.output.ExpOutput;
import te4j.template.compiler.exp.output.ExpOutputWrite;
import te4j.util.TypeUtils;

import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * @author lero4ka16
 */
@Getter
public final class ImmutableExpParentheses extends AbstractExp implements ExpParentheses {

    private final Exp[] inner;
    private final Type type;

    private ImmutableExpParentheses(Exp[] inner, Type type) {
        super(ExpDefaultFilters.create(type));

        this.inner = inner;
        this.type = type;
    }

    private static Class<?> getNumericalType(@NonNull Exp[] inner) {
        return doIteration(null, inner, (current, next, prev) -> {
            if (prev == null || next == null) {
                return null;
            }

            if (current instanceof ExpOperator) {
                ExpOperator operator = (ExpOperator) current;

                if (operator.getOperator().isNumerical()) {
                    Type prevType = prev.getType();
                    Type nextType = next.getType();

                    if (prevType instanceof Class<?> && nextType instanceof Class<?>) {
                        Class<?> prevClass = (Class<?>) prevType;
                        Class<?> nextClass = (Class<?>) nextType;

                        if (TypeUtils.isNumberOrExtends(prevClass)
                                && TypeUtils.isNumberOrExtends(nextClass)) {
                            return TypeUtils.getDominatingNumber(prevClass, nextClass);
                        }
                    }
                }
            }

            return null;
        });
    }

    private static boolean isStringConcatenation(@NonNull Exp[] inner) {
        return doIteration(false, inner, (current, next, prev) -> {
            if (prev == null || next == null) {
                return null;
            }

            if (current instanceof ExpOperator) {
                ExpOperator operator = (ExpOperator) current;

                return (operator.getOperator() == Operator.PLUS)
                        && (prev.getType() == String.class || next.getType() == String.class);
            }

            return null;
        });
    }

    private static boolean isLogical(@NonNull Exp[] inner) {
        return doIteration(false, inner, (current, next, prev) -> {
            if (current instanceof ExpOperator) {
                ExpOperator exp = (ExpOperator) current;
                Operator operator = exp.getOperator();

                if (operator.isComparison()
                        || operator.isNumberComparison()
                        || operator.isLogical()) {
                    return true;
                }
            }

            return null;
        });
    }

    private static <T> T doIteration(T defaults, @NonNull Exp[] inner, @NonNull ExpHandler<T> handler) {
        for (int i = 0; i < inner.length; i++) {
            Exp exp = inner[i];

            Exp next = i == inner.length - 1 ? null : inner[i + 1];
            Exp prev = i == 0 ? null : inner[i - 1];

            T done = handler.handle(exp, next, prev);

            if (done != null) {
                return done;
            }
        }

        return defaults;
    }

    private static @NonNull Type detectType(@NonNull Exp[] inner) {
        // 100% boolean
        if (isLogical(inner)) {
            return boolean.class;
        }

        // "" + "", "" + 10, "" + false
        // there are no comparison operators
        // i.e there are no "" + "" == "" statements
        if (isStringConcatenation(inner)) {
            return String.class;
        }

        Class<?> type = getNumericalType(inner);

        if (type != null) {
            return type;
        }

        throw new IllegalStateException("Cannot get type of parentheses: " + Arrays.toString(inner));
    }

    public static @NonNull ExpParentheses create(@NonNull Exp[] inner) {
        if (inner.length <= 1) {
            throw new IllegalStateException("inner.length <= 1");
        }

        return new ImmutableExpParentheses(inner, detectType(inner));
    }

    @Override
    public void write(ExpOutput output) {
        ExpOutput token = output.next(this);
        token.applyFilters(filters);
        token.append("(");

        try (ExpOutputWrite write = token.startWrite(inner)) {
            do {
                write.current().write(token);
            } while (write.moveNext());
        }

        token.append(")");
    }

    private interface ExpHandler<T> {
        T handle(Exp current, Exp next, Exp prev);
    }


}
