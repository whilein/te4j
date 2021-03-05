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

package com.github.lero4ka16.te4j.template.compiled;

import com.github.lero4ka16.te4j.template.ParsedTemplate;
import com.github.lero4ka16.te4j.template.compiled.accessor.*;
import com.github.lero4ka16.te4j.template.compiled.feature.Namespace;
import com.github.lero4ka16.te4j.template.compiled.feature.SwitchCase;
import com.github.lero4ka16.te4j.template.compiled.path.AbstractCompiledPath;
import com.github.lero4ka16.te4j.template.compiled.path.DefaultCompiledPath;
import com.github.lero4ka16.te4j.template.compiled.path.IncludeCompiledPath;
import com.github.lero4ka16.te4j.template.compiled.path.PathAccessor;
import com.github.lero4ka16.te4j.template.exception.TemplateException;
import com.github.lero4ka16.te4j.template.include.Include;
import com.github.lero4ka16.te4j.template.method.TemplateMethodType;
import com.github.lero4ka16.te4j.template.method.impl.*;
import com.github.lero4ka16.te4j.template.output.TemplateOutput;
import com.github.lero4ka16.te4j.template.path.TemplatePath;
import com.github.lero4ka16.te4j.template.path.TemplatePathIterator;
import com.github.lero4ka16.te4j.template.provider.TemplateProvider;
import com.github.lero4ka16.te4j.util.BytesHashKey;
import com.github.lero4ka16.te4j.util.RuntimeJavaCompiler;
import com.github.lero4ka16.te4j.util.StringConcatenation;
import com.github.lero4ka16.te4j.util.expression.Exp;
import com.github.lero4ka16.te4j.util.expression.ExpList;
import com.github.lero4ka16.te4j.util.expression.ExpParser;
import com.github.lero4ka16.te4j.util.expression.ExpValue;
import com.github.lero4ka16.te4j.util.text.Text;
import com.github.lero4ka16.te4j.util.type.info.GenericInfo;
import com.github.lero4ka16.te4j.util.type.info.TypeInfo;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.github.lero4ka16.te4j.util.Utils.getMethod;
import static com.github.lero4ka16.te4j.util.Utils.toCamelCase;

public class TemplateCompileProcess<BoundType> {

    private static final String COMPILED_TEMPLATE_CLASS
            = Template.class.getName();

    private static final String PATH_CLASS
            = Path.class.getName();

    private static final String PATHS_CLASS
            = Paths.class.getName();

    private static final String TEMPLATE_OUTPUT_CLASS
            = TemplateOutput.class.getName();

    /**
     * Content of template
     */
    private final byte[] template;

    private final int off;
    private final int len;

    private final TemplateProvider provider;
    private final AtomicInteger nameCounter;

    /**
     * Type of template
     */
    private final Class<BoundType> type;

    // Класс, который компилируется в данный момент
    private final RuntimeJavaCompiler javaCompiler;

    // Пути к полям
    private final List<TemplatePath> paths;

    private final Map<String, Accessor> accessors;

    private final Set<String> includes;
    private final Map<String, Namespace> namespaces = new HashMap<>();

    private final ExpParser expParser;

    private SwitchCase currentSwitchCase;

    public TemplateCompileProcess(TemplateProvider provider, byte[] template, int off, int len,
                                  Class<BoundType> type, List<TemplatePath> paths) {
        this.type = type;
        this.provider = provider;

        this.off = off;
        this.len = len;

        this.template = template;
        this.expParser = new ExpParser(this::compilePathAccessor);

        this.nameCounter = new AtomicInteger();
        this.accessors = new HashMap<>();

        String simpleName = "GeneratedTemplate";
        this.javaCompiler = new RuntimeJavaCompiler(null, simpleName);

        this.includes = new HashSet<>();

        for (TemplatePath path : paths) {
            if (path.getMethodType() == TemplateMethodType.INCLUDE) {
                IncludeMethod includeMethod = path.getMethod();

                // мы добавим позже эти инклуды
                if (includeMethod.getFile().hasValues()) {
                    continue;
                }

                includes.add(includeMethod.getFile().getPath());
            }
        }

        this.paths = paths;
        this.javaCompiler.setSuperclass(COMPILED_TEMPLATE_CLASS + "<" + type.getCanonicalName() + ">");
    }

    public void addNamespace(Namespace namespace) {
        if (namespaces.containsKey(namespace.getName())) {
            throw new TemplateException("Namespace already exists: " + namespace.getName());
        }
        namespaces.put(namespace.getName(), namespace);
    }

    public void removeNamespace(String name) {
        namespaces.remove(name);
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

                Exp exp = expParser.parseExpression(value);
                String result = exp.compile();

                PathAccessor newAccessor;

                if (exp instanceof ExpValue) {
                    ExpValue expValue = (ExpValue) exp;
                    PathAccessor oldAccessor = expValue.getAccessor();

                    if (oldAccessor.getAccessor().equals(result)) {
                        newAccessor = oldAccessor;
                    } else {
                        newAccessor = new PathAccessor(exp.getObjectType(), result, oldAccessor.isStream());
                    }
                } else {
                    newAccessor = new PathAccessor(exp.getObjectType(), result, false);
                }

                return new DefaultCompiledPath(id, newAccessor, path);
            }
            default:
                String value = path.<ForeachMethod>getMethod().getPath();

                Exp exp = expParser.parseExpression(value);
                String result = exp.compile();

                return new DefaultCompiledPath(id, new PathAccessor(exp.getObjectType(), result, false), path);
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

        Namespace namespace = namespaces.get(element);

        Class<?> currentType;
        StringBuilder sb;

