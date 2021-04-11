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

package com.github.lero4ka16.te4j.include;

/**
 * @author lero4ka16
 */
public final class IncludeArgument {

    private final Include file;

    private final int begin;
    private final int end;

    public IncludeArgument(Include file, int begin, int end) {
        this.file = file;
        this.begin = begin;
        this.end = end;
    }

    public Include getFile() {
        return file;
    }

    public int getBegin() {
        return begin;
    }

    public int getEnd() {
        return end;
    }

    public boolean isExpression() {
        return file.charAt(begin) == '[' && file.charAt(end - 1) == ']';
    }

    public String getExpression() {
        return file.substring(begin + 1, end - 1);
    }

    public String getValue() {
        return file.substring(begin, end);
    }

}
