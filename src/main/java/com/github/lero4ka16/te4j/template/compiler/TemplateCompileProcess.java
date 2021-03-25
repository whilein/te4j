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

package com.github.lero4ka16.te4j.template.compiler;

import com.github.lero4ka16.te4j.Te4j;
import com.github.lero4ka16.te4j.include.IncludeTarget;
import com.github.lero4ka16.te4j.template.Template;
import com.github.lero4ka16.te4j.template.compiler.code.IterationCode;
import com.github.lero4ka16.te4j.template.compiler.code.RenderCode;
import com.github.lero4ka16.te4j.template.compiler.exp.ExpParsedList;
import com.github.lero4ka16.te4j.template.compiler.exp.ExpParser;
import com.github.lero4ka16.te4j.template.compiler.path.AbstractCompiledPath;
import com.github.lero4ka16.te4j.template.compiler.path.DefaultCompiledPath;
import com.github.lero4ka16.te4j.template.compiler.path.IncludeCompiledPath;
import com.github.lero4ka16.te4j.template.compiler.path.PathAccessor;
import com.github.lero4ka16.te4j.template.compiler.switchcase.SwitchCase;
import com.github.lero4ka16.te4j.template.context.TemplateContext;
import com.github.lero4ka16.te4j.template.environment.Environment;
import com.github.lero4ka16.te4j.template.environment.LoopEnvironment;
import com.github.lero4ka16.te4j.template.environment.PrimaryEnvironment;
import com.github.lero4ka16.te4j.template.exception.TemplateException;
import com.github.lero4ka16.te4j.template.method.TemplateMethodType;
import com.github.lero4ka16.te4j.template.method.impl.ConditionMethod;
import com.github.lero4ka16.te4j.template.method.impl.ForeachMethod;
import com.github.lero4ka16.te4j.template.method.impl.IncludeMethod;
import com.github.lero4ka16.te4j.template.method.impl.SwitchCaseMethod;
import com.github.lero4ka16.te4j.template.method.impl.ValueMethod;
import com.github.lero4ka16.te4j.template.output.TemplateOutput;
import com.github.lero4ka16.te4j.template.output.TemplateOutputBuffer;
import com.github.lero4ka16.te4j.template.output.TemplateOutputStream;
import com.github.lero4ka16.te4j.template.output.TemplateOutputString;
import com.github.lero4ka16.te4j.template.parse.ParsedTemplate;
import com.github.lero4ka16.te4j.template.path.TemplatePath;
import com.github.lero4ka16.te4j.template.path.TemplatePathIterator;
import com.github.lero4ka16.te4j.util.formatter.TextFormatter;
import com.github.lero4ka16.te4j.util.hash.Hash;
import com.github.lero4ka16.te4j.util.type.TypeInfo;
import com.github.lero4ka16.te4j.util.type.ref.ITypeRef;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author lero4ka16
 */
public class TemplateCompileProcess<T> {

    private static final String OUTPUT_STREAM_CLASS
            = OutputStream.class.getName();

    private static final String TEMPLATE_OUTPUT_CLASS
            = TemplateOutput.class.getName();

    private static final String TEMPLATE_OUTPUT_STRING_CLASS
            = TemplateOutputString.class.getName();

    private static final String TEMPLATE_OUTPUT_STREAM_CLASS
            = TemplateOutputStream.class.getName();

    private static final String TEMPLATE_OUTPUT_BUFFER_CLASS
            = TemplateOutputBuffer.class.getName();

    private final ParsedTemplate template;
    private final TemplateContext context;

    private final ITypeRef<T> type;
    private final String parentFile;

    private final TemplateClassCompiler javaCompiler;

    private final Set<String> includes;

    private final PrimaryEnvironment primaryEnvironment;
    private final Map<String, Environment> environments = new HashMap<>();

    private final ExpParser expParser;

    private AtomicInteger nameCounter;

    private int outputType;
    private SwitchCase currentSwitchCase;

