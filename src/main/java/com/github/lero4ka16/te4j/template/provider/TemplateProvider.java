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

package com.github.lero4ka16.te4j.template.provider;

import com.github.lero4ka16.te4j.template.ParsedTemplate;
import com.github.lero4ka16.te4j.template.compiled.Template;
import com.github.lero4ka16.te4j.template.exception.TemplateLoadException;
import com.github.lero4ka16.te4j.template.provider.root.TemplateProviderRoot;
import com.github.lero4ka16.te4j.template.reader.TemplateReader;
import com.github.lero4ka16.te4j.util.Utils;
import com.github.lero4ka16.te4j.util.replace.ReplaceStrategy;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Data
public final class TemplateProvider {

    private final TemplateProviderRoot root;
    private final ReplaceStrategy replaceStrategy;

    public <BoundType> Template<BoundType> load(Class<BoundType> type, byte[] bytes) {
        return parse(bytes).compile(type);
    }

    public <BoundType> Template<BoundType> load(Class<BoundType> type, String name) {
        return parse(name).compile(type);
    }

    public <BoundType> Template<BoundType> loadFile(Class<BoundType> type, File file) {
        return parseFile(file).compile(type);
    }

    public <BoundType> Template<BoundType> loadFile(Class<BoundType> type, Path path) {
        return parseFile(path).compile(type);
    }

    public ParsedTemplate parse(byte[] bytes) {
        return new TemplateReader(this, bytes).readTemplate();
    }

    public ParsedTemplate parse(String name) {
        try {
            return parse(root.read(name));
        } catch (IOException e) {
            throw new TemplateLoadException("Cannot read template", e);
        }
    }

    public ParsedTemplate parseFile(File file) {
        try {
            return parse(Utils.readFile(file));
        } catch (IOException e) {
            throw new TemplateLoadException("Cannot read template", e);
        }
    }

    public ParsedTemplate parseFile(Path path) {
        try {
            return parse(Files.readAllBytes(path));
        } catch (IOException e) {
            throw new TemplateLoadException("Cannot read template", e);
        }
    }

}
