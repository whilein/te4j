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

package te4j.include;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.var;
import te4j.template.path.DefaultTemplatePathIterator;
import te4j.util.resolver.DefaultMethodResolver;
import te4j.util.resolver.MethodResolver;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lero4ka16
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultIncludePath implements IncludePath {

    String path;
    List<IncludePathElement> args;

    public static IncludePath create(@NonNull String path) {
        var args = new ArrayList<IncludePathElement>();
        int begin = 0;

        for (int i = 0; i < path.length(); i++) {
            char ch = path.charAt(i);

            if (ch == '[') {
                args.add(IncludeString.create(path.substring(begin, i)));
                begin = i + 1;
            } else if (ch == ']') {
                args.add(IncludeExpression.create(path.substring(begin, i)));
                begin = i + 1;
            }
        }

        if (begin != path.length()) {
            args.add(IncludeString.create(path.substring(begin)));
        }

        return new DefaultIncludePath(path, args);
    }

    @Override
    public boolean hasExpressions() {
        return args.size() != 1;
    }

    @Override
    public String format() {
        return path;
    }

    private Object resolve(String path, Object object) {
        if (path.equals("$")) { // чтобы не делать пустые скобки
            return object;
        }

        var iterator = DefaultTemplatePathIterator.create(path);

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

    @Override
    public String format(Object element) {
        var sb = new StringBuilder();

        for (IncludePathElement value : args) {
            if (value.isExpression()) {
                sb.append(resolve(value.getValue(), element));
            } else {
                sb.append(value.getValue());
            }
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return path;
    }

}
