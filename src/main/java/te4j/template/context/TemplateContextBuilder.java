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

package te4j.template.context;

import lombok.NonNull;
import te4j.filter.Filters;
import te4j.modifiable.watcher.ModifyWatcherManager;
import te4j.template.option.minify.Minify;
import te4j.template.option.output.Output;
import te4j.template.option.style.TemplateStyle;

import java.util.Collection;

/**
 * @author lero4ka16
 */
public interface TemplateContextBuilder {

    @NonNull TemplateContextBuilder filters(Filters filters);

    @NonNull TemplateContextBuilder style(TemplateStyle style);

    TemplateContextBuilder output(Output... types);

    TemplateContextBuilder output(Collection<Output> types);

    TemplateContextBuilder outputAll();

    TemplateContextBuilder disableAutoReloading();

    TemplateContextBuilder enableAutoReloading(ModifyWatcherManager modifyWatcherManager);

    TemplateContextBuilder enableAutoReloading();

    TemplateContextBuilder useResources();

    TemplateContextBuilder minify(Minify... options);

    TemplateContextBuilder minify(Collection<Minify> options);

    TemplateContextBuilder minifyAll();

    TemplateContext build();

}
