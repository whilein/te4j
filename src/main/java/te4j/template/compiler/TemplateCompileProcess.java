/*
 *    Copyright 2021 Whilein
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

package te4j.template.compiler;

import lombok.NonNull;
import te4j.filter.Filters;
import te4j.include.IncludePath;
import te4j.template.Template;
import te4j.template.compiler.code.IterationCode;
import te4j.template.compiler.code.RenderCode;
import te4j.template.compiler.exp.ExpList;
import te4j.template.compiler.exp.ExpParser;
import te4j.template.compiler.path.AbstractCompiledPath;
import te4j.template.compiler.path.DefaultCompiledPath;
import te4j.template.compiler.path.IncludeCompiledPath;
import te4j.template.compiler.path.PathAccessor;
import te4j.template.compiler.switchcase.SwitchCase;
import te4j.template.context.parser.TemplateParser;
import te4j.template.environment.Environment;
import te4j.template.environment.LoopEnvironment;
import te4j.template.environment.PrimaryEnvironment;
import te4j.template.exception.TemplateException;
import te4j.template.method.TemplateMethodType;
import te4j.template.method.impl.*;
import te4j.template.option.minify.Minify;
import te4j.template.option.output.Output;
import te4j.template.output.TemplateOutput;
import te4j.template.output.TemplateOutputStream;
import te4j.template.parser.ParsedTemplate;
import te4j.template.path.DefaultTemplatePathIterator;
import te4j.template.path.TemplatePath;
import te4j.template.path.TemplatePathIterator;
import te4j.util.compiler.JavaRuntimeCompiler;
import te4j.util.compiler.RuntimeCompiler;
import te4j.util.formatter.TextFormatter;
import te4j.util.hash.ByteArrayHash;
import te4j.util.hash.Hash;
import te4j.util.type.TypeInfo;
import te4j.util.type.ref.TypeReference;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author whilein
 */
public final class TemplateCompileProcess<T> {

    private static final String OUTPUT_STREAM_CLASS
            = OutputStream.class.getName();

    private static final String TEMPLATE_OUTPUT_CLASS
            = TemplateOutput.class.getName();

    private static final String TEMPLATE_OUTPUT_STREAM_CLASS
            = TemplateOutputStream.class.getName();

    private static final String TEMPLATE_CLASS
            = Template.class.getName();

    private final ParsedTemplate template;

    private final Set<Output> outputTypes;
    private final Set<Minify> minifyOptions;

    private final TemplateParser parser;
    private final TypeReference<T> type;

    private final String parentFile;

    private final RuntimeCompiler compiler;

    private final Set<String> includes;

    private final PrimaryEnvironment primaryEnvironment;
    private final Map<String, Environment> environments = new HashMap<>();

    private final ExpParser expParser;

    private AtomicInteger nameCounter;

    private Output outputType;
    private SwitchCase currentSwitchCase;

    private String putUserVariable;
    private String putTemplateContent;

    public TemplateCompileProcess(
            @NonNull Filters filters,
            @NonNull TypeReference<T> type,
            @NonNull TemplateParser parser,
            @NonNull Set<Output> outputTypes,
            @NonNull Set<Minify> minifyOptions,
            @NonNull ParsedTemplate template,
            @NonNull String parentFile
    ) {
        this.parser = parser;
        this.outputTypes = outputTypes;
        this.minifyOptions = minifyOptions;

        this.type = type;
        this.parentFile = parentFile;

        this.primaryEnvironment = new PrimaryEnvironment("object", type.getRawType(), type.getType());

        this.template = template;
        this.expParser = new ExpParser(filters, this::compilePathAccessor);

        this.compiler = JavaRuntimeCompiler.create("GeneratedTemplate", new StringBuilder());
        this.compiler.setInterfaces(Collections.singletonList(TEMPLATE_CLASS + "<" + type.getCanonicalName() + ">"));

        this.includes = new HashSet<>();
    }

