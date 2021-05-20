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

package te4j.include;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * @author whilein
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class IncludeString implements IncludePathElement {

    private final String string;

    public static IncludePathElement create(@NonNull String string) {
        return new IncludeString(string);
    }

    @Override
    public boolean isExpression() {
        return false;
    }

    @Override
    public String getValue() {
        return string;
    }
}
