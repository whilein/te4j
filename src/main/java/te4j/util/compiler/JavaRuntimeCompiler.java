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

package te4j.util.compiler;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import te4j.util.IOUtils;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lero4ka16
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class JavaRuntimeCompiler implements RuntimeCompiler {

    private final String className;
    private final StringBuilder content;

    private Collection<String> interfaces;
    private String superclass;

    public static @NonNull RuntimeCompiler create(@NonNull String className, @NonNull StringBuilder content) {
        return new JavaRuntimeCompiler(className, content);
    }

    @Override
    public @NonNull String getClassName() {
        return className;
    }

    @Override
    public String getSuperclass() {
        return superclass;
    }

    @Override
    public void setSuperclass(String superclass) {
        this.superclass = superclass;
    }

    @Override
    public Collection<String> getInterfaces() {
        return interfaces;
    }

    @Override
    public void setInterfaces(Collection<String> interfaces) {
        this.interfaces = interfaces;
    }

    @Override
    public @NonNull StringBuilder getContent() {
        return content;
    }

    @Override
    public Class<?> compile() throws IOException {
        Path tmp = Files.createTempDirectory("te4j");

        try {
            Path classOutput = tmp.resolve(className + ".java");

            try (Writer writer = Files.newBufferedWriter(classOutput)) {
                writer.write("public final class ");
                writer.write(className);
                if (superclass != null) {
                    writer.write(" extends ");
                    writer.write(superclass);
                }
                if (interfaces != null && !interfaces.isEmpty()) {
                    writer.write(" implements ");
                    writer.write(String.join(",", interfaces));
                }
                writer.write('{');
                writer.write(content.toString());
                writer.write('}');
                writer.flush();
            }

            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            int result = compiler.run(null, null, null, classOutput.toAbsolutePath().toString());

            if (result != 0) {
                throw new RuntimeException("Cannot compile class: ");
            }

            URLClassLoader classLoader = new URLClassLoader(new URL[]{tmp.toUri().toURL()});
            Class<?> cls;

            try {
                cls = classLoader.loadClass(className);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            List<Path> paths = Files.list(tmp).collect(Collectors.toList());

            for (Path path : paths) {
                String fileName = path.getFileName().toString();

                // load only synthetic file
                if (fileName.indexOf('$') == -1) continue;

                String syntheticName = fileName.substring(0, fileName.length() - 6); // .class

                try {
                    classLoader.loadClass(syntheticName);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }

            return cls;
        } finally {
            IOUtils.deleteDirectory(tmp);
        }
    }

}
