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

import com.github.lero4ka16.te4j.Te4j
import com.github.lero4ka16.te4j.expression.ExpList
import com.github.lero4ka16.te4j.expression.ExpParser
import com.github.lero4ka16.te4j.expression.ExpValue
import com.github.lero4ka16.te4j.include.IncludeTarget
import com.github.lero4ka16.te4j.template.Template
import com.github.lero4ka16.te4j.template.compiler.TemplateClassCompiler.Companion.current
import com.github.lero4ka16.te4j.template.compiler.code.IterationCode
import com.github.lero4ka16.te4j.template.compiler.code.RenderCode
import com.github.lero4ka16.te4j.template.compiler.path.AbstractCompiledPath
import com.github.lero4ka16.te4j.template.compiler.path.DefaultCompiledPath
import com.github.lero4ka16.te4j.template.compiler.path.IncludeCompiledPath
import com.github.lero4ka16.te4j.template.compiler.path.PathAccessor
import com.github.lero4ka16.te4j.template.compiler.switchcase.SwitchStatement
import com.github.lero4ka16.te4j.template.context.TemplateContext
import com.github.lero4ka16.te4j.template.environment.Environment
import com.github.lero4ka16.te4j.template.environment.LoopEnvironment
import com.github.lero4ka16.te4j.template.environment.PrimaryEnvironment
import com.github.lero4ka16.te4j.template.exception.TemplateException
import com.github.lero4ka16.te4j.template.method.TemplateMethodType
import com.github.lero4ka16.te4j.template.method.impl.Condition
import com.github.lero4ka16.te4j.template.method.impl.Foreach
import com.github.lero4ka16.te4j.template.method.impl.Include
import com.github.lero4ka16.te4j.template.method.impl.SwitchCase
import com.github.lero4ka16.te4j.template.method.impl.Value
import com.github.lero4ka16.te4j.template.output.TemplateOutput
import com.github.lero4ka16.te4j.template.output.TemplateOutputBuffer
import com.github.lero4ka16.te4j.template.output.TemplateOutputStream
import com.github.lero4ka16.te4j.template.output.TemplateOutputString
import com.github.lero4ka16.te4j.template.parse.ParsedTemplate
import com.github.lero4ka16.te4j.template.path.TemplatePath
import com.github.lero4ka16.te4j.template.path.TemplatePathIterator
import com.github.lero4ka16.te4j.util.formatter.TextFormatter
import com.github.lero4ka16.te4j.util.type.ref.ITypeRef
import java.io.OutputStream
import java.util.ArrayList
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Collectors
import kotlin.collections.set

/**
 * @author lero4ka16
 */
