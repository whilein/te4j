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

package com.github.lero4ka16.te4j.template.replace;

/**
 * @author lero4ka16
 */
public final class ReplaceStrategy {

    public static final int DEL_CR = 1;
    public static final int DEL_LF = 2;
    public static final int DEL_REPEATING_SPACES = 4;
    public static final int DEL_TAB = 8;

    public static final int ALL = DEL_CR | DEL_LF | DEL_REPEATING_SPACES | DEL_TAB;
    public static final int NONE = 0;

    private ReplaceStrategy() {
        throw new UnsupportedOperationException();
    }

}
