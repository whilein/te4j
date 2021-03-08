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

import com.github.lero4ka16.te4j.expression.Expression;
import com.github.lero4ka16.te4j.expression.ExpressionList;
import com.github.lero4ka16.te4j.expression.ExpressionParser;
import com.github.lero4ka16.te4j.expression.ExpressionValue;
import com.github.lero4ka16.te4j.include.IncludeFile;
import com.github.lero4ka16.te4j.template.Template;
import com.github.lero4ka16.te4j.template.compiler.accessor.Accessor;
import com.github.lero4ka16.te4j.template.compiler.accessor.ArrayAccessor;
import com.github.lero4ka16.te4j.template.compiler.accessor.BytesAccessor;
import com.github.lero4ka16.te4j.template.compiler.accessor.MethodAccessor;
import com.github.lero4ka16.te4j.template.compiler.accessor.RawAccessor;
import com.github.lero4ka16.te4j.template.compiler.code.IterationCode;
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
import com.github.lero4ka16.te4j.template.output.TemplateOutputType;
import com.github.lero4ka16.te4j.template.parse.ParsedTemplate;
import com.github.lero4ka16.te4j.template.path.TemplatePath;
import com.github.lero4ka16.te4j.template.path.TemplatePathIterator;
import com.github.lero4ka16.te4j.util.BytesHashKey;
import com.github.lero4ka16.te4j.util.RuntimeJavaCompiler;
import com.github.lero4ka16.te4j.util.StringConcatenation;
import com.github.lero4ka16.te4j.util.text.Text;
import com.github.lero4ka16.te4j.util.type.TypeInfo;
import com.github.lero4ka16.te4j.util.type.ref.TypeRef;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
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
public class TemplateCompileProcess<BoundType> {

    private static final String COMPILED_TEMPLATE_CLASS
            = Template.class.getName();

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
    /**
     * Content of template
     */
    private final byte[] template;

    private final int off;
    private final int len;

    private final TemplateContext context;

    private final TypeRef<BoundType> type;
    private final String parentFile;

    private final RuntimeJavaCompiler javaCompiler;

    private final List<TemplatePath> paths;

    private final Map<String, Accessor> accessors;

    private final Set<String> includes;

    private final PrimaryEnvironment primaryEnvironment;
    private final Map<String, Environment> environments = new HashMap<>();

    private final ExpressionParser expressionParser;

    private AtomicInteger nameCounter;

    private int outputType;
    private SwitchCase currentSwitchCase;

