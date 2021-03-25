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
import com.github.lero4ka16.te4j.modifiable.watcher.ModifyWatcherManager;
import com.github.lero4ka16.te4j.template.Template;
import com.github.lero4ka16.te4j.template.context.TemplateContext;
import com.github.lero4ka16.te4j.template.context.TemplateContextBuilder;
import com.github.lero4ka16.te4j.template.parse.ParsedTemplate;
import com.github.lero4ka16.te4j.util.type.ref.ITypeRef;
import com.github.lero4ka16.te4j.util.type.ref.TypeRef;
import org.jetbrains.annotations.NotNull;

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

    private static final Filters FILTERS
            = new Filters();

    private static final ModifyWatcherManager DEFAULT_MODIFY_WATCHER
            = new ModifyWatcherManager();

    private static final TemplateContext DEFAULTS = custom()
            .replace(DEL_ALL)
            .outputTypes(STRING | BYTES)
            .enableHotReloading(DEFAULT_MODIFY_WATCHER)
            .build();

    private Te4j() {
        throw new UnsupportedOperationException();
    }

    public static ModifyWatcherManager getDefaultModifyWatcher() {
        return DEFAULT_MODIFY_WATCHER;
    }

    public static @NotNull Filters getFilters() {
        return FILTERS;
    }

    /**
     * @return Default template context
     */
    public static @NotNull TemplateContext defaults() {
        return DEFAULTS;
    }

    public static @NotNull <T> Template<T> loadBytes(@NotNull ITypeRef<T> type,
                                                     byte @NotNull [] data) {
        return DEFAULTS.loadBytes(type, data);
    }

    public static @NotNull <T> Template<T> loadString(@NotNull ITypeRef<T> type,
                                                      @NotNull String data) {
        return DEFAULTS.loadString(type, data);
    }

    public static @NotNull <T> Template<T> load(@NotNull ITypeRef<T> type,
                                                @NotNull String name) {
        return DEFAULTS.load(type, name);
    }

    public static @NotNull <T> Template<T> loadFile(@NotNull ITypeRef<T> type,
                                                    @NotNull File file) {
        return DEFAULTS.loadFile(type, file);
    }

    public static @NotNull <T> Template<T> loadFile(@NotNull ITypeRef<T> type,
                                                    @NotNull Path path) {
        return DEFAULTS.loadFile(type, path);
    }

    public static @NotNull <T> Template<T> load(@NotNull TypeRef<T> type,
                                                @NotNull String name) {
        return DEFAULTS.load(type, name);
    }

    public static @NotNull <T> Template<T> loadFile(@NotNull TypeRef<T> type,
                                                    @NotNull File file) {
        return DEFAULTS.loadFile(type, file);
    }

    public static @NotNull <T> Template<T> loadFile(@NotNull TypeRef<T> type,
                                                    @NotNull Path path) {
        return DEFAULTS.loadFile(type, path);
    }

    public static @NotNull <T> Template<T> loadBytes(@NotNull Class<T> type,
                                                     byte @NotNull [] bytes) {
        return DEFAULTS.loadBytes(type, bytes);
    }

    public static @NotNull <T> Template<T> loadString(@NotNull Class<T> type,
                                                      @NotNull String data) {
        return DEFAULTS.loadString(type, data);
    }

    public static @NotNull <T> Template<T> load(@NotNull Class<T> type,
                                                @NotNull String name) {
        return DEFAULTS.load(type, name);
    }

    public static @NotNull <T> Template<T> loadFile(@NotNull Class<T> type,
                                                    @NotNull File file) {
        return DEFAULTS.loadFile(type, file);
    }

    public static @NotNull <T> Template<T> loadFile(@NotNull Class<T> type,
                                                    @NotNull Path path) {
        return DEFAULTS.loadFile(type, path);
    }

    public static ParsedTemplate parseBytes(byte[] bytes) {
        return DEFAULTS.parseBytes(bytes);
    }


    public static @NotNull ParsedTemplate parse(@NotNull String name) {
        return DEFAULTS.parse(name);
    }

    public static @NotNull ParsedTemplate parseFile(@NotNull File file) {
        return DEFAULTS.parseFile(file);
    }

    public static @NotNull ParsedTemplate parseFile(@NotNull Path path) {
        return DEFAULTS.parseFile(path);
    }

    /**
     * @return New custom template context builder
     */
    public static @NotNull TemplateContextBuilder custom() {
        return new TemplateContextBuilder();
    }
}
