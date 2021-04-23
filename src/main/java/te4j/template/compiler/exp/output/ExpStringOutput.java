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

package te4j.template.compiler.exp.output;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import te4j.template.compiler.exp.Exp;
import te4j.template.compiler.exp.filter.ExpFilters;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lero4ka16
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExpStringOutput implements ExpOutput {

    @Getter
    private final Exp exp;

    private final StringBuilder prefix = new StringBuilder();
    private final List<ExpOutputElement> inner = new ArrayList<>();
    private final StringBuilder suffix = new StringBuilder();

    private ExpOutputWrite write;
    private ExpFilters filters;

    public static @NonNull ExpOutput create(@NonNull Exp exp) {
        return new ExpStringOutput(exp);
    }

    public static @NonNull ExpOutput create() {
        return new ExpStringOutput(null);
    }

    @Override
    public void applyFilters(@NonNull ExpFilters filters) {
        this.filters = filters;
    }

    @Override
    public void prepend(@NonNull String text) {
        prefix.insert(0, text);
    }

    @Override
    public void append(@NonNull String text) {
        if (inner.isEmpty()) {
            prefix.append(text);
        } else {
            suffix.append(text);
        }
    }

    private ExpOutput search(Exp exp) {
        for (ExpOutputElement element : inner) {
            if (element instanceof ExpOutput) {
                ExpOutput output = (ExpOutput) element;

                if (output.getExp() == exp) {
                    return output;
                }
            }
        }

        return null;
    }

    @Override
    public void prepend(@NonNull Exp exp, @NonNull String text) {
        ExpOutput output = search(exp);

        if (output == null) {
            throw new IllegalStateException("There are no output for " + exp);
        }

        output.prepend(text);
    }

    @Override
    public void append(@NonNull Exp exp, @NonNull String text) {
        ExpOutput output = search(exp);

        if (output == null) {
            throw new IllegalStateException("There are no output for " + exp);
        }

        output.append(text);
    }

    @Override
    public ExpOutputWrite startWrite(@NonNull Exp[] values) {
        return write = ExpOutputArrayWrite.create(this, values);
    }

    @Override
    public ExpOutputWrite getWrite() {
        return write;
    }

    @Override
    public void stopWrite() {
        write = null;
    }

    @Override
    public @NonNull ExpOutput next(@NonNull Exp exp) {
        ExpOutput token;

        if (suffix.length() > 0) {
            inner.add(ExpOutputStringElement.create(suffix.toString()));
            suffix.setLength(0);
        }

        inner.add(token = new ExpStringOutput(exp));

        return token;
    }

    @Override
    public @NonNull String flush() {
        StringBuilder out = new StringBuilder();
        out.append(prefix);

        for (ExpOutputElement inner : this.inner) {
            out.append(inner.flush());
        }

        out.append(suffix);

        String value = out.toString();

        if (filters != null) {
            value = filters.format(value);
        }

        return value;
    }
}
