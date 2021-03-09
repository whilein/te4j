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

package com.github.lero4ka16.te4j.watcher;

import java.lang.ref.WeakReference;
import java.nio.file.Path;

/**
 * @author lero4ka16
 */
public class ModifiableHolder extends WeakReference<Modifiable> {

    private Path[] lastFiles;

    public ModifiableHolder(Modifiable referent) {
        super(referent);
    }

    public void setModified() {
        Modifiable modifiable = get();

        if (modifiable != null) {
            modifiable.setModified();
        }
    }

    public boolean wasDeleted() {
        return get() == null;
    }

    public Path[] getFiles() {
        Modifiable modifiable = get();

        if (modifiable == null) {
            return lastFiles;
        }

        return lastFiles = modifiable.getFiles();
    }
}
