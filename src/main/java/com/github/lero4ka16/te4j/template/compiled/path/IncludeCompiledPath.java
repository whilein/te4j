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

import com.github.lero4ka16.te4j.template.include.Include;
import com.github.lero4ka16.te4j.template.path.TemplatePath;
import com.github.lero4ka16.te4j.util.type.info.GenericInfo;
import com.github.lero4ka16.te4j.util.type.info.TypeInfo;
import lombok.Getter;

/**
 * @author Лера
 */
@Getter
public class IncludeCompiledPath extends AbstractCompiledPath {

    private final Include value;

    public IncludeCompiledPath(String id, Include value, TemplatePath path) {
        super(id, path);

        this.value = value;
    }

    @Override
    public TypeInfo getReturnType() {
        return GenericInfo.STRING;
    }

    @Override
    public String getAccessorValue() {
        return value.getPath();
    }

}
