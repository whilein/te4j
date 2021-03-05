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

package com.github.lero4ka16.te4j.template.provider.root;

import com.github.lero4ka16.te4j.util.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author lero4ka16
 */
public class ResourceTemplateProviderRoot extends TemplateProviderRoot {

    private final String root;

    public ResourceTemplateProviderRoot(String root) {
        if (!root.endsWith("/")) {
            root += "/";
        }

        if (root.equals("./")) {
            root = "";
        }

        this.root = root;
    }

    @Override
    public byte[] read(String relative) throws IOException {
        try (InputStream is = ClassLoader.getSystemResourceAsStream(root + relative)) {
            if (is == null) {
                throw new FileNotFoundException(root + relative);
            }

            return Utils.readBytes(is);
        }
    }

    @Override
    public String resolve(String relative) {
        return root + relative;
    }
}
