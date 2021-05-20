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

package te4j.template.option.style;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * @author whilein
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ImmutableTemplateStyle implements TemplateStyle {

    private final char[] styles;

    private static final TemplateStyle DEFAULTS = new Builder()
            .style(StyleAspect.BEGIN_METHOD, '<')
            .style(StyleAspect.END_METHOD, '>')
            .style(StyleAspect.METHOD_MARKER, '*')
            .style(StyleAspect.BEGIN_VALUE, '^')
            .style(StyleAspect.END_VALUE, '^')
            .build();

    public static @NonNull TemplateStyle getDefaults() {
        return DEFAULTS;
    }

    /**
     * Creates new template style builder with defaults
     *
     * @return New builder
     */
    public static @NonNull TemplateStyleBuilder builder() {
        return new Builder().inherit(DEFAULTS);
    }

    @Override
    public char style(@NonNull StyleAspect aspect) {
        return styles[aspect.ordinal()];
    }

    private static class Builder implements TemplateStyleBuilder {

        private final char[] styles = new char[StyleAspect.LENGTH];

        @Override
        public @NonNull TemplateStyleBuilder inherit(@NonNull TemplateStyle another) {
            for (StyleAspect aspect : StyleAspect.VALUES) {
                char value = another.style(aspect);

                if (value != 0) {
                    style(aspect, value);
                }
            }

            return this;
        }

        @Override
        public @NonNull TemplateStyleBuilder style(@NonNull StyleAspect aspect, char value) {
            styles[aspect.ordinal()] = value;
            return this;
        }

        @Override
        public @NonNull TemplateStyle build() {
            return new ImmutableTemplateStyle(styles);
        }

    }
}
