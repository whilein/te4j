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

package com.github.lero4ka16.te4j.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author lero4ka16
 */
public final class Utils {

    private static final char[] HEX = "0123456789ABCDEF".toCharArray();
    private static final byte[] EMPTY_BYTES = new byte[0];

    private Utils() {
        throw new UnsupportedOperationException();
    }

    public static byte[] readFile(File file) throws IOException {
        try (InputStream is = new FileInputStream(file)) {
            return readBytes(is, (int) file.length());
        }
    }

    public static byte[] readBytes(InputStream is) throws IOException {
        byte[] result = new byte[1024];
        int n;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        while ((n = is.read(result)) != -1) {
            baos.write(result, 0, n);
        }

        return baos.toByteArray();
    }

    public static byte[] readBytes(InputStream is, int size) throws IOException {
        if (size < 0) {
            throw new IllegalArgumentException("size");
        }

        if (size == 0) {
            return EMPTY_BYTES;
        }

        byte[] result = new byte[size];

        int off = 0;
        int len;

        while (off < size && (len = is.read(result, off, size - off)) != -1) {
            off += len;
        }

        return result;
    }

    /**
     * Переносит текст из shake_case в camelCase
     *
     * @param upper Начинается ли текст с большой буквы
     * @param value Текст
     * @return Обработанный текст
     */
    public static String toCamelCase(boolean upper, String value) {
        StringBuilder sb = new StringBuilder(value.length());
        boolean nextCapitalized = upper;

        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);

            if (ch == '_') {
                nextCapitalized = true;
                continue;
            }

            if (nextCapitalized) {
                sb.append(Character.toUpperCase(ch));
                nextCapitalized = false;
            } else {
                sb.append(Character.toLowerCase(ch));
            }
        }

        return sb.toString();
    }

    public static boolean isJUnitTest() {
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            if (element.getClassName().startsWith("org.junit.")) {
                return true;
            }
        }

        return false;
    }

    public static String stripTags(String s) {
        return s.replaceAll("<.*?>", "");
    }

    public static String escapeTags(String s) {
        StringBuilder out = null;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == '"' || c == '\'' || c == '<' || c == '>' || c == '&') {
                if (out == null) {
                    out = new StringBuilder(s.length());
                    out.append(s, 0, i);
                }

                out.append("&#");
                out.append((int) c);
                out.append(';');
            } else {
                if (out != null) {
                    out.append(c);
                }
            }
        }

        return out == null ? s : out.toString();
    }

    public static String toHexString(String value) {
        return toHexString(value.getBytes(StandardCharsets.UTF_8));
    }

    public static String toHexString(byte[] value) {
        StringBuilder result = new StringBuilder(value.length * 2);

        for (byte b : value) {
            result.append(HEX[(b >> 4) & 0xF]);
            result.append(HEX[b & 0xF]);
        }

        return result.toString();
    }

}
