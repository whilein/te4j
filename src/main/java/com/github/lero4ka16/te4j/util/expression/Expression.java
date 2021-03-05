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

package com.github.lero4ka16.te4j.util.expression;

import com.github.lero4ka16.te4j.util.type.TypeInfo;
import lombok.Setter;

@Setter
public abstract class Expression {

    protected String filter;

    public abstract ExpressionReturnType getReturnType();

    public void addFilter(String filter) {
        if (this.filter == null) this.filter = filter;
        else this.filter += ":" + filter;
    }

    public String compile() {
        return ExpCompile.singleton(this);
    }

    public abstract TypeInfo getObjectType();

    protected abstract void compile(ExpCompile compile);

}