    public TemplateCompileProcess(@NotNull TemplateContext context, @NotNull ParsedTemplate template,
                                  @NotNull ITypeRef<T> type, @NotNull String parentFile) {
        this.type = type;
        this.parentFile = parentFile;

        this.primaryEnvironment = new PrimaryEnvironment("object", type.getType(), type.getRawType());

        this.context = context;

        this.template = template;
        this.expParser = new ExpParser(this::compilePathAccessor);

        this.javaCompiler = TemplateClassCompiler.current();
        this.javaCompiler.setTemplateType(type.getCanonicalName());

        this.includes = new HashSet<>();

        for (TemplatePath path : template.getPaths()) {
            if (path.getMethodType() == TemplateMethodType.INCLUDE) {
                IncludeMethod includeMethod = path.getMethod();

                if (includeMethod.getFile().hasValues()) {
                    continue;
                }

                includes.add(parentFile + "/" + includeMethod.getFile().format());
            }
        }
    }

    public Environment setEnvironment(String name, Environment environment) {
        return environments.put(name, environment);
    }

    public void addEnvironment(String name, Environment environment) {
        if (environments.containsKey(name)) {
            throw new TemplateException("Namespace already exists: " + name);
        }

        environments.put(name, environment);
    }

    public void removeEnvironment(String name) {
        environments.remove(name);
    }

    public List<AbstractCompiledPath> compilePaths(List<TemplatePath> paths) {
        return paths.stream().map(this::compilePaths).collect(Collectors.toList());
    }

    public AbstractCompiledPath compilePaths(TemplatePath path) {
        String id = "_" + nameCounter.incrementAndGet();

        TemplateMethodType methodType = path.getMethodType();

        switch (methodType) {
            case INCLUDE:
                return new IncludeCompiledPath(id, path.<IncludeMethod>getMethod().getFile(), path);
            case CASE:
                return new DefaultCompiledPath(id, compilePathAccessor(path.<SwitchCaseMethod>getMethod().getValue()), path);
            case VALUE:
            case CONDITION: {
                String value = path.getMethodType() == TemplateMethodType.VALUE
                        ? path.<ValueMethod>getMethod().getValue()
                        : path.<ConditionMethod>getMethod().getCondition();

                return new DefaultCompiledPath(id, expParser.recompile(value), path);
            }
            default:
                String value = path.<ForeachMethod>getMethod().getPath();

                return new DefaultCompiledPath(id, expParser.recompile(value), path);
        }
    }

    private PathAccessor compilePathAccessor(String path) {
        TemplatePathIterator iterator = new TemplatePathIterator(path);

        if (!iterator.hasNext()) {
            return null;
        }

        String element = iterator.next();

        if (!iterator.hasNext()) {
            switch (element) {
                case "true":
                    return PathAccessor.TRUE;
                case "false":
                    return PathAccessor.FALSE;
                case "null":
                    return PathAccessor.NULL;
            }
        }

        Environment environment = environments.get(element);

        if (environment == null) {
            iterator.previous();
            environment = primaryEnvironment;
        }

        return environment.resolve(iterator);
    }

