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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.Arrays;

/**
 * @author lero4ka16
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class TemplateOutputBuffer extends AbstractTemplateOutput {

    private byte[] value;
    private int length;

    public static @NonNull TemplateOutput create(int capacity) {
        return new TemplateOutputBuffer(new byte[capacity], 0);
    }

    public static @NonNull TemplateOutput create() {
        return create(32);
    }

    @Override
    public String toString() {
        return new String(value, 0, length);
    }

    @Override
    public byte[] toByteArray() {
        return Arrays.copyOf(value, length);
    }

    private void ensure(int len) {
        if (length + len > value.length) {
            value = Arrays.copyOf(value, Math.max(value.length + len, value.length * 2));
        }
    }

    @Override
    public void write(int b) {
        ensure(1);
        value[length++] = (byte) b;
    }

    @Override
    public void write(byte[] bytes) {
        write(bytes, 0, bytes.length);
    }

    @Override
    public void write(byte[] bytes, int off, int len) {
        if (len == 0) return;

        ensure(len);

        System.arraycopy(bytes, off, value, length, len);
        length += len;
    }

    @Override
    public void reset() {
        length = 0;
    }

}
