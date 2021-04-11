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

package te4j.template.parse;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import te4j.modifiable.watcher.ModifyWatcherManager;
import te4j.template.Template;
import te4j.template.compiler.TemplateCompiler;
import te4j.template.context.TemplateContext;
import te4j.template.path.TemplatePath;
import te4j.template.source.TemplateSource;
import te4j.util.type.ref.TypeReference;

import java.util.List;

/**
 * @author lero4ka16
 */
public final class StandardParsedTemplate extends ParsedTemplate {

    private final List<TemplatePath> paths;

    public StandardParsedTemplate(TemplateContext context, List<TemplatePath> paths,
                                  byte[] content, int offset, int length) {
        super(context, content, offset, length);

        if (paths.isEmpty()) {
            throw new IllegalArgumentException("paths");
        }

        this.paths = paths;
    }

    @Override
    public List<TemplatePath> getPaths() {
        return paths;
    }

    @Override
    public <T> Template<T> compile(@Nullable ModifyWatcherManager modifyWatcherManager,
                                   @NotNull String parentFile,
                                   @NotNull TemplateSource source,
                                   @NotNull TypeReference<T> type) {
        Template<T> result = TemplateCompiler.INSTANCE.compile(context, this, type, parentFile);

        if (modifyWatcherManager != null) {
            result = Template.wrapHotReloading(modifyWatcherManager, context, result, type, source);
        }

        return result;
    }

    @Override
    public boolean hasPaths() {
        return true;
    }

}
