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

import com.github.lero4ka16.te4j.template.provider.root.FileTemplateProviderRoot;
import com.github.lero4ka16.te4j.template.provider.root.PathTemplateProviderRoot;
import com.github.lero4ka16.te4j.template.provider.root.ResourceTemplateProviderRoot;
import com.github.lero4ka16.te4j.template.provider.root.TemplateProviderRoot;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author lero4ka16
 */
public class TemplateProviderBuilder {

    private TemplateProviderRoot root;
    private int replaceStrategy;

    public TemplateProviderBuilder use(TemplateProviderRoot root) {
        this.root = root;
        return this;
    }

    public TemplateProviderBuilder useFiles() {
        return useFiles(".");
    }

    public TemplateProviderBuilder useResources() {
        return useResources(".");
    }

    public TemplateProviderBuilder useFiles(String filename) {
        return useFiles(Paths.get(filename));
    }

    public TemplateProviderBuilder useFiles(Path path) {
        return use(new PathTemplateProviderRoot(path));
    }

    public TemplateProviderBuilder useFiles(File file) {
        return use(new FileTemplateProviderRoot(file));
    }

    public TemplateProviderBuilder useResources(String resource) {
        return use(new ResourceTemplateProviderRoot(resource));
    }

    public TemplateProviderBuilder replaceStrategy(int value) {
        this.replaceStrategy = value;
        return this;
    }

    public TemplateProvider build() {
        if (root == null) return useFiles().build();
        return new TemplateProvider(root, replaceStrategy);
    }

}
