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

import te4j.Te4j;
import te4j.modifiable.watcher.ModifyWatcherManager;

/**
 * @author lero4ka16
 */
public final class TemplateContextBuilder {

    private boolean useResources;
    private ModifyWatcherManager modifyWatcherManager;

    private int outputTypes;
    private int replace;

    public TemplateContextBuilder() {
    }

    public TemplateContextBuilder(TemplateContext another) {
        this.useResources = another.useResources();
        this.modifyWatcherManager = another.getModifyWatcherManager();
        this.outputTypes = another.getOutputTypes();
        this.replace = another.getReplace();
    }

    public TemplateContextBuilder outputTypes(int bits) {
        this.outputTypes = bits;
        return this;
    }

    public TemplateContextBuilder disableHotReloading() {
        this.modifyWatcherManager = null;
        return this;
    }

    public TemplateContextBuilder enableHotReloading(ModifyWatcherManager modifyWatcherManager) {
        this.modifyWatcherManager = modifyWatcherManager;
        return this;
    }

    public TemplateContextBuilder enableHotReloading() {
        return enableHotReloading(Te4j.getDefaultModifyWatcher());
    }

    public TemplateContextBuilder useResources() {
        this.useResources = true;
        return this;
    }

    public TemplateContextBuilder replace(int value) {
        this.replace = value;
        return this;
    }

    public TemplateContext build() {
        if (outputTypes == 0) {
            outputTypes(Te4j.STRING | Te4j.BYTES);
        }

        return new TemplateContext(useResources, modifyWatcherManager, outputTypes, replace);
    }

}
