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
package com.github.lero4ka16.te4j.template.path

/**
 * @author lero4ka16
 */
class TemplatePathIterator(val text: String) : Iterator<String?> {
    private var startIndex = 0
    private var endIndex = 0

    fun previous() {
        if (endIndex == 0) {
            throw java.util.NoSuchElementException()
        }

        endIndex = startIndex
        startIndex = text.lastIndexOf('.', startIndex - 2) + 1
    }

    override fun next(): String {
        if (endIndex == -1) {
            throw NoSuchElementException()
        }

        return try {
            endIndex = text.indexOf('.', startIndex + 1)

            text.substring(startIndex, if (endIndex == -1) text.length else endIndex)
        } finally {
            startIndex = endIndex + 1
        }
    }

    override fun hasNext(): Boolean {
        return endIndex != -1
    }
}