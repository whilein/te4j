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

package com.github.lero4ka16.te4j.template.output;

import java.util.Arrays;

/**
 * @author lero4ka16
 */
public final class TemplateOutputBuffer extends AbstractTemplateOutput {

    private byte[] value;
    private int length;

    public TemplateOutputBuffer() {
        this(32);
    }

    public TemplateOutputBuffer(int capacity) {
        this.value = new byte[capacity];
    }

    @Override
    public String toString() {
        return new String(value, 0, length);
    }

    @Override
    public byte[] toByteArray() {
        return Arrays.copyOf(value, length);
    }

    public int getLength() {
        return length;
    }

    public void ensure(int len) {
        if (length + len > value.length) {
            value = Arrays.copyOf(value, Math.max(value.length + len, value.length * 2));
        }
    }

    @Override
    public void write(int b) {
        ensure(1);
        value[length++] = (byte) b;
    }

    public void write(byte[] bytes) {
        write(bytes, 0, bytes.length);
    }

    public void write(byte[] bytes, int off, int len) {
        if (len == 0) return;

        ensure(len);

        System.arraycopy(bytes, off, value, length, len);
        length += len;
    }

    public void reset() {
        length = 0;
    }

}
