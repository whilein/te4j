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
import te4j.template.compiler.exp.ExpNumber;
import te4j.template.compiler.exp.filter.ExpDefaultFilters;
import te4j.template.compiler.exp.output.ExpOutput;
import te4j.util.NumberUtils;

public final class ImmutableExpNumber extends AbstractExp implements ExpNumber {

    private final String numberString;
    private final Number number;

    private ImmutableExpNumber(String numberString, Number number) {
        super(ExpDefaultFilters.create(number.getClass()));

        this.numberString = numberString;
        this.number = number;
    }

    @Override
    public Number getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return number.getClass().getSimpleName() + "/" + numberString;
    }

    public static @NonNull ExpNumber create(@NonNull String number) {
        return new ImmutableExpNumber(number, NumberUtils.parseNumber(number));
    }

    @Override
    public void write(ExpOutput output) {
        ExpOutput token = output.next(this);
        token.applyFilters(filters);
        token.append(numberString);
    }

}
