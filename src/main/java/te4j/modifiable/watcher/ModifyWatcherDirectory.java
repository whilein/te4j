/*
 *    Copyright 2021 Whilein
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

package te4j.modifiable.watcher;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import te4j.modifiable.ModifiableReference;

import java.io.Closeable;
import java.util.Collection;

/**
 * @author whilein
 */
public interface ModifyWatcherDirectory extends Closeable {

    boolean hasEntries();

    void addEntry(@NotNull String name, @NotNull ModifiableReference reference);
    void removeEntry(@NotNull String name, @NotNull ModifiableReference reference);

    @UnmodifiableView @NotNull Collection<@NotNull ModifiableReference> getEntries(@NotNull String name);
    @Override void close();
}
