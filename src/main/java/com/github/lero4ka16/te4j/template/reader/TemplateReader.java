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

package com.github.lero4ka16.te4j.template.reader;

import com.github.lero4ka16.te4j.include.IncludeFile;
import com.github.lero4ka16.te4j.template.context.TemplateContext;
import com.github.lero4ka16.te4j.template.exception.TemplateException;
import com.github.lero4ka16.te4j.template.exception.TemplateUnexpectedTokenException;
import com.github.lero4ka16.te4j.template.method.TemplateMethod;
import com.github.lero4ka16.te4j.template.method.TemplateMethodType;
import com.github.lero4ka16.te4j.template.method.impl.ConditionMethod;
import com.github.lero4ka16.te4j.template.method.impl.ForeachMethod;
import com.github.lero4ka16.te4j.template.method.impl.IncludeMethod;
import com.github.lero4ka16.te4j.template.method.impl.SwitchCaseMethod;
import com.github.lero4ka16.te4j.template.method.impl.ValueMethod;
import com.github.lero4ka16.te4j.template.parse.ParsedTemplate;
import com.github.lero4ka16.te4j.template.parse.PlainParsedTemplate;
import com.github.lero4ka16.te4j.template.parse.StandardParsedTemplate;
import com.github.lero4ka16.te4j.template.path.TemplatePath;
import com.github.lero4ka16.te4j.template.reader.token.TemplateToken;
import com.github.lero4ka16.te4j.template.reader.token.TemplateTokenType;
import com.github.lero4ka16.te4j.util.io.BytesReader;
import com.github.lero4ka16.te4j.util.io.DataReader;
import com.github.lero4ka16.te4j.util.text.Text;

import java.util.ArrayList;
import java.util.List;

public final class TemplateReader {

    private final TemplateContext context;

    private final byte[] value;
    private final DataReader reader;

    public TemplateReader(TemplateContext context, byte[] value) {
        this.context = context;

        this.value = value;
        this.reader = new BytesReader(value);
    }

    private ParsedTemplate newTemplate(List<TemplatePath> paths, int begin, int end, boolean inner) {
        ParsedTemplate template;

        if (paths.isEmpty()) {
            if (!inner) {
                byte[] processed = Text.of(value, begin, end - begin)
                        .disableEscaping()
                        .replaceStrategy(context.getReplaceStrategy())
                        .computeAsBytes();

                template = new PlainParsedTemplate(context, processed, 0, processed.length);
            } else {
                template = new PlainParsedTemplate(context, value, begin, end - begin);
            }
        } else {
            template = new StandardParsedTemplate(context, paths, value, begin, end - begin);
        }

        return template;
    }

    public ParsedTemplate readTemplate() {
        int begin = reader.position();

        List<TemplatePath> paths = new ArrayList<>();
        readPathsTo(paths);

        int end = reader.position();

        return newTemplate(paths, begin, end, false);
    }

    private void readPathsTo(List<TemplatePath> paths) {
        int value;

        while ((value = reader.read()) != -1) {
            int position = reader.position();
            TemplatePath path;

            switch (value) {
                case '^': // possible value begin
                    reader.roll();
                    path = readValue();
                    break;
                case '<': // possible operation begin
                    reader.roll();
                    path = readOperation();
                    break;
                default: continue;
            }

            if (path == null) {
                reader.position(position);
            } else {
                paths.add(path);
            }
        }
    }

    private TemplatePath readValue() {
        int pathBegin = reader.position();
        int startA = reader.read();
        int startB = reader.read();

        // должно быть ^^, без пробелов и других знаков
        if (startA != startB) return null;

        int valueBegin = reader.position();

        for (;;) {
            int value = reader.read();

            if (value == -1) return null;
            if (value == '^' && reader.read() == '^') break;
        }

        int pathEnd = reader.position();
        int valueEnd = pathEnd - 2; // }}

        String value = reader.substring(valueBegin, valueEnd).trim();
        return new TemplatePath(pathBegin, pathEnd - pathBegin, new ValueMethod(value));
    }

