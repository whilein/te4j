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

package com.github.lero4ka16.te4j.filter.impl

import com.github.lero4ka16.te4j.filter.Filter

/**
 * @author lero4ka16
 */
class Capitalize : Filter {

    override val name: String = "capitalize"

    override fun apply(value: String): String {
        return "${javaClass.name}.process($value)"
    }

    companion object {
        @JvmStatic
        fun process(value: String): String {
            val ch = value[0]
            val mod = Character.toTitleCase(ch)
            if (ch == mod) return value

            val chars = value.toCharArray()
            chars[0] = mod
            return String(chars)
        }
    }

}