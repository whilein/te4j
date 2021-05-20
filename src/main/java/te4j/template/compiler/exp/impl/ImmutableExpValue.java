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

package te4j.template.compiler.exp.impl;

import lombok.NonNull;
import te4j.template.compiler.exp.AbstractExp;
import te4j.template.compiler.exp.ExpNegation;
import te4j.template.compiler.exp.ExpValue;
import te4j.template.compiler.exp.filter.ExpDefaultFilters;
import te4j.template.compiler.exp.output.ExpOutput;
import te4j.template.compiler.path.PathAccessor;

import java.lang.reflect.Type;

public final class ImmutableExpValue extends AbstractExp implements ExpValue {

    private final PathAccessor accessor;

    private ImmutableExpValue(PathAccessor accessor, Type type, ExpNegation negation) {
        super(ExpDefaultFilters.create(type, negation.getPrefix() + "%s"));

        this.accessor = accessor;
    }

    @Override
    public String toString() {
        return getType() + "/" + accessor.getAccessor();
    }

    public static ExpValue create(@NonNull PathAccessor accessor, @NonNull ExpNegation negation) {
        return new ImmutableExpValue(accessor, accessor.getReturnType().getType(), negation);
    }

    @Override
    public PathAccessor getAccessor() {
        return accessor;
    }

    @Override
    public void write(ExpOutput output) {
        ExpOutput token = output.next(this);
        token.applyFilters(filters);
        token.append(accessor.getAccessor());
    }

}