    public TemplateCompileProcess(TemplateContext context, byte[] template, int off, int len,
                                  TypeRef<BoundType> type, String parentFile, List<TemplatePath> paths) {
        this.type = type;
        this.parentFile = parentFile;

        this.primaryEnvironment = new PrimaryEnvironment("object", type.getType(), type.getTypeClass());

        this.context = context;

        this.off = off;
        this.len = len;

        this.template = template;
        this.expressionParser = new ExpressionParser(this::compilePathAccessor);

        this.nameCounter = new AtomicInteger();
        this.accessors = new HashMap<>();

        String simpleName = "GeneratedTemplate";
        this.javaCompiler = new RuntimeJavaCompiler(null, simpleName);

        this.includes = new HashSet<>();

        for (TemplatePath path : paths) {
            if (path.getMethodType() == TemplateMethodType.INCLUDE) {
                IncludeMethod includeMethod = path.getMethod();

                if (includeMethod.getFile().hasValues()) {
                    continue;
                }

                includes.add(parentFile + "/" + includeMethod.getFile().format());
            }
        }

        this.paths = paths;
        this.javaCompiler.setSuperclass(COMPILED_TEMPLATE_CLASS + "<" + type.getCanonicalName() + ">");
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

    public void removeNamespace(String name) {
        environments.remove(name);
    }

    private AbstractCompiledPath compilePaths(TemplatePath path) {
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

                Expression exp = expressionParser.parseExpression(value);
                String result = exp.compile();

                PathAccessor newAccessor;

                if (exp instanceof ExpressionValue) {
                    ExpressionValue expValue = (ExpressionValue) exp;
                    PathAccessor oldAccessor = expValue.getAccessor();

                    if (oldAccessor.getAccessor().equals(result)) {
                        newAccessor = oldAccessor;
                    } else {
                        newAccessor = new PathAccessor(exp.getObjectType(), result);
                    }
                } else {
                    newAccessor = new PathAccessor(exp.getObjectType(), result);
                }

                return new DefaultCompiledPath(id, newAccessor, path);
            }
            default:
                String value = path.<ForeachMethod>getMethod().getPath();

                Expression exp = expressionParser.parseExpression(value);
                String result = exp.compile();

                return new DefaultCompiledPath(id, new PathAccessor(exp.getObjectType(), result), path);
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

    private List<AbstractCompiledPath> compilePaths(List<TemplatePath> paths) {
        return paths.stream().map(this::compilePaths).collect(Collectors.toList());
    }

    private void generateAccessors(List<AbstractCompiledPath> paths) {
        for (AbstractCompiledPath path : paths) {
            Accessor accessor = generateAccessor(path);
            accessors.put(path.getId(), accessor);
        }
    }

    private void writeTemplate(StringBuilder sb, ParsedTemplate template) {
        List<AbstractCompiledPath> compiled = compilePaths(template.getPaths());

        generateAccessors(compiled);

        StringConcatenation concatenation = new StringConcatenation(nameCounter, sb, outputType);
        concat(compiled, template.getRawContent(), template.getOffset(), template.getLength()).insert(concatenation);
        concatenation.generateFields(this);
    }

    private Accessor generateAccessor(AbstractCompiledPath path) {
        String accessorValue = path.getAccessorValue();

        switch (path.getMethodType()) {
            case VALUE:
                return new MethodAccessor(accessorValue);
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
                    Expression exp = expressionParser.parseExpression(from);

                    if (!(exp instanceof ExpressionList)) {
                        throw new UnsupportedOperationException();
                    }

                    ExpressionList expList = (ExpressionList) exp;
                    values = expList.toArray();
                    type = expList.getObjectType().getComponentType();
                }

                SwitchCase oldSwitchCase = currentSwitchCase;

                SwitchCase switchCase = new SwitchCase(type, values);
                currentSwitchCase = switchCase;

                StringBuilder sb = new StringBuilder();
                sb.append("switch (").append(accessorValue).append(") {");

                for (Object o : values) {
                    switchCase.setValue(o);
                    sb.append("case ");

                    boolean string = o instanceof String;

                    if (string) sb.append('"');
                    sb.append(o);
                    if (string) sb.append('"');

                    sb.append(": {");
                    writeTemplate(sb, SwitchCaseMethod.getBlock());
                    sb.append("break;}");
                }

                currentSwitchCase = oldSwitchCase;

                if (SwitchCaseMethod.getDefaultBlock() != null) {
                    sb.append("default: {");
                    writeTemplate(sb, SwitchCaseMethod.getDefaultBlock());
                    sb.append("}");
                }

                sb.append("}");

                return new RawAccessor(sb.toString());
            }
            case INCLUDE: {
                IncludeMethod method = path.getMethod();

                String fileName;
                IncludeFile file = method.getFile();

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
                    List<AbstractCompiledPath> compiled = compilePaths(template.getPaths());
                    generateAccessors(compiled);

                    return concat(compiled, template.getRawContent(), template.getOffset(), template.getLength());
                } else {
                    return new BytesAccessor(template.getRawContent(), template.getOffset(), template.getLength());
                }
            }
            case CONDITION: {
                ConditionMethod method = path.getMethod();
                ParsedTemplate block = method.getBlock();
                ParsedTemplate elseBlock = method.getElseBlock();

                StringBuilder sb = new StringBuilder("if(");
                sb.append(accessorValue).append("){");
                writeTemplate(sb, block);

                if (elseBlock != null) {
                    sb.append("} else {");
                    writeTemplate(sb, elseBlock);
                }

                sb.append('}');
                return new RawAccessor(sb.toString());
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

                IterationCode code = new IterationCode();
                code.setElementType(listType.getName());
                code.setAs(as + path.getId());
                code.setFrom(accessorValue);

                LoopEnvironment loop = new LoopEnvironment(code.getCounterFieldName());

                Environment prevLoop = setEnvironment("loop", loop);
                addEnvironment(as, new PrimaryEnvironment(code.getElementName(), listType, listType));

                StringBuilder sb = new StringBuilder();

                List<AbstractCompiledPath> compiled = compilePaths(template.getPaths());
                generateAccessors(compiled);

                StringConcatenation concatenation = new StringConcatenation(nameCounter, sb, outputType);
                concat(compiled, template.getRawContent(), template.getOffset(), template.getLength()).insert(concatenation);
                concatenation.generateFields(this);

                code.setContent(sb.toString());

                if (returnType.isArray()) {
                    code.setArray(true);
                } else if (returnType.isArrayList()) {
                    code.setArrayList(true);
                }

                if (loop.hasIndex()) {
                    code.setInsertCounter(true);
                }

                setEnvironment("loop", prevLoop);
                removeNamespace(as);

                return new RawAccessor(code.toString());
            }
        }

        throw new IllegalStateException();
    }

    private final Map<BytesHashKey, String> byteValues = new HashMap<>();

    public void addBytes(Integer field, byte[] bytes, int outputType) {
        String fieldName = TemplateOutputType.getPrefix(outputType) + field;

        BytesHashKey key = new BytesHashKey(bytes);
        String prevField = byteValues.put(key, fieldName);

        switch (outputType) {
            case TemplateOutputType.STRING:
                addContent("private final String " + fieldName + " = "
                        + (prevField != null ? prevField : getString(bytes)) + ";");
                break;
            case TemplateOutputType.BYTES:
                addContent("private final byte[] " + fieldName + " = "
                        + (prevField != null ? prevField : getString(bytes) + ".getBytes()") + ";");
                break;
        }
    }

    private String getString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        sb.append('"');

        try {
            Text.of(bytes)
                    .replaceStrategy(context.getReplaceStrategy())
                    .compute(sb);
        } catch (IOException e) {
            e.printStackTrace();
        }

        sb.append("\"");

        return sb.toString();
    }

    private Accessor concat(List<AbstractCompiledPath> paths, byte[] value, int off, int len) {
        List<Accessor> result = new ArrayList<>();

        int startIndex = off;

        for (AbstractCompiledPath path : paths) {
            result.add(new BytesAccessor(value, startIndex, path.getOffset() - startIndex));
            result.add(accessors.get(path.getId()));

            startIndex = path.getOffset() + path.getLength();
        }

        result.add(new BytesAccessor(value, startIndex, len + off - startIndex));
        return new ArrayAccessor(result.toArray(new Accessor[0]));
    }

    private void addRenderMethod(int outputType) {
        this.outputType = outputType;
        this.nameCounter = new AtomicInteger();

        StringBuilder sb = new StringBuilder();

        sb.append("public void render(").append(type.getCanonicalName()).append(" object, ");
        sb.append(outputType == TemplateOutputType.STRING
                ? TEMPLATE_OUTPUT_STRING_CLASS
                : TEMPLATE_OUTPUT_CLASS);
        sb.append(" out) {");

        List<AbstractCompiledPath> compiled = compilePaths(paths);
        generateAccessors(compiled);
        StringConcatenation concatenation = new StringConcatenation(nameCounter, sb, outputType);
        concat(compiled, template, off, len).insert(concatenation);
        concatenation.generateFields(this);
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

        sb.append("public void render(").append(type.getCanonicalName()).append(" object, ");
        sb.append(OUTPUT_STREAM_CLASS).append(" out) throws java.io.IOException {");

        if (hasBytesOptimization) {
            sb.append("render(object, new ").append(TEMPLATE_OUTPUT_STREAM_CLASS).append("(out));");
        } else {
            sb.append("out.write(renderAsBytes(object));");
        }

        sb.append("}");

        addContent(sb.toString());
    }

    public void addContent(String src) {
        javaCompiler.addContent(src);
    }

    @SuppressWarnings("rawtypes")
    public Template compile() throws Exception {
        addEnvironment("this", primaryEnvironment);

        for (int value : TemplateOutputType.VALUES) {
            if ((context.getOutputTypes() & value) == value) {
                addRenderMethod(value);
            }
        }

        addRenderAsString((context.getOutputTypes() & TemplateOutputType.STRING) != 0);
        addRenderAsBytes((context.getOutputTypes() & TemplateOutputType.BYTES) != 0);
        addRenderToStream((context.getOutputTypes() & TemplateOutputType.BYTES) != 0);

        StringBuilder includeBuilder = new StringBuilder();

        for (String include : includes) {
            if (includeBuilder.length() > 0) {
                includeBuilder.append(", ");
            }

            includeBuilder
                    .append('"')
                    .append(Text.of(parentFile + "/" + include).computeAsString())
                    .append('"');
        }

        addContent("private final String[] includes = new String[]{" + includeBuilder + "};");
        addContent("public String[] getIncludes() { return includes; }");

        Class<?> result = javaCompiler.compile();

        Constructor<?> constructor = result.getDeclaredConstructor();
        return (Template) constructor.newInstance();
    }

}
