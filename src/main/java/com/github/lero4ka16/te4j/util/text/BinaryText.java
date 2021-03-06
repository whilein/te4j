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

package com.github.lero4ka16.te4j.util.text;

/**
 * @author lero4ka16
 */
public class BinaryText extends Text {

    private final byte[] value;
    private final int off, len;

    public BinaryText(byte[] value, int off, int len) {
        this.value = value;

        this.off = off;
        this.len = len;
    }

    @Override
    public int length() {
        return len;
    }

    @Override
    public int charAt(int i) {
        return value[off + i];
    }
}
