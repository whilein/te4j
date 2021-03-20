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
package com.github.lero4ka16.te4j.template.compiler.code

import com.github.lero4ka16.te4j.template.environment.LoopEnvironment
import com.github.lero4ka16.te4j.template.parse.ParsedTemplate

/**
 * @author lero4ka16
 */
class IterationCode(
    private val elementType: String,
    private val namespace: String,
    private val counterFieldName: String,
    private val elementFieldName: String,
    private val from: String,
    private val content: ParsedTemplate,
    private val arrayList: Boolean,
    private val array: Boolean,
    private val castArrayList: Boolean,
    private val loop: LoopEnvironment
) {
    private val arrayFieldName: String
        get() = "__array_$namespace"
    private val countFieldName: String
        get() = "__count_$namespace"

    fun write(out: RenderCode) {
        val arrayOrArrayList = arrayList || array
        val counterPosition = out.position()

        if (arrayOrArrayList) {
            if (arrayList) {
                out.append("java.util.List<").append(elementType).append("> ")
            } else {
                out.append(elementType).append("[] ")
            }

            out.append(arrayFieldName).append("=")

            if (castArrayList) {
                out.append("(java.util.List<").append(elementType).append(">) ")
            }

            out.append(from).append(";")
            out.append("int ").append(countFieldName).append("=").append(arrayFieldName)

            if (arrayList) {
                out.append(".size();")
            } else {
                out.append(".length;")
            }

            out.append("for(int ")
            out.append(counterFieldName).append("=0;")
            out.append(counterFieldName).append("<").append(countFieldName).append(";")
            out.append(counterFieldName).append("++)")
            out.append("{")
            out.append(elementType).append(" ").append(elementFieldName).append("=")
            out.append(arrayFieldName)
            out.append(if (arrayList) ".get(" else "[")
            out.append(counterFieldName)
            out.append(if (arrayList) ");" else "];")
        } else {
            out.append("for(").append(elementType).append(" ").append(elementFieldName).append(":").append(from).append(")")
            out.append("{")
        }

        out.appendTemplate(content)

        if (!arrayOrArrayList && loop.hasCounter) {
            out.setPosition(counterPosition)
            out.append("int ").append(counterFieldName).append("=0;")
            out.resetPosition()
            out.append(counterFieldName).append("++;")
        }

        out.append("}")
    }
}