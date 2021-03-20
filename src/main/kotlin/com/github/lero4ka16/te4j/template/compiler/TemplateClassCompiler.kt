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
package com.github.lero4ka16.te4j.template.compiler

import com.github.lero4ka16.te4j.template.Template
import com.github.lero4ka16.te4j.util.Utils.Companion.deleteDirectory
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.net.URLClassLoader
import javax.tools.ToolProvider

/**
 * @author lero4ka16
 */
internal class TemplateClassCompiler private constructor() {
    var templateType: String? = null

    private val content = StringBuilder()

    private val out: File = File("tmp-" + Integer.toHexString(Thread.currentThread().hashCode()))

    private fun clearContent() {
        content.setLength(0)
    }

    operator fun plusAssign(content: String?) {
        this.content.append(content)
    }

    @Throws(IOException::class)
    fun compile(): Class<*> {
        return try {
            if (!out.mkdirs()) throw RuntimeException("Cannot create directory: " + out.name)

            val tmp = File(out, "GeneratedTemplate.java")

            FileWriter(tmp).use { writer ->
                writer.append("public final class GeneratedTemplate")
                writer.append(" extends ")
                writer.append(TEMPLATE_CLASS)
                writer.append('<')
                writer.append(templateType)
                writer.append('>')
                writer.append('{')
                writer.append(content.toString())
                writer.append('}')
                writer.flush()
            }

            val compiler = ToolProvider.getSystemJavaCompiler()
            val result = compiler.run(null, null, null, tmp.absolutePath)

            if (result != 0) {
                throw RuntimeException("Cannot compile class: $result")
            }

            val classLoader = URLClassLoader(arrayOf(out.toURI().toURL()))

            val cls: Class<*> = try {
                classLoader.loadClass("GeneratedTemplate")
            } catch (e: ClassNotFoundException) {
                throw RuntimeException(e)
            }

            val files = out.listFiles()!!

            for (file in files) {
                val fileName = file.name

                // load only synthetic file
                if (fileName.indexOf('$') == -1) continue
                val syntheticName = fileName.substring(0, fileName.length - 6) // .class
                try {
                    classLoader.loadClass(syntheticName)
                } catch (e: ClassNotFoundException) {
                    throw RuntimeException(e)
                }
            }

            cls
        } finally {
            deleteDirectory(out)
            clearContent()
        }
    }

    companion object {
        private val TEMPLATE_CLASS = Template::class.java.name
        private val THREAD_LOCAL = ThreadLocal.withInitial { TemplateClassCompiler() }

        @JvmStatic
        fun current(): TemplateClassCompiler {
            return THREAD_LOCAL.get()
        }
    }

}