        if (namespace == null) {
            currentType = type;
            sb = new StringBuilder("object");
        } else {
            if (iterator.hasNext()) {
                sb = new StringBuilder(namespace.getJavaName());
                currentType = namespace.getType();

                element = iterator.next();
            } else {
                return new PathAccessor(new GenericInfo(namespace.getType()), namespace.getJavaName(), false);
            }
        }

        boolean stream;
        Method found;

        for (; ; ) {
            sb.append('.');

            String upperCamelCase_1 = "get" + toCamelCase(true, element);
            String upperCamelCase_2 = "is" + toCamelCase(true, element);

            String lowerCamelCase = toCamelCase(false, element);

            String name;

            if ((found = getMethod(currentType, upperCamelCase_1)) != null) {
                name = upperCamelCase_1;
            } else if ((found = getMethod(currentType, upperCamelCase_2)) != null) {
                name = upperCamelCase_2;
            } else if ((found = getMethod(currentType, lowerCamelCase)) != null) {
                name = lowerCamelCase;
            } else {
                return null;
            }

            sb.append(name);

            stream = found.getParameterCount() == 1 && OutputStream.class.isAssignableFrom(found.getParameterTypes()[0]);

            if (stream) {
                sb.append("(out)");
            } else {
                sb.append("()");
            }

            currentType = found.getReturnType();

            if (!iterator.hasNext()) {
                break;
            }

            element = iterator.next();
        }

        return new PathAccessor(new GenericInfo(found.getGenericReturnType()), sb.toString(), stream);
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

        StringConcatenation concatenation = new StringConcatenation(nameCounter, sb);
        concat(compiled, template.getRawContent(), template.getOffset(), template.getLength()).insert(concatenation);
        concatenation.generateFields(this);
    }

    private Accessor generateAccessor(AbstractCompiledPath path) {
        String accessorValue = path.getAccessorValue();

        switch (path.getMethodType()) {
            case VALUE:
                return path.isStream()
                        ? new RawAccessor(accessorValue + ";")
                        : new MethodAccessor(accessorValue);
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
                    Exp exp = expParser.parseExpression(from);

                    if (!(exp instanceof ExpList)) {
                        throw new UnsupportedOperationException();
                    }

                    ExpList expList = (ExpList) exp;
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
                Include include = method.getFile();

                if (include.hasValues()) {
                    SwitchCase switchCase = currentSwitchCase;

                    if (switchCase != null) {
                        fileName = include.format(switchCase.getValue());
                    } else {
                        fileName = include.format();
                    }

                    includes.add(fileName);
                } else {
                    fileName = include.format();
                }

                ParsedTemplate template = provider.parse(fileName);

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
                String fieldName = "_" + as;

                addNamespace(new Namespace(as, fieldName, listType));

                StringBuilder sb = new StringBuilder();

                sb.append("for(").append(listType.getName()).append(' ').append(fieldName).append(':').append(accessorValue).append(") {");
                List<AbstractCompiledPath> compiled = compilePaths(template.getPaths());
                generateAccessors(compiled);

                StringConcatenation concatenation = new StringConcatenation(nameCounter, sb);
                concat(compiled, template.getRawContent(), template.getOffset(), template.getLength()).insert(concatenation);
                concatenation.generateFields(this);
                sb.append('}');

                removeNamespace(as);

                return new RawAccessor(sb.toString());
            }
        }

        throw new IllegalStateException();
    }

    private final Map<BytesHashKey, String> byteValues = new HashMap<>();

    public void addBytes(String fieldName, byte[] bytes) {
        BytesHashKey key = new BytesHashKey(bytes);
        String prevFieldName = byteValues.put(key, fieldName);

        addContent("private final byte[] " + fieldName + " = "
                + (prevFieldName != null ? prevFieldName : getString(bytes)) + ";");
    }

    private String getString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        sb.append('"');

        try {
            Text.of(bytes)
                    .replaceStrategy(provider.getReplaceStrategy())
                    .compute(sb);
        } catch (IOException e) {
            e.printStackTrace();
        }

        sb.append("\".getBytes()");

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

    private String generateFormatMethod() {
        addNamespace(new Namespace("this", "object", type));

        StringBuilder sb = new StringBuilder();
        sb.append("public void render(")
                .append(type.getCanonicalName()).append(" object, ")
                .append(TEMPLATE_OUTPUT_CLASS).append(" out) {");
        sb.append("try {");
        List<AbstractCompiledPath> compiled = compilePaths(paths);
        generateAccessors(compiled);
        StringConcatenation concatenation = new StringConcatenation(nameCounter, sb);
        concat(compiled, template, off, len).insert(concatenation);
        concatenation.generateFields(this);
        sb.append("} catch (Throwable cause) { throw new com.github.lero4ka16.te4j.template.exception.TemplateException(\"Cannot render template\", cause); } }");

        return sb.toString();
    }

    public void addContent(String src) {
        javaCompiler.addContent(src);
    }

    @SuppressWarnings("unchecked")
    public Template<BoundType> compile() throws Exception {
        addContent(generateFormatMethod());

        StringBuilder includeBuilder = new StringBuilder();

        for (String include : includes) {
            if (includeBuilder.length() > 0) {
                includeBuilder.append(", ");
            }

            includeBuilder
                    .append('"')
                    .append(Text.of(provider.getRoot().resolve(include)).computeAsString())
                    .append('"');
        }

        addContent("private final String[] includes = new String[]{" + includeBuilder + "};");
        addContent("public String[] getIncludes() { return includes; }");

        Class<?> result = javaCompiler.compile();

        Constructor<?> constructor = result.getDeclaredConstructor();
        return (Template<BoundType>) constructor.newInstance();
    }

}
