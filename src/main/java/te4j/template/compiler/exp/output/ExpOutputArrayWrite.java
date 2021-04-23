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
import lombok.experimental.FieldDefaults;
import te4j.template.compiler.exp.Exp;

/**
 * @author lero4ka16
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExpOutputArrayWrite implements ExpOutputWrite {

    final ExpOutput output;
    final Exp[] values;

    @Getter
    int index;

    public static ExpOutputWrite create(@NonNull ExpOutput output, @NonNull Exp[] values) {
        return new ExpOutputArrayWrite(output, values);
    }

    @Override
    public @NonNull ExpOutput getOutput() {
        return output;
    }

    @Override
    public boolean moveNext() {
        if (index >= values.length - 1)
            return false;

        index++;
        return true;
    }

    @Override
    public Exp next() {
        return index >= values.length - 1 ? null : values[index + 1];
    }

    @Override
    public Exp current() {
        return values[index];
    }

    @Override
    public Exp prev() {
        return index <= 0 ? null : values[index - 1];
    }

    @Override
    public void close() {
        output.stopWrite();
    }
}
