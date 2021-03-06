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

package com.github.lero4ka16.te4j.include;

import com.github.lero4ka16.te4j.template.path.TemplatePathIterator;
import com.github.lero4ka16.te4j.util.Utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lero4ka16
 */
public class IncludeFile {

    private final String path;
    private final List<IncludeParam> values;

    public IncludeFile(String path) {
        this.path = path;
        this.values = new ArrayList<>();

        int begin = 0;

        for (int i = 0; i < path.length(); i++) {
            char ch = path.charAt(i);

            if (ch == '[') {
                add(begin, i);
                begin = i + 1;
            } else if (ch == ']') {
                add(begin - 1, i + 1);
                begin = i + 1;
            }
        }

        add(begin, path.length());
    }

    private void add(int from, int to) {
        if (from == to) return;
        this.values.add(new IncludeParam(this, from, to));
    }

    public boolean hasValues() {
        return values.size() != 1;
    }

    public String format() {
        return path;
    }

    public Object resolve(String path, Object object) {
        if (path.equals("$")) { // чтобы не делать пустые скобки
            return object;
        }

        TemplatePathIterator iterator = new TemplatePathIterator(path);

        while (iterator.hasNext()) {
            String element = iterator.next();

            String upperCamelCase = "get" + Utils.toCamelCase(true, element);
            String lowerCamelCase = Utils.toCamelCase(false, element);

            Method found =  Utils.getMethod(object.getClass(), upperCamelCase);

            if (found == null) {
                found = Utils.getMethod(object.getClass(), lowerCamelCase);
            }

            if (found == null) {
                throw new IllegalStateException();
            }

            try {
                object = found.invoke(object);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }

        return object;
    }

    public String format(Object element) {
        StringBuilder sb = new StringBuilder();

        for (IncludeParam value : values) {
            if (value.isExpression()) {
                sb.append(resolve(value.getExpression(), element));
            } else {
                sb.append(value.getValue());
            }
        }

        return sb.toString();
    }

    public String substring(int begin, int end) {
        return path.substring(begin, end);
    }

    public char charAt(int i) {
        return path.charAt(i);
    }

    @Override
    public String toString() {
        return path;
    }

}
