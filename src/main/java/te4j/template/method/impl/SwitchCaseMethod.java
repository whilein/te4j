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

package te4j.template.method.impl;

import te4j.template.method.TemplateMethod;
import te4j.template.method.TemplateMethodType;
import te4j.template.parse.ParsedTemplate;

/**
 * @author lero4ka16
 */
public class SwitchCaseMethod implements TemplateMethod {

    private final String value;
    private final String from;

    private final ParsedTemplate block, defaultBlock;

    public SwitchCaseMethod(String value,
                            String from,
                            ParsedTemplate block,
                            ParsedTemplate defaultBlock) {
        this.value = value;
        this.from = from;
        this.block = block;
        this.defaultBlock = defaultBlock;
    }

    public String getValue() {
        return value;
    }

    public String getFrom() {
        return from;
    }

    public ParsedTemplate getBlock() {
        return block;
    }

    public ParsedTemplate getDefaultBlock() {
        return defaultBlock;
    }

    @Override
    public TemplateMethodType getType() {
        return TemplateMethodType.CASE;
    }
}
