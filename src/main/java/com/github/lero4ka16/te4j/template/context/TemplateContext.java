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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
     * @param type  Type
     * @param bytes Data
     * @return New compiled template
     */
    public <T> Template<T> loadBytes(ITypeRef<T> type, byte[] bytes) {
        return parseBytes(bytes).compile(
                useResources ? null : modifyWatcherManager,
                ".", null, type
        );
    }

    public <T> Template<T> load(ITypeRef<T> type, String name) {
        return parse(name).compile(
                useResources ? null : modifyWatcherManager,
                getParent(name), name, type
        );
    }

    public <T> Template<T> loadFile(ITypeRef<T> type, File file) {
        return parseFile(file).compile(
                modifyWatcherManager,
                file.getAbsoluteFile().getParent(),
                file.getAbsolutePath(),
                type
        );
    }

    public <T> Template<T> loadFile(ITypeRef<T> type, Path path) {
        return parseFile(path).compile(
                modifyWatcherManager,
                path.toAbsolutePath().getParent().toString(),
                path.toAbsolutePath().toString(),
                type
        );
    }

    public <T> Template<T> loadBytes(Class<T> type, byte[] bytes) {
        return loadBytes(new ClassRef<>(type), bytes);
    }

    public <T> Template<T> load(Class<T> type, String name) {
        return load(new ClassRef<>(type), name);
    }

    public <T> Template<T> loadFile(Class<T> type, File file) {
        return loadFile(new ClassRef<>(type), file);
    }

    public <T> Template<T> loadFile(Class<T> type, Path path) {
        return loadFile(new ClassRef<>(type), path);
    }

    public ParsedTemplate parseBytes(byte[] bytes) {
        return new TemplateReader(this, bytes).readTemplate();
    }

    public ParsedTemplate parse(String name) {
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

    public ParsedTemplate parseFile(File file) {
        try {
            return parseBytes(Utils.readFile(file));
        } catch (IOException e) {
            throw new TemplateLoadException("Cannot read template", e);
        }
    }

    public ParsedTemplate parseFile(Path path) {
        try {
            return parseBytes(Files.readAllBytes(path));
        } catch (IOException e) {
            throw new TemplateLoadException("Cannot read template", e);
        }
    }

}
