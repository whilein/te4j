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
import te4j.template.compiler.exp.ExpString;
import te4j.template.compiler.exp.filter.ExpDefaultFilters;
import te4j.template.compiler.exp.output.ExpOutput;

@Getter
public final class ImmutableExpString extends AbstractExp implements ExpString {

    private final String value;

    private ImmutableExpString(String value) {
        super(ExpDefaultFilters.create(String.class, "\"%s\""));

        this.value = value;
    }

    public static @NonNull ExpString create(@NonNull String value) {
        return new ImmutableExpString(value);
    }

    @Override
    public void write(ExpOutput output) {
        ExpOutput token = output.next(this);
        token.applyFilters(filters);
        token.append(value);
    }

}