    public void writePath(AbstractCompiledPath path, RenderCode out) {
        String accessorValue = path.getAccessorValue();

        switch (path.getMethodType()) {
            case VALUE:
                out.appendCodeSegment("out.put(" + accessorValue + ");");
                break;
            case CASE: {
                SwitchCaseMethod SwitchCaseMethod = path.getMethod();
                String value = SwitchCaseMethod.getValue();
                String from = SwitchCaseMethod.getFrom();

                TypeInfo info = path.getReturnType();

                Class<?> type;
                Object[] values;

                if (from == null) {
                    if (!info.isEnum()) {
                        throw new IllegalStateException(value + " is not enum");
                    }

                    type = (Class<?>) info.getType();
                    values = type.getEnumConstants();
                } else {
                    ExpParsedList parsedList = expParser.parseList(from);

                    values = parsedList.getValues();
                    type = parsedList.getType();
                }

                SwitchCase oldSwitchCase = currentSwitchCase;

                SwitchCase switchCase = new SwitchCase(type);
                currentSwitchCase = switchCase;

                out.append("switch (").append(accessorValue).append(") {");

                for (Object o : values) {
                    switchCase.setValue(o);
                    out.append("case ");

                    boolean string = o instanceof String;

                    if (string) out.append("\"");
                    out.append(o.toString());
                    if (string) out.append("\"");

                    out.append(": {");
                    out.appendTemplate(SwitchCaseMethod.getBlock());
                    out.append("break;}");
                }

                currentSwitchCase = oldSwitchCase;

                if (SwitchCaseMethod.getDefaultBlock() != null) {
                    out.append("default: {");
                    out.appendTemplate(SwitchCaseMethod.getDefaultBlock());
                    out.append("}");
                }

                out.append("}");
                break;
            }
            case INCLUDE: {
                IncludeMethod method = path.getMethod();

                String fileName;
                IncludeTarget file = method.getFile();

                if (file.hasValues()) {
                    SwitchCase switchCase = currentSwitchCase;

                    if (switchCase != null) {
                        fileName = file.format(switchCase.getValue());
                    } else {
                        fileName = file.format();
                    }

                    includes.add(parentFile + "/" + fileName);
                } else {
                    fileName = file.format();
                }

                ParsedTemplate template = context.parse(parentFile + "/" + fileName);

                if (template.hasPaths()) {
                    out.appendTemplate(template);
                } else {
                    out.appendTextSegment(template);
                }

                break;
            }
            case CONDITION: {
                ConditionMethod method = path.getMethod();
                ParsedTemplate block = method.getBlock();
                ParsedTemplate elseBlock = method.getElseBlock();

                out.append("if(").append(accessorValue).append("){");
                out.appendTemplate(block);

                if (elseBlock != null) {
                    out.append("} else {");
                    out.appendTemplate(elseBlock);
                }

                out.append("}");
                break;
            }
            case FOR: {
                ForeachMethod method = path.getMethod();

                TypeInfo returnType = path.getReturnType();
                Class<?> listType = returnType.getComponentType();

                if (listType == null) {
                    throw new IllegalStateException("No iterable type found for " + path.getAccessorValue());
                }

                ParsedTemplate template = method.getBlock();
                String as = method.getAs();

                String id = path.getId();
                String counter = "__counter_" + id + "_";

                LoopEnvironment loop = new LoopEnvironment(counter);

                IterationCode iterationCode = new IterationCode(
                        id, listType.getName(), accessorValue, counter, template, loop,
                        returnType.isArrayList(), returnType.isArray(),
                        !List.class.isAssignableFrom(returnType.getRawType())
                );

                Environment prevLoop = setEnvironment("loop", loop);
                addEnvironment(method.getAs(), new PrimaryEnvironment(iterationCode.getElementFieldName(), listType, listType));

                iterationCode.write(out);

                setEnvironment("loop", prevLoop);
                removeEnvironment(as);
                break;
            }
        }
    }

    private final Map<Hash, String> byteValues = new HashMap<>();

    public AtomicInteger getNameCounter() {
        return nameCounter;
    }

    public void addBytes(Integer field, byte[] bytes) {
        String fieldName = getOutputPrefix(outputType) + field;
        String prevFieldName = byteValues.put(Hash.forArray(bytes), fieldName);

        switch (outputType) {
            case Te4j.STRING:
                addContent("private final String " + fieldName + " = "
                        + (prevFieldName != null ? prevFieldName : getString(bytes)) + ";");
                break;
            case Te4j.BYTES:
                addContent("private final byte[] " + fieldName + " = "
                        + (prevFieldName != null ? prevFieldName : getString(bytes) + ".getBytes()") + ";");
                break;
        }
    }

    private String getString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        sb.append('"');

        try {
            new TextFormatter(bytes)
                    .replace(context.getReplace())
                    .write(sb);
        } catch (IOException e) {
            e.printStackTrace();
        }

        sb.append("\"");

