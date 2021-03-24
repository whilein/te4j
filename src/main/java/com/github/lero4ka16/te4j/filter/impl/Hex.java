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

package com.github.lero4ka16.te4j.filter.impl;

import com.github.lero4ka16.te4j.filter.Filter;

import java.nio.charset.StandardCharsets;

/**
 * @author lero4ka16
 */
public final class Hex implements Filter {
    private static final char[] HEX = "0123456789ABCDEF".toCharArray();

    @Override
    public String getName() {
        return "hex";
    }

    public static String process(String value) {
        return process(value.getBytes(StandardCharsets.UTF_8));
    }

    public static String process(byte[] value) {
        StringBuilder result = new StringBuilder(value.length * 2);

        for (byte b : value) {
            result.append(HEX[(b >> 4) & 0xF]);
            result.append(HEX[b & 0xF]);
        }

        return result.toString();
    }
}
