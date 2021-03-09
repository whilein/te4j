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

package com.github.lero4ka16.te4j.template.context;

import com.github.lero4ka16.te4j.Te4j;

/**
 * @author lero4ka16
 */
public final class TemplateContextBuilder {

    private boolean useResources;
    private int outputTypes;
    private int replace;

    public TemplateContextBuilder outputTypes(int bits) {
        this.outputTypes = bits;
        return this;
    }

    public TemplateContextBuilder useResources() {
        this.useResources = true;
        return this;
    }

    public TemplateContextBuilder replace(int value) {
        this.replace = value;
        return this;
    }

    public TemplateContext build() {
        if (outputTypes == 0) {
            outputTypes(Te4j.STRING | Te4j.BYTES);
        }

        return new TemplateContext(useResources, outputTypes, replace);
    }

}
