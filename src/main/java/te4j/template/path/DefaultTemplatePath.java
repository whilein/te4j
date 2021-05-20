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

package te4j.template.path;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import te4j.template.method.TemplateMethod;
import te4j.template.method.TemplateMethodType;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultTemplatePath implements TemplatePath {

    int offset;
    int length;

    TemplateMethod method;

    public static TemplatePath create(int offset, int length, TemplateMethod method) {
        return new DefaultTemplatePath(offset, length, method);
    }

    @Override
    public int getOffset() {
        return offset;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public @NonNull TemplateMethodType getMethodType() {
        return method.getType();
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NonNull <T extends TemplateMethod> T getMethod() {
        return (T) method;
    }

}
