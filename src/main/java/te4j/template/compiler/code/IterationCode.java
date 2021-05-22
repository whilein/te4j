/*
 *    Copyright 2021 Whilein
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

package te4j.template.compiler.code;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import te4j.template.environment.LoopEnvironment;
import te4j.template.parser.ParsedTemplate;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public final class IterationCode {

    private final String namespace;
    private final String elementType;
    private final String from;

    private final String counterFieldName;
    private final String lengthFieldName;

    private final ParsedTemplate content;
    private final LoopEnvironment loop;

    private final boolean randomAccessList;
    private final boolean array;

    private final boolean castToList;

    public String getElementFieldName() {
        return "__element_" + namespace;
    }

    public String getArrayFieldName() {
        return "__array_" + namespace;
    }
    
    public void write(RenderCode out) {
        boolean arrayOrRandomAccessList = randomAccessList || array;

        int counterPosition;

        if (arrayOrRandomAccessList) {
            if (randomAccessList) {
                out.append("java.util.List<").append(elementType).append("> ");
            } else {
                out.append(elementType).append("[] ");
            }

            out.append(getArrayFieldName()).append("=");

            if (!array && castToList) {
                out.append("(java.util.List<").append(elementType).append(">) ");
            }

            out.append(from).append(";");

            out.append("int ").append(lengthFieldName).append("=").append(getArrayFieldName());

            if (randomAccessList) {
                out.append(".size();");
            } else {
                out.append(".length;");
            }

            counterPosition = out.position();

            out.append("for(int ");
            out.append(counterFieldName).append("=0;");
            out.append(counterFieldName).append("<").append(lengthFieldName).append(";");
            out.append(counterFieldName).append("++)");

            out.append("{");
            out.append(elementType).append(" ").append(getElementFieldName()).append("=");

            out.append(getArrayFieldName())
                    .append(randomAccessList ? ".get(" : "[")
                    .append(counterFieldName)
                    .append(randomAccessList ? ");" : "];");
        } else {
            out.append("java.util.Collection<").append(elementType).append(">").append(getArrayFieldName()).append("=").append(from).append(";");
            counterPosition = out.position();

            out.append("for(").append(elementType).append(" ").append(getElementFieldName()).append(":").append(getArrayFieldName()).append(")");
            out.append("{");
        }

        out.appendTemplate(content);

        if (!arrayOrRandomAccessList) {
            if (loop.hasCounter()) {
                out.setPosition(counterPosition);
                out.append("int ").append(counterFieldName).append("=0;");
                out.resetPosition();

                out.append(counterFieldName).append("++;");
            }

            if (loop.hasLength()) {
                // todo Iterable support (Iterable doesn't has .size() method)

                out.setPosition(counterPosition);
                out.append("int ").append(lengthFieldName).append("=").append(getArrayFieldName()).append(".size();");
                out.resetPosition();
            }
        }

        out.append("}");
    }
}
