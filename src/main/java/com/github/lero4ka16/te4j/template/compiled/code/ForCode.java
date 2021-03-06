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

package com.github.lero4ka16.te4j.template.compiled.code;

/**
 * @author lero4ka16
 */
public class ForCode {

    private String elementType;
    private String elementName;
    private String from;

    private String counter;
    private String content;

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setCounter(String counter) {
        this.counter = counter;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (counter != null) {
            sb.append("int ").append(counter).append("=0;");
        }
        sb.append("for(").append(elementType).append(' ').append(elementName).append(':').append(from).append(')');
        sb.append('{');

        sb.append(content);

        if (counter != null) {
            sb.append(counter).append("++;");
        }

        sb.append('}');
        return sb.toString();
    }
}
