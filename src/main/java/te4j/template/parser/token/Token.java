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

import org.immutables.value.Value;
import te4j.template.exception.TemplateUnexpectedTokenException;

/**
 * @author whilein
 */
@Value.Immutable
public abstract class Token {

    public abstract String getMethod();

    public abstract String getPath();

    public abstract TokenType getType();

    public void expect(int position, TokenType... types) throws TemplateUnexpectedTokenException {
        for (TokenType expectType : types) {
            if (expectType == getType()) return;
        }

        throw new TemplateUnexpectedTokenException(types, this, position);
    }

}
