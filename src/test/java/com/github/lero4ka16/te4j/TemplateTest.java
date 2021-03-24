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

package com.github.lero4ka16.te4j;

import com.github.lero4ka16.te4j.modifiable.watcher.ModifyWatcherManager;
import com.github.lero4ka16.te4j.template.Template;
import com.github.lero4ka16.te4j.template.context.TemplateContext;
import com.github.lero4ka16.te4j.util.Utils;
import com.github.lero4ka16.te4j.util.type.ref.ClassRef;
import com.github.lero4ka16.te4j.util.type.ref.ITypeRef;
import com.github.lero4ka16.te4j.util.type.ref.TypeRef;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author lero4ka16
 */
public class TemplateTest {
    private Object dummy;

    private TemplateContext context;
    private TemplateContext trimContext;
    private TemplateContext hotReloadContext;

    private File tests;

    @AfterEach
    public void clean() {
        Utils.deleteDirectory(tests);
    }

    @BeforeEach
    public void init() {
        dummy = new Object();

        context = Te4j.custom()
                .useResources()
                .replace(Te4j.DEL_ALL)
                .build();

        trimContext = Te4j.custom()
                .useResources()
                .build();

        hotReloadContext = Te4j.custom()
                .replace(Te4j.DEL_ALL)
                .enableHotReloading(new ModifyWatcherManager())
                .build();

        tests = new File("tests");
        tests.mkdirs();
    }

    @Test
    public void testTrim() {
        String expect;

        if (System.getProperty("os.name").contains("Windows")) {
            expect = "Inline if: Yes\r\n\r\nMultiline list:\r\n  - ^Hi\r\n  - ^Hello\r\n  - ^Bye\r\n  - ^Goodbye\r\n";
        } else {
            expect = "Inline if: Yes\n\nMultiline list:\n  - ^Hi\n  - ^Hello\n  - ^Bye\n  - ^Goodbye\n";
        }

        testTemplate(trimContext,
                "WEB-INF/trim.txt", expect,
                new Pojo_6(true, Arrays.asList("Hi", "Hello", "Bye", "Goodbye")), new ClassRef<>(Pojo_6.class));
    }

    @Test
    public void testHotReload() throws InterruptedException {
        Path plain_1 = Paths.get("tests/hotreload_plain_1.txt");
        Path plain_2 = Paths.get("tests/hotreload_plain_2.txt");

        copyResource("WEB-INF/hotreload_plain_1.txt", plain_1);
        copyResource("WEB-INF/hotreload_plain_1.txt", plain_2);

        Thread.sleep(100);

        Template<Object> template_1 = hotReloadContext.loadFile(Object.class, plain_1);
        Template<Object> template_2 = hotReloadContext.loadFile(Object.class, plain_2);

        assertEquals("Before hot reload", template_1.renderAsString(dummy));
        assertEquals("Before hot reload", template_2.renderAsString(dummy));

        copyResource("WEB-INF/hotreload_plain_2.txt", plain_1);
        copyResource("WEB-INF/hotreload_plain_2.txt", plain_2);

        // Слушание событий происходит в отдельном потоке
        // ждём немного, перед тем, чтобы сделать проверку
        Thread.sleep(100);

        assertEquals("After hot reload", template_1.renderAsString(dummy));
        assertEquals("After hot reload", template_2.renderAsString(dummy));
    }

    @Test
    public void testPlain() {
        testTemplate(context,
                "WEB-INF/plain.txt", "Hello world!Привет мир!",
                dummy, new ClassRef<>(Object.class));
    }

    @Test
    public void testValue() {
        testTemplate(context,
                "WEB-INF/greeting.txt", "Hello my friend!",
                new Pojo_1("my friend"), new ClassRef<>(Pojo_1.class));
    }

    @Test
    public void testForeach() {
        testTemplate(context,
                "WEB-INF/foreach.txt", "<a>0: 10</a><a>1: 20</a><a>2: 30</a><a>10</a><a>20</a><a>30</a>",
                new Pojo_2(10, 20, 30), new ClassRef<>(Pojo_2.class));
    }

    @Test
    public void testForeachCollection() {
        testTemplate(context,
                "WEB-INF/foreach.txt", "<a>0: 15</a><a>1: 25</a><a>2: 35</a><a>15</a><a>25</a><a>35</a>",
                new Pojo_5(15, 25, 35), new ClassRef<>(Pojo_5.class));
    }

