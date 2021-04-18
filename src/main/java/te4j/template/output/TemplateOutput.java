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

package te4j.template.output;

import lombok.NonNull;

/**
 * @author lero4ka16
 */
public interface TemplateOutput {

    void put(@NonNull String value);

    void put(Object object);

    void put(double d);

    void put(float f);

    void put(long value);

    void put(int value);

    void write(byte[] bytes);

    void write(byte[] bytes, int off, int len);

    void write(int ch);

    byte[] toByteArray();

}
