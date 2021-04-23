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

package te4j.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author lero4ka16
 */
@UtilityClass
public class IOUtils {

    private static final byte[] EMPTY_BYTES = new byte[0];

    public byte[] read(String name, boolean resource) throws IOException {
        if (resource) {
            try (InputStream is = ClassLoader.getSystemResourceAsStream(name)) {
                if (is == null) {
                    throw new FileNotFoundException("Resource not found: " + name);
                }

                return IOUtils.readBytes(is);
            }
        } else {
            return Files.readAllBytes(Paths.get(name));
        }
    }

    public byte[] readFile(File file) throws IOException {
        try (InputStream is = new FileInputStream(file)) {
            return readBytes(is, (int) file.length());
        }
    }

    public byte[] readBytes(InputStream is) throws IOException {
        byte[] result = new byte[1024];
        int n;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

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
