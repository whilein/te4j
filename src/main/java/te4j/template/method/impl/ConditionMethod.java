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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import te4j.template.method.TemplateMethod;
import te4j.template.method.TemplateMethodType;
import te4j.template.parser.ParsedTemplate;

import java.util.Objects;

/**
 * @author lero4ka16
 */
public class ConditionMethod implements TemplateMethod {

    private final String condition;
    private final ConditionMethod elseIf;
    private final ParsedTemplate block, elseBlock;

    private ConditionMethod(String condition,
                            ConditionMethod elseIf,
                            ParsedTemplate block,
                            ParsedTemplate elseBlock) {
        this.condition = condition;
        this.elseIf = elseIf;
        this.block = block;
        this.elseBlock = elseBlock;
    }

    public static ConditionMethod create(
            @NotNull String condition,
            @NotNull ParsedTemplate block,
            @Nullable ParsedTemplate elseBlock
    ) {
        Objects.requireNonNull(condition, "condition");
        Objects.requireNonNull(block, "block");

        return new ConditionMethod(condition, null, block, elseBlock);
    }

    public static ConditionMethod create(
            @NotNull String condition,
            @NotNull ParsedTemplate block,
            @NotNull ConditionMethod elseIf
    ) {
        Objects.requireNonNull(condition, "condition");
        Objects.requireNonNull(block, "block");
        Objects.requireNonNull(elseIf, "elseIf");

        return new ConditionMethod(condition, elseIf, block, null);
    }

    public ConditionMethod getElseIf() {
        return elseIf;
    }

    public String getCondition() {
        return condition;
    }

    public ParsedTemplate getBlock() {
        return block;
    }

    public ParsedTemplate getElseBlock() {
        return elseBlock;
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
