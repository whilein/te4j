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

package te4j.template.method.impl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import te4j.template.method.TemplateMethod;
import te4j.template.method.TemplateMethodType;
import te4j.template.parser.ParsedTemplate;

/**
 * @author lero4ka16
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConditionMethod implements TemplateMethod {

    private final String condition;
    private final TemplateMethod elseIf;
    private final ParsedTemplate block, elseBlock;

    public static TemplateMethod create(
            @NonNull String condition,
            @NonNull ParsedTemplate block,
            ParsedTemplate elseBlock
    ) {
        return new ConditionMethod(condition, null, block, elseBlock);
    }

    public static TemplateMethod create(
            @NonNull String condition,
            @NonNull ParsedTemplate block,
            @NonNull TemplateMethod elseIf
    ) {
        return new ConditionMethod(condition, elseIf, block, null);
    }

    @Override
    public TemplateMethodType getType() {
        return TemplateMethodType.CONDITION;
    }

    @Override
    public String toString() {
        return "Condition[" + condition + ", " + (elseIf == null ? ("block=" + block + (elseBlock == null ? "" : ", else=" + elseBlock)) : ("elseIf=" + elseIf)) + "]";
    }

}
