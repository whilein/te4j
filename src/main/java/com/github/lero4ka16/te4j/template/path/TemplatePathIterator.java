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

package com.github.lero4ka16.te4j.template.path;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class TemplatePathIterator implements Iterator<String> {

    private final String text;

    private int startIndex;
    private int endIndex;

    public TemplatePathIterator(String text) {
        this.text = text;
    }

    @Override
    public String next() {
        if (endIndex == -1) {
            throw new NoSuchElementException();
        }

        try {
            endIndex = text.indexOf('.', startIndex + 1);

            return text.substring(startIndex, endIndex == -1 ? text.length() : endIndex);
        } finally {
            startIndex = endIndex + 1;
        }
    }

    @Override
    public boolean hasNext() {
        return endIndex != -1;
    }

}
