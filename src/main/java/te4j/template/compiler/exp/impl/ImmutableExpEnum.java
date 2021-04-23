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

package te4j.template.compiler.exp.impl;

import lombok.Getter;
import lombok.NonNull;
import te4j.template.compiler.exp.AbstractExp;
import te4j.template.compiler.exp.ExpEnum;
import te4j.template.compiler.exp.filter.ExpDisabledFilters;
import te4j.template.compiler.exp.output.ExpOutput;

import java.lang.reflect.Type;

@Getter
public final class ImmutableExpEnum extends AbstractExp implements ExpEnum {

    public static @NonNull ExpEnum create(@NonNull String name) {
        return new ImmutableExpEnum(name);
    }

    private final String name;

    private ImmutableExpEnum(String name) {
        super(ExpDisabledFilters.INSTANCE);
        this.name = name;
    }

    @Override
    public Type getType() {
        return Enum.class;
    }

    @Override
    public void write(ExpOutput output) {
        throw new UnsupportedOperationException("ExpEnum#write(ExpOutput)");
    }

}
