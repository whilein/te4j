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
import com.github.lero4ka16.te4j.util.resolver.DefaultMethodResolver;
import com.github.lero4ka16.te4j.util.resolver.MethodResolver;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lero4ka16
 */
public final class Include {

    private final String path;
    private final List<IncludeArgument> args;

    public Include(String path) {
        this.path = path;
        this.args = new ArrayList<>();

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

        this.args.add(new IncludeArgument(this, from, to));
    }

    public boolean hasValues() {
        return args.size() != 1;
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

            Method found = null;

            for (MethodResolver resolver : DefaultMethodResolver.RESOLVERS) {
                found = resolver.findMethod(element, object.getClass());
                if (found != null) break;
            }

            if (found == null) {
                throw new IllegalStateException("No path found: " + path);
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

        for (IncludeArgument value : args) {
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
