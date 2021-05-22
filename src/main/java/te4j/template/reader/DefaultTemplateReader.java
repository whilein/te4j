/*
 *    Copyright 2021 Whilein
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

package te4j.template.reader;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import te4j.include.DefaultIncludePath;
import te4j.template.exception.TemplateException;
import te4j.template.exception.TemplateUnexpectedTokenException;
import te4j.template.method.TemplateMethod;
import te4j.template.method.TemplateMethodType;
import te4j.template.method.impl.*;
import te4j.template.option.minify.Minify;
import te4j.template.parser.EmptyParsedTemplate;
import te4j.template.parser.ParsedTemplate;
import te4j.template.parser.PlainParsedTemplate;
import te4j.template.parser.StandardParsedTemplate;
import te4j.template.parser.token.*;
import te4j.template.path.DefaultTemplatePath;
import te4j.template.path.TemplatePath;
import te4j.util.formatter.TextFormatter;
import te4j.util.io.BytesReader;
import te4j.util.io.DataReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultTemplateReader implements TemplateReader {

    @NotNull Set<@NotNull Minify> minifyOptions;

    byte @NotNull [] value;
    @NotNull DataReader reader;

    public static TemplateReader create(
            final byte @NonNull [] value,
            final @NonNull Set<@NotNull Minify> minifyOptions
    ) {
        return new DefaultTemplateReader(minifyOptions, value, new BytesReader(value));
    }

    private ParsedTemplate newTemplate(
            final @NotNull List<TemplatePath> paths,
            final int begin, final int end, final boolean inner
    ) {
        if (paths.isEmpty()) {
            if (!inner) {
                val processed = new TextFormatter(value, begin, end - begin)
                        .disableEscaping()
                        .minify(minifyOptions)
                        .formatAsBytes();

                return processed.length == 0
                        ? EmptyParsedTemplate.getInstance()
                        : PlainParsedTemplate.create(processed, 0, processed.length);
            } else {
                return PlainParsedTemplate.create(value, begin, end - begin);
            }
        } else {
            return StandardParsedTemplate.create(paths, value, begin, end - begin);
        }
    }

    @Override
    public @NonNull ParsedTemplate readTemplate() {
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

            if (value == '{') {
                reader.roll();
                path = readValue();

                if (path == null) {
                    reader.position(position - 1);
                    path = readOperation();
                }
            } else {
                continue;
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
        if (startA != startB) return null;

        int valueBegin = reader.position();

        for (; ; ) {
            int value = reader.read();
            if (value == -1) return null;

            if (value == '{') {
                if (valueBegin + 1 == reader.position()) {
                    valueBegin++;
                    pathBegin++;
                }
            }

            if (value == '}') {
                if (reader.read() == value)
                    break;
                else
                    reader.roll();
            }
        }

        int pathEnd = reader.position();
        int valueEnd = pathEnd - 2; // }}

        return DefaultTemplatePath.create(
                pathBegin, pathEnd - pathBegin,
                ValueMethod.create(reader.substring(valueBegin, valueEnd).trim())
        );
    }

    private TokenizedTemplate readInner(TokenType... types) throws TemplateUnexpectedTokenException {
        int blockBegin = reader.position();
        int blockEnd;

        List<TemplatePath> innerPaths = new ArrayList<>();
        Token token;

        try {
            readPathsTo(innerPaths);

            // исключение должно обязательно прилететь
            throw new IllegalStateException("No inner block found");
        } catch (TemplateUnexpectedTokenException e) {
            token = e.getToken();
            blockEnd = e.getPosition();

            token.expect(blockEnd, types);
        }

        return ImmutableTokenizedTemplate.create(token, newTemplate(innerPaths, blockBegin, blockEnd, true));
    }

    private TemplatePath readOperation() throws TemplateUnexpectedTokenException {
        int begin = reader.position();

        Token token = readToken(reader);
        if (token == null) return null;

        token.expect(begin, TokenType.BEGIN);

        String path = token.getPath();
        TemplateMethodType methodType = TemplateMethodType.findType(token.getMethod());
        TemplateMethod method;

        switch (methodType) {
            default:
                throw new IllegalArgumentException("unknown method: " + token.getMethod());
            case INCLUDE: {
                method = IncludeMethod.create(DefaultIncludePath.create(path));
                break;
            }
            case FOR: {
                int separator = path.indexOf(':');

                if (separator == -1) {
                    throw new IllegalStateException("Not found ':' in " + path);
                }

                String as = path.substring(0, separator).trim();
                String value = path.substring(separator + 1).trim();

                method = ForeachMethod.create(value, as, readInner(TokenType.END_FOR).getTemplate());
                break;
            }
            case CASE: {
                int separator = path.indexOf(':');

                String value;
                String from;

                if (separator == -1) {
                    value = path;
                    from = null;
                } else {
                    value = path.substring(0, separator).trim();
                    from = path.substring(separator + 1).trim();
                }

                TokenizedTemplate inner = readInner(TokenType.CASE_DEFAULT, TokenType.END_CASE);

                switch (inner.getToken().getType()) {
                    case END_CASE:
                        method = SwitchCaseMethod.create(value, from, inner.getTemplate(), null);
                        break;
                    case CASE_DEFAULT:
                        method = SwitchCaseMethod.create(value, from, inner.getTemplate(),
                                readInner(TokenType.END_CASE).getTemplate());
                        break;
                    default:
                        throw new IllegalStateException("Unexpected token: " + inner.getToken().getType());
                }

                break;
            }
            case CONDITION:
                method = readCondition(path);
                break;
        }

        return DefaultTemplatePath.create(begin, reader.position() - begin, method);
    }

    private TemplateMethod readCondition(String path) {
        TokenizedTemplate inner = readInner(TokenType.ELSE, TokenType.ELIF, TokenType.END_IF);

        switch (inner.getToken().getType()) {
            case ELSE:
                return ConditionMethod.create(path, inner.getTemplate(),
                        readInner(TokenType.END_IF).getTemplate());
            case ELIF:
                return ConditionMethod.create(path, inner.getTemplate(),
                        readCondition(inner.getToken().getPath()));
            case END_IF:
                return ConditionMethod.create(path, inner.getTemplate(),
                        (ParsedTemplate) null);
            default:
                throw new IllegalStateException("Unexpected token: " + inner.getToken().getType());
        }
    }

    private Token readToken(DataReader in) {
        if (in.read() != '{' || in.read() != '%') { // <*
            return null;
        }

        // читаем первый символ, который не является пробелом
        if (!in.moveNonWhitespace()) return null;

        int start = in.position();

        for (; ; ) {
            if (!in.move('%')) {
                throw new TemplateException("Not found closing for token");
            }

            in.next();

            int ch = in.readNonWhitespace();
            if (ch == -1) return null;
            if (ch == '}') break;
        }

        int end = in.position() - 2; // *>

        String fullPath = in.substring(start, end).trim();

        int separator = fullPath.indexOf(' ');

        String method;
        String path;

        if (separator == -1) {
            method = fullPath;
            path = "";
        } else {
            method = fullPath.substring(0, separator);
            path = fullPath.substring(separator + 1);
        }

        return ImmutableToken.create(method, path, TokenType.getType(method));
    }

}
