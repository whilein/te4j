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

package com.github.lero4ka16.te4j.template.output;

/**
 * @author lero4ka16
 */
public class TemplateOutputType {

    public static final int STRING = 1;
    public static final int BYTES = 2;
    public static final int[] VALUES = new int[]{STRING, BYTES};

    public static String getPrefix(int bit) {
        switch (bit) {
            case 1:
                return "STRING_";
            case 2:
                return "BYTES_";
            default:
                throw new IllegalArgumentException("Undefined bit: " + bit);
        }
    }

}