    public String getPutTemplateContent() {
        return putTemplateContent;
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
            case VALUE: {
                return new DefaultCompiledPath(id,
                        expParser.recompile(path.<ValueMethod>getMethod().getValue()), path);
            }
            case CONDITION: {
                return new DefaultCompiledPath(id,
                        expParser.recompile(path.<ConditionMethod>getMethod().getCondition()), path);
            }
            default:
                String value = path.<ForeachMethod>getMethod().getPath();

                return new DefaultCompiledPath(id, expParser.recompile(value), path);
        }
    }

    private PathAccessor compilePathAccessor(String path) {
        TemplatePathIterator iterator = DefaultTemplatePathIterator.create(path);

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
            iterator.prev();
            environment = primaryEnvironment;
        }

        return environment.resolve(iterator);
    }

    public String formatFile(String file) {
        return parentFile.equals(".") ? file : parentFile + File.separatorChar + file;
    }

    public void writePath(AbstractCompiledPath path, RenderCode out) {
        switch (path.getMethodType()) {
            case VALUE: {
                out.appendCodeSegment("out." + putUserVariable + "(" + path.getAccessorValue() + ");");
                break;
            }
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
                    ExpList parsedList = expParser.parseList(from);

                    values = parsedList.getValues();
                    type = parsedList.getElementType();
                }

                SwitchCase oldSwitchCase = currentSwitchCase;

                SwitchCase switchCase = new SwitchCase(type);
                currentSwitchCase = switchCase;

                out.append("switch (").append(path.getAccessorValue()).append(") {");

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
                IncludePath file = method.getFile();

                if (file.hasExpressions()) {
                    SwitchCase switchCase = currentSwitchCase;

                    if (switchCase != null) {
                        fileName = file.format(switchCase.getValue());
                    } else {
                        fileName = file.format();
                    }

                } else {
                    fileName = file.format();
                }

                fileName = formatFile(fileName);
                includes.add(fileName);

                ParsedTemplate template = parser.from(fileName);

                if (template.hasPaths()) {
                    out.appendTemplate(template);
                } else {
                    out.appendTextSegment(template);
                }

                break;
            }
            case CONDITION: {
                appendCondition(path.getMethod(), path.getAccessorValue(), out);
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
                String length = "__length_" + id + "_";

                LoopEnvironment loop = new LoopEnvironment(counter, length);

                IterationCode iterationCode = new IterationCode(
                        id, listType.getName(), path.getAccessorValue(), counter, length,
                        template, loop, returnType.isArrayList(), returnType.isArray(),
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

    private void appendCondition(ConditionMethod method, String value, RenderCode out) {
        ParsedTemplate block = method.getBlock();
        ConditionMethod elseIf = (ConditionMethod) method.getElseIf();
        ParsedTemplate elseBlock = method.getElseBlock();

        out.append("if(").append(value).append("){");
        out.appendTemplate(block);

        if (elseIf != null) {
            out.append("} else ");
            appendCondition(elseIf, expParser.recompile(elseIf.getCondition()).getAccessor(), out);
            return;
        }

        if (elseBlock != null) {
            out.append("} else {");
            out.appendTemplate(elseBlock);
        }

        out.append("}");
    }

    private final Map<Hash, String> byteValues = new HashMap<>();

    public AtomicInteger getNameCounter() {
        return nameCounter;
    }

    public void addBytes(Integer field, byte[] bytes) {
        String fieldName = outputType.getPrefix() + field;
        String prevFieldName = byteValues.put(ByteArrayHash.create(bytes), fieldName);

        switch (outputType) {
            case STRING:
                addContent("private final String " + fieldName + " = "
                        + (prevFieldName != null ? prevFieldName : getString(bytes)) + ";");
                break;
            case BYTES:
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
                    .minify(minifyOptions)
                    .write(sb);
        } catch (IOException e) {
            e.printStackTrace();
        }

        sb.append("\"");

        return sb.toString();
    }

    public Output getOutputType() {
        return outputType;
    }

    private void addRenderMethod(@NonNull Output outputType) {
        this.outputType = outputType;
        this.nameCounter = new AtomicInteger();

        StringBuilder sb = new StringBuilder();

        sb.append("private void render(").append(type.getCanonicalName()).append(" object, ");

        switch (outputType) {
            case STRING:
                sb.append("StringBuilder");

                putUserVariable = "append";
                putTemplateContent = "append";
                break;
            case BYTES:
                sb.append(TEMPLATE_OUTPUT_CLASS);

                putUserVariable = "put";
                putTemplateContent = "write";
                break;
        }

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
            sb.append(TEMPLATE_OUTPUT_CLASS).append(" result = bytesOptimized.get();");
            sb.append("result.reset();");
        } else {
            sb.append("StringBuilder result = stringOptimized.get();");
            sb.append("result.setLength(0);");
        }

        sb.append("render(object, result);");

        if (hasBytesOptimization) {
            sb.append("return result.toByteArray();");
        } else {
            sb.append("return result.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);");
        }

        sb.append("}");

        addContent(sb.toString());
    }

    public void addRenderAsString(boolean hasStringOptimization) {
        StringBuilder sb = new StringBuilder();
        sb.append("public String renderAsString(").append(type.getCanonicalName()).append(" object) {");

        if (hasStringOptimization) {
            sb.append("StringBuilder result = stringOptimized.get();");
            sb.append("result.setLength(0);");
        } else {
            sb.append(TEMPLATE_OUTPUT_CLASS).append(" result = bytesOptimized.get();");
            sb.append("result.reset();");
        }

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
            sb.append("render(object, ").append(TEMPLATE_OUTPUT_STREAM_CLASS).append(".create(os));");
        } else {
            sb.append("os.write(renderAsBytes(object));");
        }

        sb.append("}");

        addContent(sb.toString());
    }

    public void addContent(String src) {
        compiler.getContent().append(src);
    }

    @SuppressWarnings("unchecked")
    public Template<T> compile() throws Exception {
        addEnvironment("this", primaryEnvironment);

        for (Output output : outputTypes) {
            addRenderMethod(output);
        }

        addRenderAsString(outputTypes.contains(Output.STRING));
        addRenderAsBytes(outputTypes.contains(Output.BYTES));
        addRenderToStream(outputTypes.contains(Output.BYTES));

        StringBuilder includeBuilder = new StringBuilder();

        for (String include : includes) {
            if (includeBuilder.length() > 0) {
                includeBuilder.append(", ");
            }

            includeBuilder.append('"');
            includeBuilder.append(new TextFormatter(include).format());
            includeBuilder.append('"');
        }

        addContent("private final String[] includes = new String[]{" + includeBuilder + "};");
        addContent("public String[] getIncludes() { return includes; }");

        Class<?> result = compiler.compile();

        Constructor<?> constructor = result.getDeclaredConstructor();
        return (Template<T>) constructor.newInstance();
    }

}
