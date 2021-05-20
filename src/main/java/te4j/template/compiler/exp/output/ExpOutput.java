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

package te4j.template.compiler.exp.output;

import lombok.NonNull;
import te4j.template.compiler.exp.Exp;
import te4j.template.compiler.exp.filter.ExpFilters;

/**
 * @author whilein
 */
public interface ExpOutput extends ExpOutputElement {

    Exp getExp();

    void applyFilters(@NonNull ExpFilters filters);

    void prepend(@NonNull String text);

    void append(@NonNull String text);

    void prepend(@NonNull Exp exp, @NonNull String text);

    void append(@NonNull Exp exp, @NonNull String text);

    ExpOutputWrite startWrite(@NonNull Exp[] values);

    ExpOutputWrite getWrite();

    void stopWrite();

    /**
     * Get or create next token
     *
     * @return Token, not null
     */
    @NonNull ExpOutput next(@NonNull Exp exp);

}
