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
public final class ForCode {

    private String elementType;
    private String as;
    private String from;
    private String content;

    private boolean insertCounter;

    private boolean arrayList;
    private boolean array;

    public String getCounterFieldName() {
        return "__counter_" + as;
    }

    public String getElementName() {
        return "__element_" + as;
    }

    public String getArrayFieldName() {
        return "__array_" + as;
    }

    public String getCountFieldName() {
        return "__count_" + as;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    public void setAs(String as) {
        this.as = as;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setInsertCounter(boolean insertCounter) {
        this.insertCounter = insertCounter;
    }

    public void setArray(boolean array) {
        this.array = array;
    }

    public void setArrayList(boolean arrayList) {
        this.arrayList = arrayList;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        boolean arrayOrArrayList = arrayList || array;

        if (!arrayOrArrayList && insertCounter) {
            sb.append("int ").append(getCounterFieldName()).append("=0;");
        }

        if (arrayOrArrayList) {
            if (arrayList) {
                sb.append("List<").append(elementType).append("> ");
            } else {
                sb.append(elementType).append("[] ");
            }

            sb.append(getArrayFieldName()).append('=').append(from).append(';');

            sb.append("int ").append(getCountFieldName()).append('=').append(getArrayFieldName());

            if (arrayList) {
                sb.append(".size();");
            } else {
                sb.append(".length;");
            }

            sb.append("for(int ");
            sb.append(getCounterFieldName()).append("=0;");
            sb.append(getCounterFieldName()).append("<").append(getCountFieldName()).append(';');
            sb.append(getCounterFieldName()).append("++)");

            sb.append('{');
            sb.append(elementType).append(' ').append(getElementName()).append('=');

            sb.append(getArrayFieldName())
                    .append(arrayList ? ".get(" : "[")
                    .append(getCounterFieldName())
                    .append(arrayList ? ");" : "];");
        } else {
            sb.append("for(").append(elementType).append(' ').append(getElementName()).append(':').append(from).append(')');
            sb.append('{');
        }

        sb.append(content);

        if (!arrayOrArrayList && insertCounter) {
            sb.append(getCounterFieldName()).append("++;");
        }

        sb.append('}');
        return sb.toString();
    }
}
