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

import com.github.lero4ka16.te4j.Te4j;

import java.util.LinkedList;

public final class ExpCompile {

    private final Expression[] expression;
    private final LinkedList<String> tokens;

    private int position;

    public ExpCompile(Expression[] expression, LinkedList<String> tokens) {
        this.expression = expression;
        this.tokens = tokens;
    }

    public Expression[] getExpression() {
        return expression;
    }

    public LinkedList<String> getTokens() {
        return tokens;
    }

    public int getPosition() {
        return position;
    }

    public void skipNext() {
        position++;
    }

    public Expression getPrevious() {
        return expression[position - 1];
    }

    public static String singleton(Expression exp) {
        ExpCompile compile = new ExpCompile(new Expression[]{exp}, new LinkedList<>());
        exp.compile(compile);

        return compile.toString();
    }

    public void compile() {
        while (position < expression.length) {
            expression[position].compile(this);
            position++;
        }
    }

    public Expression getNext() {
        return expression[position + 1];
    }

    public String toString() {
        return String.join("", tokens);
    }

    public void appendBeforePrevious(String value) {
        // костыль :)))
        String removed = tokens.removeLast();
        tokens.addLast(value);
        tokens.addLast(removed);
    }

    public void append(String value) {
        tokens.add(value);
    }

    public void appendFiltered(String filter, String value) {
        append(Te4j.getFilters().applyFilters(filter, value));
    }
}
