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

package com.github.lero4ka16.te4j.template.method;

/**
 * <ul>
 *     <li>NONE - метод отсутствует, используется по умолчанию.
 *     Отображает данные по указанному пути</li>
 *     <li>IF - условие, если по указанному пути будет true,
 *     то оно отобразит текст в блоке.</li>
 *     <li>LIST - отображает список, для каждого элемента по указанному пути (может быть массивом или Iterable)
 *     будет отображать блок, отформатированный специально под этот элемент</li>
 *     <li>INCLUDE - добавляет данные из другого файла, специально оформатированные под текущий объект</li>
 * </ul>
 *
 * @author lero4ka16
 */
public enum TemplateMethodType {

    VALUE,
    FOR,
    CONDITION,
    INCLUDE,
    CASE;

    public static TemplateMethodType findType(String name) {
        switch (name) {
            case "if":
                return CONDITION;
            case "for":
                return FOR;
            case "include":
                return INCLUDE;
            case "case":
                return CASE;
            default:
                throw new IllegalArgumentException(name);
        }
    }
}
