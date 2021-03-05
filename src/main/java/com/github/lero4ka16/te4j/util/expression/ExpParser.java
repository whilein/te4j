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

import com.github.lero4ka16.te4j.template.compiled.path.PathAccessor;
import com.github.lero4ka16.te4j.util.reader.CharsReader;
import com.github.lero4ka16.te4j.util.reader.DataReader;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor
public final class ExpParser {

    private final Function<String, PathAccessor> mapper;

    public Exp parseExpression(String value) {
        DataReader reader = new CharsReader("(" + value + ")");
        Exp exp = parseNext(null, reader, 0);

        if (exp instanceof ExpParentheses) {
            ExpParentheses parentheses = (ExpParentheses) exp;

            if (parentheses.canOpenParentheses()) {
                return parentheses.openParentheses();
            }
        }

        return exp;
    }

    private Exp parseNext(Exp prev, DataReader reader, int eof){
        boolean closed = false;

        while (reader.isReadable()) {
            int ch = reader.read();

            Exp token = null;

            if (ch == eof) {
                closed = true;
                break;
            } else if (ch == '(') {
                token = parseParentheses(reader);
            } else if (ch == '"' || ch == '\'') {
                token = parseString(reader, ch);
            } else if (ch == '[') {
                token = parseList(reader);
            } else if (ch == '#') {
                token = parseEnum(parseText(reader, eof));
            } else if (ch != ' ' && ch != ',') {
                boolean nextDigit = reader.isReadable();

                if (nextDigit) {
                    int next = reader.read();
                    nextDigit = next >= '0' && next <= '9';
                    reader.roll();
                }

                reader.roll();

                if (ExpOpType.isOperator(ch) && !(prev instanceof ExpOp) && !nextDigit) {
                    token = parseOperator(reader);
                } else {
                    token = parseValue(parseText(reader, eof));
                }
            }

            if (token != null) {
                if (reader.isReadable()) {
                    ch = reader.read();

                    while (ch == ':') {
                        String filter = parseText(reader, eof);

                        if (token instanceof ExpOp) {
                            throw new IllegalStateException("Unexpected filter");
                        }

                        token.addFilter(filter);
                        ch = reader.read();
                    }

                    reader.roll();
                }

                return token;
            }
        }

        if (!closed) {
            throw new IllegalStateException("End of file");
        }

        return null;
    }

    private Exp parseParentheses(DataReader reader) {
        List<Exp> tokens = new ArrayList<>();
        Exp prev = null;
        Exp token;

        while ((token = parseNext(prev, reader, ')')) != null) {
            tokens.add(token);
            prev = token;
        }

        return tokens.size() == 1 ? tokens.get(0) : new ExpParentheses(tokens.toArray(new Exp[0]));
    }

    private Class<?> getClass(String name) {
        switch (name) {
            case "byte":
                return byte.class;
            case "short":
                return short.class;
            case "int":
                return int.class;
            case "long":
                return long.class;
            case "float":
                return float.class;
            case "char":
                return char.class;
            case "boolean":
                return boolean.class;
            case "double":
                return double.class;
            default:
                try {
                    return Class.forName(name);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
        }
    }

    private Exp parseList(DataReader reader) {
        Class<?> type = Object.class;

        int pos = reader.position();
        int ch = reader.read();

        if (ch == '&') {
            for (; ; ) {
                ch = reader.read();

                if (ch == ',' || ch == ']') {
                    type = getClass(reader.substring(pos + 1, reader.position() - 1).trim());

                    if (ch == ']') {
                        return new ExpList(type, new Exp[0]);
                    }

                    break;
                }
            }
        } else {
            reader.roll();
        }

        List<Exp> tokens = new ArrayList<>();
        Exp prev = null;
        Exp token;

        while ((token = parseNext(prev, reader, ']')) != null) {
            tokens.add(token);
            prev = token;

            ch = reader.readNonWhitespace();

            if (ch == ']') break;
            if (ch != ',') throw new IllegalStateException("Unexpected char: " + (char) ch);
        }

        return new ExpList(type, tokens.toArray(new Exp[0]));
    }

    private Exp parseString(DataReader reader, int quote) {
        int start = reader.position();

        while (reader.isReadable()) {
            int ch = reader.read();
            if (ch == quote) break;
        }

        return new ExpString(reader.substring(start, reader.position() - 1));
    }

    private Exp parseEnum(String text) {
        return new ExpEnum(text);
    }

    private Exp parseOperator(DataReader reader) {
        int startPos = reader.position();

        ExpOpType[] types = ExpOpType.VALUES;
        int pos = 0;

        for (; ; ) {
            int ch = reader.read();
            if (ch == -1) break;

            if (ch == ' ') {
                reader.roll();
                break;
            }

            ExpOpType[] newTypes = ExpOpType.filter(types, pos++, ch);

            if (newTypes.length == 0) {
                reader.roll();
                break;
            }

            types = newTypes;
        }

        String op = reader.substring(startPos, reader.position());

        ExpOpType type = ExpOpType.get(types, op)
                .orElseThrow(() -> new IllegalStateException("Unknown operator: " + op));

        return new ExpOp(type);
    }

    private String parseText(DataReader reader, int eof) {
        int start = reader.position();

        for (; ; ) {
            int ch = reader.read();
            if (ch == -1) break;

            if (ch == ' ' || ch == eof || ch == ':' || ch == ',') {
                reader.roll();
                break;
            }

            if (ch == '-' && start == reader.position() - 1) { // negation
                continue;
            }

            if (ExpOpType.isOperator(ch)) {
                reader.roll();
                break;
            }
        }

        return reader.substring(start, reader.position());
    }

    private Exp parseValue(String value) {
        int idx = 0;
        int ch = value.charAt(idx);
        boolean negate = false;

        if (ch == '-') {
            negate = true;
            ch = value.charAt(++idx);
        }

        if (ch >= '0' && ch <= '9') {
            return new ExpNumber(value);
        }

        String text = value.substring(idx);
        PathAccessor accessor = mapper.apply(text);

        if (accessor == null) {
            throw new IllegalStateException("Accessor not found: " + text);
        }

        return new ExpValue(accessor, negate);
    }

}
