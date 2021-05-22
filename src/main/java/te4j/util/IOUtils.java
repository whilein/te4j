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

package te4j.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * @author whilein
 */
@UtilityClass
public class IOUtils {

    private static final byte[] EMPTY_BYTES = new byte[0];

    public byte[] readFile(File file) throws IOException {
        try (InputStream is = new FileInputStream(file)) {
            return readBytes(is, (int) file.length());
        }
    }

    public static @NotNull Optional<@NotNull String> getParentFile(final @NonNull String file) {
        for (int i = file.length() - 1; i >= 0; i--) {
            val ch = file.charAt(i);

            if (ch == '/' || ch == '\\')
                return Optional.of(file.substring(0, i));
        }

        return Optional.empty();
    }

    public byte[] readBytes(InputStream is) throws IOException {
        val result = new byte[1024];
        int n;

        val baos = new ByteArrayOutputStream();

        while ((n = is.read(result)) != -1) {
            baos.write(result, 0, n);
        }

        return baos.toByteArray();
    }

    public byte[] readBytes(InputStream is, int size) throws IOException {
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
    public String toCamelCase(boolean upper, String value) {
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

    public boolean deleteDirectory(@NonNull Path dir) {
        try {
            try (DirectoryStream<Path> paths = Files.newDirectoryStream(dir)) {
                for (Path path : paths) {
                    if (Files.isDirectory(path)) {
                        boolean result = deleteDirectory(path);
                        if (!result) return false;
                    } else {
                        Files.delete(path);
                    }
                }
            }

            Files.delete(dir);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean deleteDirectory(@NonNull File dir) {
        File[] files = dir.listFiles();
        assert files != null;

        for (File file : files) {
            boolean result;

            if (file.isDirectory()) {
                result = deleteDirectory(file);
            } else {
                result = file.delete();
            }

            if (!result) return false;
        }

        return dir.delete();
    }

}
