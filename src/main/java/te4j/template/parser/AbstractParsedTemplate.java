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

package te4j.template.parser;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.function.IntPredicate;

/**
 * @author whilein
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractParsedTemplate implements ParsedTemplate {

    protected final byte[] content;

    @Getter
    protected final int offset, length;

    private byte[] _content;

    protected static void checkArguments(byte[] content, int offset, int length) {
        if (offset == 0 && length == 0 && content.length == 0) {
            throw new IllegalArgumentException("template is empty");
        }

        if (offset < 0 || offset >= content.length) {
            throw new IllegalArgumentException("offset must be between 0 and "
                    + content.length + " inclusive, but " + offset);
        }

        if (length < 0 || offset + length > content.length) {
            throw new IllegalArgumentException("length must be between 0 and "
                    + (content.length - offset) + ", but " + length);
        }
    }

    protected static boolean hasNewlines(byte[] content, int off, int length) {
        for (int i = 0; i < length; i++) {
            if (content[off + i] == '\n') {
                return true;
            }
        }

        return false;
    }

    protected static int trim(byte[] content, int off, int len) {
        boolean inline = !hasNewlines(content, off, len);
        if (inline) return 0;

        int result = 0;

        // trim spaces until crlf
        result += trim(AbstractParsedTemplate::isSpace, content, off, len);
        // trim beginning crlf
        result += trim(AbstractParsedTemplate::isCRLF, content, off, len);

        return result;
    }

    private static boolean isSpace(int value) {
        return value == ' ' || value == '\t';
    }

    private static boolean isCRLF(int value) {
        return value == '\r' || value == '\n';
    }

    private static int trim(IntPredicate value, byte[] content, int off, int len) {
        int n = 0;

        while (len - n != 0 && value.test(content[off + n] & 0xFF)) {
            n++;
        }

        return n;
    }

    @Override
    public byte @NonNull [] getRawContent() {
        return content;
    }

    @Override
    public byte @NonNull [] getContent() {
        if (_content == null) {
            _content = offset != 0 || length != content.length
                    ? Arrays.copyOfRange(content, offset, offset + length)
                    : content;
        }

        return _content;
    }

}
