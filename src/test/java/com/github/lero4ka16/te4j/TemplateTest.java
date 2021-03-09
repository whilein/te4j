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

import com.github.lero4ka16.te4j.template.Template;
import com.github.lero4ka16.te4j.template.context.TemplateContext;
import com.github.lero4ka16.te4j.util.type.ref.ClassRef;
import com.github.lero4ka16.te4j.util.type.ref.TypeRef;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author lero4ka16
 */
public class TemplateTest {

    private Object dummy;

    private TemplateContext context;
    private TemplateContext hotreloadContext;

    private Path tests;

    @AfterEach
    public void clean() throws IOException {
        Files.walk(tests).sorted(Comparator.reverseOrder())
                .forEach(this::deleteSilently);
    }

    private void deleteSilently(Path path) {
        try {
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    public void init() throws IOException {
        dummy = new Object();

        context = Te4j.custom()
                .useResources()
                .replace(Te4j.DEL_ALL)
                .build();

        hotreloadContext = Te4j.custom()
                .replace(Te4j.DEL_ALL)
                .enableHotReloading()
                .build();

        tests = Paths.get("tests");

        Files.createDirectories(tests);
    }

    @Test
    public void testPlainHotReload() throws InterruptedException {
        Path plain = Paths.get("tests/hotreload_plain.html");
        copyResource("WEB-INF/hotreload_plain_1.html", plain);

        Template<Object> template = hotreloadContext.load(Object.class, "tests/hotreload_plain.html");
        assertEquals("Before hot reload", template.renderAsString(dummy));

        copyResource("WEB-INF/hotreload_plain_2.html", plain);

        // Слушание событий происходит в отдельном потоке
        // ждём немного, перед тем, чтобы сделать проверку
        Thread.sleep(10);

        assertEquals("After hot reload", template.renderAsString(dummy));
    }

    @Test
    public void testPlain() {
        testTemplate(context,
                "WEB-INF/plain.html", "Hello world!Привет мир!",
                dummy, new ClassRef<>(Object.class));
    }

    @Test
    public void testValue() {
        testTemplate(context,
                "WEB-INF/value.html", "Hello my friend!",
                new Example_1("my friend"), new ClassRef<>(Example_1.class));
    }

    @Test
    public void testForeach() {
        testTemplate(context,
                "WEB-INF/foreach.html", "<a>0: 10</a><a>1: 20</a><a>2: 30</a><a>10</a><a>20</a><a>30</a>",
                new Example_2(10, 20, 30), new ClassRef<>(Example_2.class));
    }

    @Test
    public void testForeachCollection() {
        testTemplate(context,
                "WEB-INF/foreach.html", "<a>0: 15</a><a>1: 25</a><a>2: 35</a><a>15</a><a>25</a><a>35</a>",
                new Example_5(15, 25, 35), new ClassRef<>(Example_5.class));
    }

    @Test
    public void testForeachGeneric() {
        testTemplate(context,
                "WEB-INF/foreach_generic.html", "<a>0: 50</a><a>1: 100</a><a>2: 200</a>",
                Arrays.asList(50, 100, 200), new TypeRef<List<Integer>>() {
                });
    }

    @Test
    public void testConditionFalse() {
        testTemplate(context,
                "WEB-INF/condition.html", "<a>Result is false</a>",
                new Example_3("Hello world", false), new ClassRef<>(Example_3.class));
    }

    @Test
    public void testConditionTrue() {
        testTemplate(context,
                "WEB-INF/condition.html", "<a>Hello world</a>",
                new Example_3("Hello world", true), new ClassRef<>(Example_3.class));
    }

    @Test
    public void testSwitchCase_Condition() {
        testTemplate(context,
                "WEB-INF/switchcase.html", "<a>Goodbye my friend</a>",
                new Example_4("Goodbye my friend", true), new ClassRef<>(Example_4.class));
    }

    @Test
    public void testSwitchCase_Foreach() {
        testTemplate(context,
                "WEB-INF/switchcase.html", "<a>0: 5</a><a>1: 10</a><a>2: 15</a><a>5</a><a>10</a><a>15</a>",
                new Example_4(new int[]{5, 10, 15}), new ClassRef<>(Example_4.class));
    }

    @Test
    public void testSwitchCase_Value() {
        testTemplate(context,
                "WEB-INF/switchcase.html", "Hello you!",
                new Example_4("you"), new ClassRef<>(Example_4.class));
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
                                  T object, TypeRef<T> type) {
        String result = context.load(type, resource).renderAsString(object);
        assertEquals(expectText, result);
    }

    public static class Example_1 {
        private final String name;

        public Example_1(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static class Example_2 {
        private final int[] elements;

        public Example_2(int... elements) {
            this.elements = elements;
        }

        public int[] getElements() {
            return elements;
        }
    }

    public static class Example_3 {
        private final String message;
        private final boolean condition;

        public Example_3(String message, boolean condition) {
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

    public static class Example_4 {
        private final Check check;

        private final String name;
        private final int[] elements;
        private final String message;
        private final boolean condition;

        public Example_4(Check check, String name, int[] elements, String message, boolean condition) {
            this.check = check;
            this.name = name;
            this.elements = elements;
            this.message = message;
            this.condition = condition;
        }

        public Example_4(String name) {
            this(Check.VALUE, name, null, null, false);
        }

        public Example_4(int[] elements) {
            this(Check.FOREACH, null, elements, null, false);
        }

        public Example_4(String message, boolean condition) {
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

    public static class Example_5 {
        private final List<Integer> elements;

        public Example_5(int... elements) {
            this.elements = IntStream.of(elements).boxed().collect(Collectors.toList());
        }

        public Collection<Integer> getElements() {
            return elements;
        }
    }

    public enum Check {

        CONDITION, FOREACH, VALUE;

        public String getName() {
            return name().toLowerCase();
        }

    }

}
