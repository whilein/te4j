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

package com.github.lero4ka16.te4j.template.compiler.accessor;

import com.github.lero4ka16.te4j.util.StringConcatenation;

/**
 * @author lero4ka16
 */
public class MethodAccessor implements Accessor {

    private final String value;

    public MethodAccessor(String value) {
        this.value = value;
    }

    @Override
    public void insert(StringConcatenation concatenation) {
        concatenation.appendMethod(value);
    }
}
