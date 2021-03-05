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

import com.github.lero4ka16.te4j.template.compiled.path.PathAccessor;
import com.github.lero4ka16.te4j.util.io.CharsReader;
import com.github.lero4ka16.te4j.util.io.DataReader;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor
public final class ExpressionParser {

    private final Function<String, PathAccessor> mapper;

    public Expression parseExpression(String value) {
        DataReader reader = new CharsReader("(" + value + ")");
        Expression exp = parseNext(null, reader, 0);

        if (exp instanceof ExpressionParentheses) {
            ExpressionParentheses parentheses = (ExpressionParentheses) exp;

            if (parentheses.canOpenParentheses()) {
                return parentheses.openParentheses();
            }
        }

        return exp;
    }

    private Expression parseNext(Expression prev, DataReader reader, int eof) {
        boolean closed = false;

        while (reader.isReadable()) {
            int ch = reader.read();

            Expression token = null;

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

                if (Operator.isOperator(ch) && !(prev instanceof ExpressionOperator) && !nextDigit) {
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

                        if (token instanceof ExpressionOperator) {
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

    private Expression parseParentheses(DataReader reader) {
        List<Expression> tokens = new ArrayList<>();
        Expression prev = null;
        Expression token;

        while ((token = parseNext(prev, reader, ')')) != null) {
            tokens.add(token);
            prev = token;
        }

        return tokens.size() == 1 ? tokens.get(0) : new ExpressionParentheses(tokens.toArray(new Expression[0]));
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

    private Expression parseList(DataReader reader) {
        Class<?> type = Object.class;

        int pos = reader.position();
        int ch = reader.read();

        if (ch == '&') {
            for (; ; ) {
                ch = reader.read();

                if (ch == ',' || ch == ']') {
                    type = getClass(reader.substring(pos + 1, reader.position() - 1).trim());

                    if (ch == ']') {
                        return new ExpressionList(type, new Expression[0]);
                    }

                    break;
                }
            }
        } else {
            reader.roll();
        }

        List<Expression> tokens = new ArrayList<>();
        Expression prev = null;
        Expression token;

        while ((token = parseNext(prev, reader, ']')) != null) {
            tokens.add(token);
            prev = token;

            ch = reader.readNonWhitespace();

            if (ch == ']') break;
            if (ch != ',') throw new IllegalStateException("Unexpected char: " + (char) ch);
        }

        return new ExpressionList(type, tokens.toArray(new Expression[0]));
    }

    private Expression parseString(DataReader reader, int quote) {
        int start = reader.position();

        while (reader.isReadable()) {
            int ch = reader.read();
            if (ch == quote) break;
        }

        return new ExpressionString(reader.substring(start, reader.position() - 1));
    }

    private Expression parseEnum(String text) {
        return new ExpressionEnum(text);
    }

    private Expression parseOperator(DataReader reader) {
        int startPos = reader.position();

        Operator[] types = Operator.VALUES;
        int pos = 0;

        for (; ; ) {
            int ch = reader.read();
            if (ch == -1) break;

            if (ch == ' ') {
                reader.roll();
                break;
            }

            Operator[] newTypes = Operator.filter(types, pos++, ch);

            if (newTypes.length == 0) {
                reader.roll();
                break;
            }

            types = newTypes;
        }

        String op = reader.substring(startPos, reader.position());

        if (op.equals("!")) {
            return new ExpressionLogicalNegation();
        }

        Operator type = Operator.get(types, op)
                .orElseThrow(() -> new IllegalStateException("Unknown operator: " + op));

        return new ExpressionOperator(type);
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

            if (Operator.isOperator(ch)) {
                reader.roll();
                break;
            }
        }

        return reader.substring(start, reader.position());
    }

    private Expression parseValue(String value) {
        int idx = 0;
        int ch = value.charAt(idx);
        boolean negate = false;

        if (ch == '-') {
            negate = true;
            ch = value.charAt(++idx);
        }

        if (ch >= '0' && ch <= '9') {
            return new ExpressionNumber(value);
        }

        String text = value.substring(idx);
        PathAccessor accessor = mapper.apply(text);

        if (accessor == null) {
            throw new IllegalStateException("Accessor not found: " + text);
        }

        return new ExpressionValue(accessor, negate);
    }

}
