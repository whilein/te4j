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

package te4j.template.exception;

import te4j.template.reader.token.TemplateToken;
import te4j.template.reader.token.TemplateTokenType;

import java.util.Arrays;

/**
 * @author lero4ka16
 */
public class TemplateUnexpectedTokenException extends RuntimeException {

    private final TemplateTokenType[] expect;
    private final TemplateToken token;
    private final int position;

    public TemplateUnexpectedTokenException(TemplateTokenType[] expect, TemplateToken token, int position) {
        this.expect = expect;
        this.token = token;
        this.position = position;
    }

    public TemplateTokenType[] getExpect() {
        return expect;
    }

    public TemplateToken getToken() {
        return token;
    }

    public int getPosition() {
        return position;
    }

    @Override
    public String getMessage() {
        return "Expect: " + Arrays.toString(expect) + ", Actual: " + token.getType() + ", Position: " + position;
    }

}
