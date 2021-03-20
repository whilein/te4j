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
package com.github.lero4ka16.te4j.template.method

/**
 *
 *  * NONE - метод отсутствует, используется по умолчанию.
 * Отображает данные по указанному пути
 *  * IF - условие, если по указанному пути будет true,
 * то оно отобразит текст в блоке.
 *  * LIST - отображает список, для каждого элемента по указанному пути (может быть массивом или Iterable)
 * будет отображать блок, отформатированный специально под этот элемент
 *  * INCLUDE - добавляет данные из другого файла, специально оформатированные под текущий объект
 *
 *
 * @author lero4ka16
 */
enum class TemplateMethodType {
    VALUE, FOR, CONDITION, INCLUDE, CASE;

    companion object {
        fun findType(name: String?): TemplateMethodType {
            return when (name) {
                "if" -> CONDITION
                "for" -> FOR
                "include" -> INCLUDE
                "case" -> CASE
                else -> throw IllegalArgumentException(name)
            }
        }
    }
}