    @Test
    public void testForeachGeneric() {
        testTemplate(context,
                "WEB-INF/foreach_generic.txt", "<a>0: 50</a><a>1: 100</a><a>2: 200</a>",
                Arrays.asList(50, 100, 200), new TypeRef<List<Integer>>() {
                });
    }

    @Test
    public void testConditionFalse() {
        testTemplate(context,
                "WEB-INF/condition.txt", "<a>Result is false</a>",
                new Pojo_3("Hello world", false), new ClassRef<>(Pojo_3.class));
    }

    @Test
    public void testConditionTrue() {
        testTemplate(context,
                "WEB-INF/condition.txt", "<a>Hello world</a>",
                new Pojo_3("Hello world", true), new ClassRef<>(Pojo_3.class));
    }

    @Test
    public void testSwitchCase_Condition() {
        testTemplate(context,
                "WEB-INF/switchcase.txt", "<a>Goodbye my friend</a>",
                new Pojo_4("Goodbye my friend", true), new ClassRef<>(Pojo_4.class));
    }

    @Test
    public void testSwitchCase_Foreach() {
        testTemplate(context,
                "WEB-INF/switchcase.txt", "<a>0: 5</a><a>1: 10</a><a>2: 15</a><a>5</a><a>10</a><a>15</a>",
                new Pojo_4(new int[]{5, 10, 15}), new ClassRef<>(Pojo_4.class));
    }

    @Test
    public void testSwitchCase_Value() {
        testTemplate(context,
                "WEB-INF/switchcase.txt", "Hello you!",
                new Pojo_4("you"), new ClassRef<>(Pojo_4.class));
    }

    private void copyResource(String resource, Path to) {
        try (InputStream is = ClassLoader.getSystemResourceAsStream(resource);
             OutputStream os = Files.newOutputStream(to)) {
            assert is != null;

            byte[] buf = new byte[1024];
            int n;

            while ((n = is.read(buf)) != -1) {
                os.write(buf, 0, n);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private <T> void testTemplate(TemplateContext context, String resource, String expectText,
                                  T object, ITypeRef<T> type) {
        String result = context.load(type, resource).renderAsString(object);
        assertEquals(expectText, result);
    }

    public static class Pojo_1 {
        private final String name;

        public Pojo_1(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static class Pojo_2 {
        private final int[] elements;

        public Pojo_2(int... elements) {
            this.elements = elements;
        }

        public int[] getElements() {
            return elements;
        }
    }

    public static class Pojo_3 {
        private final String message;
        private final boolean condition;

        public Pojo_3(String message, boolean condition) {
            this.message = message;
            this.condition = condition;
        }

        public String getMessage() {
            return message;
        }

        public boolean getCondition() {
            return condition;
        }
    }

    public static class Pojo_4 {
        private final Check check;

        private final String name;
        private final int[] elements;
        private final String message;
        private final boolean condition;

        public Pojo_4(Check check, String name, int[] elements, String message, boolean condition) {
            this.check = check;
            this.name = name;
            this.elements = elements;
            this.message = message;
            this.condition = condition;
        }

        public Pojo_4(String name) {
            this(Check.GREETING, name, null, null, false);
        }

        public Pojo_4(int[] elements) {
            this(Check.FOREACH, null, elements, null, false);
        }

        public Pojo_4(String message, boolean condition) {
            this(Check.CONDITION, null, null, message, condition);
        }

        public Check getCheck() {
            return check;
        }

        public String getName() {
            return name;
        }

        public int[] getElements() {
            return elements;
        }

        public String getMessage() {
            return message;
        }

        public boolean isCondition() {
            return condition;
        }
    }

    public static class Pojo_5 {
        private final List<Integer> elements;

        public Pojo_5(int... elements) {
            this.elements = IntStream.of(elements).boxed().collect(Collectors.toList());
        }

        public Collection<Integer> getElements() {
            return elements;
        }
    }

    public static class Pojo_6 {
        private final boolean condition;
        private final List<String> list;

        public Pojo_6(boolean condition, List<String> list) {
            this.condition = condition;
            this.list = list;
        }

        public boolean isCondition() {
            return condition;
        }

        public List<String> getList() {
            return list;
        }
    }

    public enum Check {

        CONDITION, FOREACH, GREETING;

        public String getName() {
            return name().toLowerCase();
        }

    }

}
