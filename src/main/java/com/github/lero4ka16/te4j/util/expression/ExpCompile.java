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

import com.github.lero4ka16.te4j.Te4j;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.LinkedList;

@Getter
@RequiredArgsConstructor
public class ExpCompile {

    private final Exp[] expression;
    private final LinkedList<String> tokens;

    private int position;

    public void skipNext() {
        position++;
    }

    public Exp getPrevious() {
        return expression[position - 1];
    }

    public static String singleton(Exp exp) {
        ExpCompile compile = new ExpCompile(new Exp[] { exp }, new LinkedList<>());
        exp.compile(compile);

        return compile.toString();
    }

    public void compile() {
        while (position < expression.length) {
            expression[position].compile(this);
            position++;
        }
    }

    public Exp getNext() {
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