        return sb.toString();
    }

    public int getOutputType() {
        return outputType;
    }

    private void addRenderMethod(int outputType) {
        this.outputType = outputType;
        this.nameCounter = new AtomicInteger();

        StringBuilder sb = new StringBuilder();

        sb.append("private void render(").append(type.getCanonicalName()).append(" object, ");
        sb.append(outputType == Te4j.STRING
                ? TEMPLATE_OUTPUT_STRING_CLASS
                : TEMPLATE_OUTPUT_CLASS);
        sb.append(" out) {");

        RenderCode renderCode = new RenderCode(this);
        renderCode.appendTemplate(template);
        renderCode.flushTemplates();

        sb.append(renderCode);
        sb.append('}');

        byteValues.clear();

        addContent(sb.toString());
    }

    public void addRenderAsBytes(boolean hasBytesOptimization) {
        StringBuilder sb = new StringBuilder();
        sb.append("public byte[] renderAsBytes(").append(type.getCanonicalName()).append(" object) {");

        if (hasBytesOptimization) {
            sb.append(TEMPLATE_OUTPUT_BUFFER_CLASS).append(" result = bytesOptimized.get();");
        } else {
            sb.append(TEMPLATE_OUTPUT_STRING_CLASS).append(" result = stringOptimized.get();");
        }

        sb.append("result.reset();");
        sb.append("render(object, result);");
        sb.append("return result.toByteArray();");
        sb.append("}");

        addContent(sb.toString());
    }

    public void addRenderAsString(boolean hasStringOptimization) {
        StringBuilder sb = new StringBuilder();
        sb.append("public String renderAsString(").append(type.getCanonicalName()).append(" object) {");

        if (hasStringOptimization) {
            sb.append(TEMPLATE_OUTPUT_STRING_CLASS).append(" result = stringOptimized.get();");
        } else {
            sb.append(TEMPLATE_OUTPUT_BUFFER_CLASS).append(" result = bytesOptimized.get();");
        }

        sb.append("result.reset();");
        sb.append("render(object, result);");
        sb.append("return result.toString();");
        sb.append("}");

        addContent(sb.toString());
    }

    public void addRenderToStream(boolean hasBytesOptimization) {
        StringBuilder sb = new StringBuilder();

        sb.append("public void renderTo(").append(type.getCanonicalName()).append(" object, ");
        sb.append(OUTPUT_STREAM_CLASS).append(" os) throws java.io.IOException {");

        if (hasBytesOptimization) {
            sb.append("render(object, new ").append(TEMPLATE_OUTPUT_STREAM_CLASS).append("(os));");
        } else {
            sb.append("os.write(renderAsBytes(object));");
        }

        sb.append("}");

        addContent(sb.toString());
    }

    public void addContent(String src) {
        javaCompiler.addContent(src);
    }

    public @NotNull String getOutputPrefix(int bit) {
        switch (bit) {
            case 1:
                return "STRING_";
            case 2:
                return "BYTES_";
            default:
                throw new IllegalArgumentException("Undefined bit: " + bit);
        }
    }

    @SuppressWarnings("unchecked")
    public Template<T> compile() throws Exception {
        addEnvironment("this", primaryEnvironment);

        for (int outputType : Te4j.OUTPUT_TYPES) {
            if ((context.getOutputTypes() & outputType) == outputType) {
                addRenderMethod(outputType);
            }
        }

        addRenderAsString((context.getOutputTypes() & Te4j.STRING) != 0);
        addRenderAsBytes((context.getOutputTypes() & Te4j.BYTES) != 0);
        addRenderToStream((context.getOutputTypes() & Te4j.BYTES) != 0);

        StringBuilder includeBuilder = new StringBuilder();

        for (String include : includes) {
            if (includeBuilder.length() > 0) {
                includeBuilder.append(", ");
            }

            includeBuilder.append('"');
            includeBuilder.append(new TextFormatter(parentFile + "/" + include).format());
            includeBuilder.append('"');
        }

        addContent("private final String[] includes = new String[]{" + includeBuilder + "};");
        addContent("public String[] getIncludes() { return includes; }");

        Class<?> result = javaCompiler.compile();

        Constructor<?> constructor = result.getDeclaredConstructor();
        return (Template<T>) constructor.newInstance();
    }

}
