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

package com.github.lero4ka16.te4j.template.compiler.code;

import com.github.lero4ka16.te4j.template.parse.ParsedTemplate;

/**
 * @author lero4ka16
 */
public final class IterationCode {

    private String elementType;
    private String as;
    private String from;
    private ParsedTemplate content;

    private boolean insertCounter;

    private boolean arrayList;
    private boolean array;

    private boolean castArrayList;

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

    public void setContent(ParsedTemplate content) {
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

    public void setCastArrayList(boolean castArrayList) {
        this.castArrayList = castArrayList;
    }

    public void write(RenderCode out) {
        boolean arrayOrArrayList = arrayList || array;

        int counterPosition = out.position();

        if (arrayOrArrayList) {
            if (arrayList) {
                out.append("java.util.List<").append(elementType).append("> ");
            } else {
                out.append(elementType).append("[] ");
            }

            out.append(getArrayFieldName()).append("=");

            if (castArrayList) {
                out.append("(java.util.List<").append(elementType).append(">) ");
            }

            out.append(from).append(";");

            out.append("int ").append(getCountFieldName()).append("=").append(getArrayFieldName());

            if (arrayList) {
                out.append(".size();");
            } else {
                out.append(".length;");
            }

            out.append("for(int ");
            out.append(getCounterFieldName()).append("=0;");
            out.append(getCounterFieldName()).append("<").append(getCountFieldName()).append(";");
            out.append(getCounterFieldName()).append("++)");

            out.append("{");
            out.append(elementType).append(" ").append(getElementName()).append("=");

            out.append(getArrayFieldName())
                    .append(arrayList ? ".get(" : "[")
                    .append(getCounterFieldName())
                    .append(arrayList ? ");" : "];");
        } else {
            out.append("for(").append(elementType).append(" ").append(getElementName()).append(":").append(from).append(")");
            out.append("{");
        }

        out.appendTemplate(content);

        if (!arrayOrArrayList && insertCounter) {
            out.setPosition(counterPosition);
            out.append("int ").append(getCounterFieldName()).append("=0;");
            out.resetPosition();

            out.append(getCounterFieldName()).append("++;");
        }

        out.append("}");
    }
}
