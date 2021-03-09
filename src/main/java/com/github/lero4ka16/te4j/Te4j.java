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

package com.github.lero4ka16.te4j;

import com.github.lero4ka16.te4j.filter.Filters;
import com.github.lero4ka16.te4j.template.Template;
import com.github.lero4ka16.te4j.template.context.TemplateContext;
import com.github.lero4ka16.te4j.template.context.TemplateContextBuilder;
import com.github.lero4ka16.te4j.template.parse.ParsedTemplate;
import com.github.lero4ka16.te4j.util.type.ref.TypeRef;

import java.io.File;
import java.nio.file.Path;

/**
 * @author lero4ka16
 */
public final class Te4j {

    public static final int DEL_CR = 1;
    public static final int DEL_LF = 2;
    public static final int DEL_REPEATING_SPACES = 4;
    public static final int DEL_TAB = 8;

    public static final int DEL_ALL = DEL_CR | DEL_LF | DEL_REPEATING_SPACES | DEL_TAB;

    public static final int STRING = 1;
    public static final int BYTES = 2;

    public static final int[] OUTPUT_TYPES = new int[]{STRING, BYTES};

    private Te4j() {
        throw new UnsupportedOperationException();
    }

    private static final Filters FILTERS = new Filters();

    private static final TemplateContext DEFAULTS = custom()
            .replace(DEL_ALL)
            .build();

    public static String getOutputPrefix(int bit) {
        switch (bit) {
            case 1:
                return "STRING_";
            case 2:
                return "BYTES_";
            default:
                throw new IllegalArgumentException("Undefined bit: " + bit);
        }
    }

    public static Filters getFilters() {
        return FILTERS;
    }

    /**
     * @return Default template context
     */
    public static TemplateContext defaults() {
        return DEFAULTS;
    }

    public static <BoundType> Template<BoundType> load(TypeRef<BoundType> type, String name) {
        return DEFAULTS.load(type, name);
    }

    public static <BoundType> Template<BoundType> loadFile(TypeRef<BoundType> type, File file) {
        return DEFAULTS.loadFile(type, file);
    }

    public static <BoundType> Template<BoundType> loadFile(TypeRef<BoundType> type, Path path) {
        return DEFAULTS.loadFile(type, path);
    }

    public static <BoundType> Template<BoundType> load(Class<BoundType> type, String name) {
        return DEFAULTS.load(type, name);
    }

    public static <BoundType> Template<BoundType> loadFile(Class<BoundType> type, File file) {
        return DEFAULTS.loadFile(type, file);
    }

    public static <BoundType> Template<BoundType> loadFile(Class<BoundType> type, Path path) {
        return DEFAULTS.loadFile(type, path);
    }

    public static ParsedTemplate parse(String name) {
        return DEFAULTS.parse(name);
    }

    public static ParsedTemplate parseFile(File file) {
        return DEFAULTS.parseFile(file);
    }

    public static ParsedTemplate parseFile(Path path) {
        return DEFAULTS.parseFile(path);
    }

    /**
     * @return New custom template context builder
     */
    public static TemplateContextBuilder custom() {
        return new TemplateContextBuilder();
    }
}
