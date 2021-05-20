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

package te4j.template;

import lombok.NonNull;
import te4j.template.output.TemplateOutput;
import te4j.template.output.TemplateOutputBuffer;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author whilein
 */
public interface Template<T> {

    ThreadLocal<TemplateOutput> bytesOptimized
            = ThreadLocal.withInitial(TemplateOutputBuffer::create);

    ThreadLocal<StringBuilder> stringOptimized
            = ThreadLocal.withInitial(StringBuilder::new);

    @NonNull String[] getIncludes();

    @NonNull String renderAsString(@NonNull T object);

    byte @NonNull [] renderAsBytes(@NonNull T object);

    void renderTo(@NonNull T object, @NonNull OutputStream os) throws IOException;

}