class TemplateCompileProcess<BoundType>(
    val context: TemplateContext,
    private val template: ParsedTemplate,
    private val type: ITypeRef<BoundType>,
    private val parentFile: String
) {
    private val javaCompiler: TemplateClassCompiler
    private val includes: MutableSet<String>
    private val primaryEnvironment: PrimaryEnvironment = PrimaryEnvironment("object", type.type, type.rawType)
    private val environments: MutableMap<String, Environment?> = HashMap()
    private val expParser: ExpParser

    var nameCounter: AtomicInteger = AtomicInteger()

    var outputType = 0
        private set

    private var currentSwitchStatement: SwitchStatement? = null

    @Suppress("SameParameterValue")
    private fun setEnvironment(name: String, environment: Environment): Environment? {
        return environments.put(name, environment)
    }

    private fun addEnvironment(name: String, environment: Environment) {
        if (environments.containsKey(name)) {
            throw TemplateException("Namespace already exists: $name")
        }

        environments[name] = environment
    }

    private fun removeEnvironment(name: String) {
        environments.remove(name)
    }

    fun compilePaths(paths: List<TemplatePath>): List<AbstractCompiledPath> {
        return paths.stream().map { path: TemplatePath ->
            this.compilePaths(
                path
            )
        }.collect(Collectors.toList())
    }

    private fun compilePaths(path: TemplatePath): AbstractCompiledPath {
        val id = "_" + nameCounter.incrementAndGet()

        when (path.methodType) {
            TemplateMethodType.INCLUDE -> return IncludeCompiledPath(
                id,
                path,
                path.getMethod<Include>().file
            )
            TemplateMethodType.CASE -> return DefaultCompiledPath(
                id, path,
                compilePathAccessor(path.getMethod<SwitchCase>().value)!!
            )
            TemplateMethodType.VALUE, TemplateMethodType.CONDITION -> {
                val value =
                    if (path.methodType === TemplateMethodType.VALUE)
                        path.getMethod<Value>().value
                    else
                        path.getMethod<Condition>().condition
                val exp = expParser.parseExpression(value)
                val result = exp.compile()

                val newAccessor: PathAccessor = if (exp is ExpValue) {
                    val oldAccessor = exp.accessor
                    if (oldAccessor.accessor == result) {
                        oldAccessor
                    } else {
                        PathAccessor(exp.objectType, result)
                    }
                } else {
                    PathAccessor(exp.objectType, result)
                }

                return DefaultCompiledPath(id, path, newAccessor)
            }
            else -> {
                val value = path.getMethod<Foreach>().path
                val exp = expParser.parseExpression(value)
                val result = exp.compile()
                return DefaultCompiledPath(id, path, PathAccessor(exp.objectType, result))
            }
        }
    }

    private fun compilePathAccessor(path: String): PathAccessor? {
        val iterator = TemplatePathIterator(path)
        if (!iterator.hasNext()) {
            return null
        }
        val element = iterator.next()
        if (!iterator.hasNext()) {
            when (element) {
                "true" -> return PathAccessor.TRUE
                "false" -> return PathAccessor.FALSE
                "null" -> return PathAccessor.NULL
            }
        }
        var environment = environments[element]
        if (environment == null) {
            iterator.previous()
            environment = primaryEnvironment
        }
        return environment.resolve(iterator)
    }

    fun writePath(path: AbstractCompiledPath, out: RenderCode) {
        when (path.methodType) {
            TemplateMethodType.VALUE -> out.appendCodeSegment("out.put(" + path.accessorValue + ");")
            TemplateMethodType.CASE -> {
                val method: SwitchCase = path.getMethod()
                val value = method.value
                val from = method.from

                val info = path.returnType

                val type: Class<*>
                val values: Array<*>

                if (from == null) {
                    if (!info.isEnum) {
                        throw IllegalStateException("$value is not enum")
                    }

                    type = info.type as Class<*>
                    values = type.enumConstants
                } else {
                    val exp = expParser.parseExpression(from)

                    if (exp !is ExpList) {
                        throw UnsupportedOperationException()
                    }

                    values = exp.toArray()
                    type = exp.objectType.componentType!!
                }

                val oldSwitchStatement = currentSwitchStatement
                val switchStatement = SwitchStatement(type)

                currentSwitchStatement = switchStatement
                out.append("switch (").append(path.accessorValue).append(") {")
                for (o: Any? in values) {
                    switchStatement.value = o
                    out.append("case ")
                    val string = o is String
                    if (string) out.append("\"")
                    out.append(o.toString())
                    if (string) out.append("\"")
                    out.append(": {")
                    out.appendTemplate(method.block)
                    out.append("break;}")
                }
                currentSwitchStatement = oldSwitchStatement

                if (method.defaultBlock != null) {
                    out.append("default: {")
                    out.appendTemplate(method.defaultBlock)
                    out.append("}")
                }

                out.append("}")
            }
            TemplateMethodType.INCLUDE -> {
                val method: Include = path.getMethod()
                val fileName: String
                val file: IncludeTarget = method.file
                if (file.hasValues()) {
                    val switchStatement = currentSwitchStatement

                    fileName = if (switchStatement != null) {
                        file.format(switchStatement.value!!)
                    } else {
                        file.format()
                    }

                    includes.add("$parentFile/$fileName")
                } else {
                    fileName = file.format()
                }

                val template = context.parse("$parentFile/$fileName")

                if (template.hasPaths) {
                    out.appendTemplate(template)
                } else {
                    out.appendTextSegment(template)
                }
            }
            TemplateMethodType.CONDITION -> {
                val method = path.getMethod<Condition>()
                val block = method.block
                val elseBlock = method.elseBlock
                out.append("if(").append(path.accessorValue).append("){")
                out.appendTemplate(block)
                if (elseBlock != null) {
                    out.append("} else {")
                    out.appendTemplate(elseBlock)
                }
                out.append("}")
            }
            TemplateMethodType.FOR -> {
                val method = path.getMethod<Foreach>()

                val returnType = path.returnType

                val listType = returnType.componentType
                    ?: throw IllegalStateException("No iterable type found for " + path.accessorValue)

                val template = method.block

                val counterFieldName = "__counter_" + path.id
                val elementFieldName = "__element_" + path.id

                val loop = LoopEnvironment(counterFieldName)
                val prevLoop = setEnvironment("loop", loop)

                addEnvironment(method.element, PrimaryEnvironment(elementFieldName, listType, listType))

                val iterationCode = IterationCode(
                    listType.name, path.id, counterFieldName, elementFieldName,
                    path.accessorValue, template, returnType.isArrayList, returnType.isArray,
                    ArrayList::class.java.isAssignableFrom(returnType.rawType), loop
                )

                iterationCode.write(out)

                if (prevLoop != null) {
                    setEnvironment("loop", prevLoop)
                }

                removeEnvironment(method.element)
            }
        }
    }

    private val byteValues: MutableMap<String, String> = HashMap()

    fun getOutputPrefix(bit: Int): String {
        return when (bit) {
            Te4j.STRING -> "STRING_"
            Te4j.BYTES -> "BYTES_"
            else -> throw IllegalStateException("Unknown bit: $bit")
        }
    }

    fun addField(field: Int, value: String) {
        val fieldName = getOutputPrefix(outputType) + field
        val prevFieldName = byteValues.put(value, fieldName)

        when (outputType) {
            Te4j.STRING -> addContent("private final String $fieldName = ${prevFieldName ?: "\"$value\""};")
            Te4j.BYTES -> addContent("private final byte[] $fieldName = ${prevFieldName ?: "\"$value\".getBytes()"};")
        }
    }

    private fun addRenderMethod(outputType: Int) {
        this.nameCounter.set(0)
        this.outputType = outputType

        val sb = StringBuilder()
        sb.append("private void render(").append(type.canonicalName).append(" object, ")
        sb.append(if (outputType == Te4j.STRING) TEMPLATE_OUTPUT_STRING_CLASS else TEMPLATE_OUTPUT_CLASS)
        sb.append(" out) {")
        val renderCode = RenderCode(this)
        renderCode.appendTemplate(template)
        renderCode.flushTemplates()
        sb.append(renderCode)
        sb.append('}')
        byteValues.clear()
        addContent(sb.toString())
    }

    private fun addRenderAsBytes(hasBytesOptimization: Boolean) {
        val sb = StringBuilder()
        sb.append("public byte[] renderAsBytes(").append(type.canonicalName).append(" object) {")
        if (hasBytesOptimization) {
            sb.append(TEMPLATE_OUTPUT_BUFFER_CLASS).append(" result = bytesOptimized.get();")
        } else {
            sb.append(TEMPLATE_OUTPUT_STRING_CLASS).append(" result = stringOptimized.get();")
        }
        sb.append("result.reset();")
        sb.append("render(object, result);")
        sb.append("return result.toByteArray();")
        sb.append("}")
        addContent(sb.toString())
    }

    private fun addRenderAsString(hasStringOptimization: Boolean) {
        val sb = StringBuilder()
        sb.append("public String renderAsString(").append(type.canonicalName).append(" object) {")
        if (hasStringOptimization) {
            sb.append(TEMPLATE_OUTPUT_STRING_CLASS).append(" result = stringOptimized.get();")
        } else {
            sb.append(TEMPLATE_OUTPUT_BUFFER_CLASS).append(" result = bytesOptimized.get();")
        }
        sb.append("result.reset();")
        sb.append("render(object, result);")
        sb.append("return result.toString();")
        sb.append("}")
        addContent(sb.toString())
    }

    private fun addRenderToStream(hasBytesOptimization: Boolean) {
        val sb = StringBuilder()
        sb.append("public void renderTo(").append(type.canonicalName).append(" object, ")
        sb.append(OUTPUT_STREAM_CLASS).append(" os) throws java.io.IOException {")
        if (hasBytesOptimization) {
            sb.append("render(object, new ").append(TEMPLATE_OUTPUT_STREAM_CLASS).append("(os));")
        } else {
            sb.append("os.write(renderAsBytes(object));")
        }
        sb.append("}")
        addContent(sb.toString())
    }

    private fun addContent(src: String?) {
        javaCompiler += src
    }

    @Throws(Exception::class)
    fun compile(): Template<BoundType> {
        addEnvironment("this", primaryEnvironment)

        for (outputType: Int in Te4j.OUTPUT_TYPES) {
            if ((context.outputTypes and outputType) == outputType) {
                addRenderMethod(outputType)
            }
        }

        addRenderAsString((context.outputTypes and Te4j.STRING) != 0)
        addRenderAsBytes((context.outputTypes and Te4j.BYTES) != 0)
        addRenderToStream((context.outputTypes and Te4j.BYTES) != 0)

        val includeBuilder = StringBuilder()

        for (include: String in includes) {
            if (includeBuilder.isNotEmpty()) {
                includeBuilder.append(", ")
            }

            includeBuilder.append('"')
            includeBuilder.append(TextFormatter("$parentFile/$include").format())
            includeBuilder.append('"')
        }

        addContent("private final String[] includes = new String[]{$includeBuilder};")
        addContent("public String[] getIncludes() { return includes; }")

        val result = javaCompiler.compile()
        val constructor = result.getDeclaredConstructor()

        @Suppress("UNCHECKED_CAST")
        return constructor.newInstance() as Template<BoundType>
    }

    companion object {
        private val OUTPUT_STREAM_CLASS = OutputStream::class.java.name
        private val TEMPLATE_OUTPUT_CLASS = TemplateOutput::class.java.name
        private val TEMPLATE_OUTPUT_STRING_CLASS = TemplateOutputString::class.java.name
        private val TEMPLATE_OUTPUT_STREAM_CLASS = TemplateOutputStream::class.java.name
        private val TEMPLATE_OUTPUT_BUFFER_CLASS = TemplateOutputBuffer::class.java.name
    }

    init {
        expParser = ExpParser { path: String ->
            compilePathAccessor(
                path
            )
        }
        javaCompiler = current()
        javaCompiler.templateType = type.canonicalName

        includes = HashSet()
        for (path: TemplatePath in template.paths) {
            if (path.methodType === TemplateMethodType.INCLUDE) {
                val include: Include = path.getMethod()

                if (include.file.hasValues()) {
                    continue
                }

                includes.add(parentFile + "/" + include.file.format())
            }
        }
    }
}