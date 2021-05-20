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

package te4j.template.compiler.path;

import te4j.template.path.TemplatePath;
import te4j.util.type.TypeInfo;

/**
 * @author whilein
 */
public final class ConditionCompiledPath extends AbstractCompiledPath {

    private final PathAccessor value;
    private final ConditionCompiledPath elseIf;

    public ConditionCompiledPath(String id, PathAccessor value, ConditionCompiledPath elseIf, TemplatePath original) {
        super(id, original);

        this.value = value;
        this.elseIf = elseIf;
    }

    public ConditionCompiledPath getElseIf() {
        return elseIf;
    }

    @Override
    public TypeInfo getReturnType() {
        return value.getReturnType();
    }

    @Override
    public String getAccessorValue() {
        return value.getAccessor();
    }


}
