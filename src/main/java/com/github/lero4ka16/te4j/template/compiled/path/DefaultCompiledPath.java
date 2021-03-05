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

package com.github.lero4ka16.te4j.template.compiled.path;

import com.github.lero4ka16.te4j.template.path.TemplatePath;
import com.github.lero4ka16.te4j.util.type.info.TypeInfo;

/**
 * @author Лера
 */
public class DefaultCompiledPath extends AbstractCompiledPath {

    private final PathAccessor value;

    public DefaultCompiledPath(String id, PathAccessor value, TemplatePath original) {
        super(id, original);

        this.value = value;
    }

    @Override
    public boolean isStream() {
        return value.isStream();
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
