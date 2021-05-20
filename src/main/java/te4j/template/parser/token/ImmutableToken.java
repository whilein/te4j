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

package te4j.template.parser.token;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import te4j.template.exception.TemplateUnexpectedTokenException;

/**
 * @author whilein
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ImmutableToken implements Token {

    String method, path;
    TokenType type;

    public static Token create(
            final @NonNull String method,
            final @NonNull String path,
            final @NonNull TokenType type
    ) {
        return new ImmutableToken(method, path, type);
    }

    @NonNull
    public void expect(int position, @NonNull TokenType... types) throws TemplateUnexpectedTokenException {
        for (TokenType expectType : types) {
            if (expectType == getType()) return;
        }

        throw new TemplateUnexpectedTokenException(types, this, position);
    }
}
