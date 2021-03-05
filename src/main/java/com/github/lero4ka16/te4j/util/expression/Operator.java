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

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum Operator {
    MULTIPLY("*"),
    PLUS("+"),
    MINUS("-"),
    DIVIDE("/"),
    OR("||"),
    AND("&&"),
    BITWISE_OR("|"),
    BITWISE_XOR("^"),
    BITWISE_AND("&"),
    BITWISE_LSHIFT("<<"),
    BITWISE_RSHIFT(">>"),
    EQUAL("=="),
    NOT_EQUAL("!=");

    private final String operator;

    public static final Operator[] VALUES = values();

    public boolean isComparison() {
        return this == EQUAL || this == NOT_EQUAL;
    }

    public boolean isLogical() {
        return this == OR || this == AND;
    }

    public boolean isNumerical() {
        return this == MULTIPLY
                || this == DIVIDE
                || this == PLUS
                || this == MINUS
                || this == BITWISE_OR
                || this == BITWISE_AND
                || this == BITWISE_LSHIFT
                || this == BITWISE_RSHIFT
                || this == BITWISE_XOR;

    }

    public static boolean isOperator(int i) {
        // в пизду оптимизацию
        return filter(VALUES, 0, i).length != 0;
    }

    public static Optional<Operator> get(Operator[] types, String value) {
        return Arrays.stream(types)
                .filter(element -> element.getOperator().equals(value))
                .findAny();
    }

    public static Operator[] filter(Operator[] type, int position, int value) {
        return Arrays.stream(type)
                .filter(element -> element.getOperator().length() > position
                        && element.getOperator().charAt(position) == value)
                .toArray(Operator[]::new);
    }
}