    private Inner readInner(TemplateTokenType... types) throws TemplateUnexpectedTokenException {
        int blockBegin = reader.position();
        int blockEnd;

        List<TemplatePath> innerPaths = new ArrayList<>();
        TemplateToken token;

        try {
            readPathsTo(innerPaths);

            // исключение должно обязательно прилететь
            throw new IllegalStateException("No inner block found");
        } catch (TemplateUnexpectedTokenException e) {
            token = e.getToken();
            blockEnd = e.getPosition();

            token.expect(blockEnd, types);
        }

        return new Inner(newTemplate(innerPaths, blockBegin, blockEnd, true), token.getType());
    }

    private TemplatePath readOperation() throws TemplateUnexpectedTokenException {
        int begin = reader.position();

        TemplateToken token = readToken(reader);
        if (token == null) return null;

        token.expect(begin, TemplateTokenType.BEGIN);

        String fullPath = token.getValue();
        int separator = fullPath.indexOf(' ');

        String methodName;
        String path;

        if (separator == -1) {
            methodName = fullPath;
            path = "";
        } else {
            methodName = fullPath.substring(0, separator);
            path = fullPath.substring(separator + 1);
        }

        TemplateMethodType methodType = TemplateMethodType.findType(methodName);

        TemplateMethod method;

        switch (methodType) {
            default:
            case INCLUDE: {
                method = new IncludeMethod(new IncludeFile(path));
                break;
            }
            case FOR: {
                separator = path.indexOf(':');

                if (separator == -1) {
                    throw new IllegalStateException("Not found ':' in " + path);
                }

                String as = path.substring(0, separator).trim();
                String value = path.substring(separator + 1).trim();

                method = new ForeachMethod(value, as, readInner(TemplateTokenType.END_FOR).template);
                break;
            }
            case CASE: {
                separator = path.indexOf(':');

                String value;
                String from;

                if (separator == -1) {
                    value = path;
                    from = null;
                } else {
                    value = path.substring(0, separator).trim();
                    from = path.substring(separator + 1).trim();
                }

                Inner inner = readInner(TemplateTokenType.CASE_DEFAULT, TemplateTokenType.END_CASE);

                if (inner.byToken == TemplateTokenType.END_CASE) {
                    method = new SwitchCaseMethod(value, from, inner.template, null);
                } else {
                    method = new SwitchCaseMethod(value, from, inner.template, readInner(TemplateTokenType.END_CASE).template);
                }
                break;
            }
            case CONDITION: {
                Inner inner = readInner(TemplateTokenType.ELSE, TemplateTokenType.END_IF);

                if (inner.byToken == TemplateTokenType.END_IF) {
                    method = new ConditionMethod(path, inner.template, null);
                } else {
                    method = new ConditionMethod(path, inner.template, readInner(TemplateTokenType.END_IF).template);
                }
                break;
            }
        }

        return new TemplatePath(begin, reader.position() - begin, method);
    }

    private TemplateToken readToken(DataReader in) {
        if (in.read() != '<' || in.read() != '*') { // <*
            return null;
        }

        // читаем первый символ, который не является пробелом
        if (!in.moveNonWhitespace()) return null;

        int start = in.position();

        for (;;) {
            if (!in.move('*')) {
                throw new TemplateException("Not found closing for token");
            }

            in.next();

            int ch = in.readNonWhitespace();
            if (ch == -1) return null;
            if (ch == '>') break;
        }

        int end = in.position() - 2; // *>

        String path = in.substring(start, end).trim();
        return new TemplateToken(path, TemplateTokenType.getType(path));
    }

    private static class Inner {
        private final ParsedTemplate template;
        private final TemplateTokenType byToken;

        public Inner(ParsedTemplate template, TemplateTokenType byToken) {
            this.template = template;
            this.byToken = byToken;
        }
    }

}
