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

package te4j.modifiable;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.nio.file.Path;
import java.util.List;

/**
 * @author lero4ka16
 */
public final class ModifiableReference extends WeakReference<Modifiable> {

    private volatile List<Path> files;

    public ModifiableReference(Modifiable referent, ReferenceQueue<Modifiable> queue) {
        super(referent, queue);

        files = referent.getFiles();
    }

    public synchronized boolean handleModify() {
        Modifiable modifiable = get();

        if (modifiable != null) {
            modifiable.handleModify();

            files = modifiable.getFiles();

            return true;
        }

        return false;
    }

    public synchronized List<Path> getFiles() {
        return files;
    }
}
