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

package com.github.lero4ka16.te4j.util;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.net.URLClassLoader;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lero4ka16
 */
public final class RuntimeJavaCompiler {

    private final String pkg;
    private final String name;

    private String superclass;

    private List<String> interfaces;

    private final StringBuilder content = new StringBuilder();

    private static final File TMP = new File("tmp");
    private static final Lock LOCK = new Lock();

    public RuntimeJavaCompiler(String pkg, String name) {
        this.pkg = pkg;
        this.name = name;
    }

    public void setSuperclass(String superclass) {
        this.superclass = superclass;
    }

    public void addInterface(String cls) {
        if (interfaces == null) {
            interfaces = new ArrayList<>();
        }

        interfaces.add(cls);
    }

    public void addContent(String content) {
        this.content.append(content);
    }

    public Class<?> compile() throws IOException {
        LOCK.lock();

        try {
            if (!TMP.mkdirs()) {
                throw new RuntimeException("Cannot create directory: " + TMP.getName());
            }

            File tmp = new File(TMP, name + ".java");

            try (Writer writer = new FileWriter(tmp)) {
                if (pkg != null) {
                    writer.write("package ");
                    writer.write(pkg);
                    writer.write(';');
                }

                writer.write("public final class ");
                writer.write(name);

                if (superclass != null) {
                    writer.append(" extends ").append(superclass);
                }

                if (interfaces != null) {
                    writer.write(" implements ");
                    writer.write(String.join(", ", interfaces));
                }

                writer.write(" {");
                writer.write(content.toString());
                writer.write("}");
            }

            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            int result = compiler.run(null, null, null, tmp.getAbsolutePath());

            if (result != 0) {
                throw new RemoteException("Cannot compile class: " + result);
            }

            URLClassLoader classLoader = new URLClassLoader(new URL[]{TMP.toURI().toURL()});
            Class<?> cls;

            try {
                cls = classLoader.loadClass(name);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            File[] files = TMP.listFiles();
            assert files != null;

            for (File in : files) {
                String fileName = in.getName();

                // load only synthetic file
                if (fileName.indexOf('$') == -1) continue;

                String className = fileName.substring(0, fileName.length() - 6); // .class

                try {
                    classLoader.loadClass(className);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }

            return cls;
        } finally {
            Utils.deleteDirectory(TMP);
            LOCK.unlock();
        }
    }

}
