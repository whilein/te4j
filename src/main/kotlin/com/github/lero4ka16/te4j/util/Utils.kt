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
package com.github.lero4ka16.te4j.util

import java.io.*

/**
 * @author lero4ka16
 */
class Utils private constructor() {
    companion object {
        private val EMPTY_BYTES = ByteArray(0)

        @JvmStatic
        @Throws(IOException::class)
        fun readFile(file: File): ByteArray {
            FileInputStream(file).use {
                return readBytes(
                    it, file.length().toInt()
                )
            }
        }

        @JvmStatic
        @Throws(IOException::class)
        fun readBytes(stream: InputStream): ByteArray {
            val result = ByteArray(1024)
            var n: Int
            val baos = ByteArrayOutputStream()

            while (stream.read(result).also { n = it } != -1) {
                baos.write(result, 0, n)
            }

            return baos.toByteArray()
        }

        @JvmStatic
        @Throws(IOException::class)
        fun readBytes(input: InputStream, size: Int): ByteArray {
            require(size >= 0) { "size" }
            if (size == 0) {
                return EMPTY_BYTES
            }
            val result = ByteArray(size)
            var off = 0
            var len = 0

            while (off < size && input.read(result, off, size - off).also { len = it } != -1) {
                off += len
            }

            return result
        }

        /**
         * Переносит текст из shake_case в camelCase
         *
         * @param upper Начинается ли текст с большой буквы
         * @param value Текст
         * @return Обработанный текст
         */
        @JvmStatic
        fun toCamelCase(upper: Boolean, value: String): String {
            val sb = StringBuilder(value.length)
            var nextCapitalized = upper
            for (element in value) {
                if (element == '_') {
                    nextCapitalized = true
                    continue
                }
                if (nextCapitalized) {
                    sb.append(Character.toUpperCase(element))
                    nextCapitalized = false
                } else {
                    sb.append(Character.toLowerCase(element))
                }
            }
            return sb.toString()
        }

        @JvmStatic
        fun deleteDirectory(dir: File): Boolean {
            val files = dir.listFiles()

            for (file in files!!) {
                val result: Boolean = if (file.isDirectory) {
                    deleteDirectory(file)
                } else {
                    file.delete()
                }

                if (!result) return false
            }

            return dir.delete()
        }

        @JvmStatic
        val isJUnitTest: Boolean
            get() {
                for (element in Thread.currentThread().stackTrace) {
                    if (element.className.startsWith("org.junit.")) {
                        return true
                    }
                }
                return false
            }
    }

    init {
        throw UnsupportedOperationException()
    }
}