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

package com.github.lero4ka16.te4j.template.context;

import com.github.lero4ka16.te4j.modifiable.watcher.ModifyWatcherManager;
import com.github.lero4ka16.te4j.template.Template;
import com.github.lero4ka16.te4j.template.exception.TemplateLoadException;
import com.github.lero4ka16.te4j.template.parse.ParsedTemplate;
import com.github.lero4ka16.te4j.template.reader.TemplateReader;
import com.github.lero4ka16.te4j.util.Utils;
import com.github.lero4ka16.te4j.util.type.ref.ClassRef;
import com.github.lero4ka16.te4j.util.type.ref.ITypeRef;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author lero4ka16
 */
public final class TemplateContext {

    private final boolean useResources;
    private final ModifyWatcherManager modifyWatcherManager;

    private final int outputTypes;
    private final int replace;

    public TemplateContext(boolean useResources, ModifyWatcherManager modifyWatcherManager,
                           int outputTypes, int replace) {
        this.useResources = useResources;
        this.modifyWatcherManager = modifyWatcherManager;
        this.outputTypes = outputTypes;
        this.replace = replace;
    }

    public int getOutputTypes() {
        return outputTypes;
    }

    public int getReplace() {
        return replace;
    }

    private String getParent(String name) {
        int separator = -1;

        for (int i = name.length() - 1; i >= 0; i--) {
            char ch = name.charAt(i);

            if (ch == '/' || ch == '\\') {
                separator = i;
                break;
            }
        }

        if (separator == -1) {
            return ".";
        }

        return name.substring(0, separator);
    }

    /**
     * Compiles new template from bytes
     *
     * @param type Type
     * @param data Bytes
     * @param <T>  Type of an object which will be passed for rendering
     * @return New compiled template
     */
    public @NotNull <T> Template<T> loadBytes(@NotNull ITypeRef<T> type,
                                              byte @NotNull [] data) {
        return parseBytes(data).compile(
                useResources ? null : modifyWatcherManager,
                ".", null, type
        );
    }

    public @NotNull <T> Template<T> loadString(@NotNull ITypeRef<T> type,
                                               @NotNull String data) {
        return loadBytes(type, data.getBytes(StandardCharsets.UTF_8));
    }

    public @NotNull <T> Template<T> load(@NotNull ITypeRef<T> type,
                                         @NotNull String name) {
        return parse(name).compile(
                useResources ? null : modifyWatcherManager,
                getParent(name), name, type
        );
    }

    public @NotNull <T> Template<T> loadFile(@NotNull ITypeRef<T> type,
                                             @NotNull File file) {
        return parseFile(file).compile(
                modifyWatcherManager,
                file.getAbsoluteFile().getParent(),
                file.getAbsolutePath(),
                type
        );
    }

    public @NotNull <T> Template<T> loadFile(@NotNull ITypeRef<T> type,
                                             @NotNull Path path) {
        return parseFile(path).compile(
                modifyWatcherManager,
                path.toAbsolutePath().getParent().toString(),
                path.toAbsolutePath().toString(),
                type
        );
    }

    public @NotNull <T> Template<T> loadBytes(@NotNull Class<T> type,
                                              byte @NotNull [] bytes) {
        return loadBytes(new ClassRef<>(type), bytes);
    }

    public @NotNull <T> Template<T> loadString(@NotNull Class<T> type,
                                               @NotNull String data) {
        return loadBytes(type, data.getBytes(StandardCharsets.UTF_8));
    }

    public @NotNull <T> Template<T> load(@NotNull Class<T> type,
                                         @NotNull String name) {
        return load(new ClassRef<>(type), name);
    }

    public @NotNull <T> Template<T> loadFile(@NotNull Class<T> type,
                                             @NotNull File file) {
        return loadFile(new ClassRef<>(type), file);
    }

    public @NotNull <T> Template<T> loadFile(@NotNull Class<T> type,
                                             @NotNull Path path) {
        return loadFile(new ClassRef<>(type), path);
    }

    public @NotNull ParsedTemplate parseBytes(byte @NotNull [] data) {
        return new TemplateReader(this, data).readTemplate();
    }

    public @NotNull ParsedTemplate parseString(@NotNull String data) {
        return parseBytes(data.getBytes(StandardCharsets.UTF_8));
    }

    public @NotNull ParsedTemplate parse(@NotNull String name) {
        if (useResources) {
            try {
                InputStream is = ClassLoader.getSystemResourceAsStream(name);

                if (is == null) {
                    throw new FileNotFoundException("Resource not found: " + name);
                }

                return parseBytes(Utils.readBytes(is));
            } catch (IOException e) {
                throw new TemplateLoadException("Cannot read template", e);
            }
        } else {
            return parseFile(Paths.get(name));
        }
    }

    public @NotNull ParsedTemplate parseFile(@NotNull File file) {
        try {
            return parseBytes(Utils.readFile(file));
        } catch (IOException e) {
            throw new TemplateLoadException("Cannot read template", e);
        }
    }

    public @NotNull ParsedTemplate parseFile(@NotNull Path path) {
        try {
            return parseBytes(Files.readAllBytes(path));
        } catch (IOException e) {
            throw new TemplateLoadException("Cannot read template", e);
        }
    }

}
