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
package com.github.lero4ka16.te4j.template.reader.token

/**
 * @author lero4ka16
 */
enum class TemplateTokenType {
    BEGIN, END_FOR, ELSE, END_IF, END_CASE, CASE_DEFAULT;

    companion object {
        fun getType(path: String?): TemplateTokenType {
            return when (path) {
                "endfor" -> END_FOR
                "default" -> CASE_DEFAULT
                "endcase" -> END_CASE
                "else" -> ELSE
                "endif" -> END_IF
                else -> BEGIN
            }
        }
    }
}