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

import lombok.Getter;
import lombok.NonNull;
import te4j.template.compiler.ExpOperator;
import te4j.template.compiler.exp.*;
import te4j.template.compiler.exp.filter.ExpDisabledFilters;
import te4j.template.compiler.exp.output.ExpOutput;
import te4j.template.compiler.exp.output.ExpOutputWrite;
import te4j.util.TypeUtils;

import java.lang.reflect.Type;

@Getter
public final class ImmutableExpOperator extends AbstractExp implements ExpOperator {

    private final Operator operator;

    protected ImmutableExpOperator(Operator operator) {
        super(ExpDisabledFilters.INSTANCE);

        this.operator = operator;
    }

    public static @NonNull ExpOperator create(final @NonNull Operator operator) {
        return new ImmutableExpOperator(operator);
    }

    @Override
    public String toString() {
        return operator.name();
    }

    @Override
    public void write(ExpOutput output) {
        ExpOutputWrite write = output.getWrite();
        assert write != null;

        Exp next = write.next();
        Exp prev = write.prev();

        if (operator.isComparison() && prev != null && next != null) {
            if (!TypeUtils.isPrimitive(prev.getType())
                    && !TypeUtils.isPrimitive(next.getType())
                    && !(prev instanceof ExpEnum)
                    && !(next instanceof ExpEnum)
                    && !(prev instanceof ExpNumber)
                    && !(next instanceof ExpNumber)) {
                if (operator == Operator.NOT_EQUAL) {
                    output.prepend(prev, "!");
                    // !prev.equals(next)
                }

                output.append(".equals(");
                next.write(output);
                output.append(")");

                write.moveNext();

                return;
            }

            if (prev instanceof ExpEnum || next instanceof ExpEnum) {
                Type prevType = prev.getType();
                Type nextType = next.getType();

                if (prevType instanceof Class<?> && nextType instanceof Class<?>) {
                    Class<?> prevClass = (Class<?>) prevType;
                    Class<?> nextClass = (Class<?>) nextType;

                    if (prevClass.isEnum() && next instanceof ExpEnum) {
                        output.next(this).append(operator.getOperator());
                        output.append(prevClass.getName() + ".");
                        next.write(output);
                        write.moveNext();
                        //prevType.prev [!= | ==] prevType.next

                        return;
                    }

                    if (nextClass.isEnum() && prev instanceof ExpEnum) {
                        output.prepend(prev, nextClass.getName() + ".");
                        output.next(this).append(operator.getOperator());
                        //nextType.prev [!= | ==] nextType.next
                        return;
                    }
                }
            }
        }

        output.next(this).append(operator.getOperator());
    }
}
