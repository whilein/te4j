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

package te4j.template.compiler;

import te4j.template.Template;
import te4j.util.Utils;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author lero4ka16
 */
final class TemplateClassCompiler {

    private static final String TEMPLATE_CLASS
            = Template.class.getName();

    private static final ThreadLocal<TemplateClassCompiler> THREAD_LOCAL
            = ThreadLocal.withInitial(TemplateClassCompiler::new);

    private String templateType;

    private final StringBuilder content = new StringBuilder();
    private final File out;

    public static TemplateClassCompiler current() {
        return THREAD_LOCAL.get();
    }

    private TemplateClassCompiler() {
        out = new File("tmp-" + Integer.toHexString(Thread.currentThread().hashCode()));
    }

    void clearContent() {
        content.setLength(0);
    }

    void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    void addContent(String content) {
        this.content.append(content);
    }

    Class<?> compile() throws IOException {
        try {
            if (!out.mkdirs())
                throw new RuntimeException("Cannot create directory: " + out.getName());

            File tmp = new File(out, "GeneratedTemplate.java");

            try (Writer writer = new FileWriter(tmp)) {
                writer.write("public final class GeneratedTemplate");
                writer.write(" extends ");
                writer.write(TEMPLATE_CLASS);
                writer.write('<');
                writer.write(templateType);
                writer.write('>');
                writer.write('{');
                writer.write(content.toString());
                writer.write('}');
                writer.flush();
            }

            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            int result = compiler.run(null, null, null, tmp.getAbsolutePath());

            if (result != 0) {
                throw new RuntimeException("Cannot compile class: " + result);
            }

            URLClassLoader classLoader = new URLClassLoader(new URL[]{out.toURI().toURL()});
            Class<?> cls;

            try {
                cls = classLoader.loadClass("GeneratedTemplate");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            File[] files = out.listFiles();
            assert files != null;

            for (File in : files) {
                String fileName = in.getName();

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
            Utils.deleteDirectory(out);
            clearContent();
        }
    }

}
