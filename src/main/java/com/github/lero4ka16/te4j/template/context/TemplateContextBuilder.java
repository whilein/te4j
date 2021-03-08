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

import com.github.lero4ka16.te4j.template.output.TemplateOutputType;

/**
 * @author lero4ka16
 */
public class TemplateContextBuilder {

    private boolean useResources;
    private int outputTypes;
    private int replaceStrategy;

    public TemplateContextBuilder outputTypes(int bits) {
        this.outputTypes = bits;
        return this;
    }

    public TemplateContextBuilder useResources() {
        this.useResources = true;
        return this;
    }

    public TemplateContextBuilder replaceStrategy(int value) {
        this.replaceStrategy = value;
        return this;
    }

    public TemplateContext build() {
        if (outputTypes == 0) {
            outputTypes(TemplateOutputType.STRING | TemplateOutputType.BYTES);
        }

        return new TemplateContext(useResources, outputTypes, replaceStrategy);
    }

}
