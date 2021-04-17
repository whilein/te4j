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

package te4j;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import te4j.modifiable.watcher.ModifyWatcherManager;
import te4j.template.Template;
import te4j.template.context.TemplateContext;
import te4j.util.Utils;
import te4j.util.type.ref.ClassReference;
import te4j.util.type.ref.TypeRef;
import te4j.util.type.ref.TypeReference;

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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author lero4ka16
 */
public class TemplateTest {
    private Object dummy;

    private TemplateContext context;
    private TemplateContext trimContext;
    private TemplateContext autoReloadContext;

    private ModifyWatcherManager modifyManager;

    private File tests;

    @AfterEach
    public void clean() {
        Utils.deleteDirectory(tests);
    }

    @BeforeEach
    public void init() {
        dummy = new Object();

        modifyManager = new ModifyWatcherManager();

        context = Te4j.custom()
                .useResources()
                .minifyAll()
                .build();

        trimContext = Te4j.custom()
                .useResources()
                .build();

        autoReloadContext = Te4j.custom()
                .minifyAll()
                .enableAutoReloading(modifyManager)
                .build();

        tests = new File("tests");
        tests.mkdirs();
    }

    @AfterEach
    public void finish() {
        modifyManager.terminate();
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
                new Pojo_6(true, Arrays.asList("Hi", "Hello", "Bye", "Goodbye")), ClassReference.create(Pojo_6.class));
    }

    @Test
    public void testAutoReload() throws InterruptedException {
        Path plain1 = Paths.get("tests/autoreload_plain_1.txt");
        Path plain2 = Paths.get("tests/autoreload_plain_2.txt");
        Path plain3 = Paths.get("tests/autoreload_plain_3.txt");
        Path plain4 = Paths.get("tests/autoreload_plain_4.txt");

        copyResource("WEB-INF/autoreload_plain_1.txt", plain1);
        copyResource("WEB-INF/autoreload_plain_1.txt", plain2);
        copyResource("WEB-INF/autoreload_plain_3.txt", plain3);
        copyResource("WEB-INF/autoreload_plain_1.txt", plain4);

        Thread.sleep(1000);

        Template<Object> template1 = autoReloadContext.load(Object.class).fromFile(plain1);
        Template<Object> template2 = autoReloadContext.load(Object.class).fromFile(plain2);
        Template<Object> template3 = autoReloadContext.load(Object.class).fromFile(plain3);

        Template<Object> template4 = autoReloadContext
                .load(Object.class)
                .fromString("<* include tests/autoreload_plain_1.txt *>");

        assertEquals("Before auto reload", template1.renderAsString(dummy));
        assertEquals("Before auto reload", template2.renderAsString(dummy));
        assertEquals("Before auto reloadBefore auto reload", template3.renderAsString(dummy));
        assertEquals("Before auto reload", template4.renderAsString(dummy));

        copyResource("WEB-INF/autoreload_plain_2.txt", plain1);
        copyResource("WEB-INF/autoreload_plain_2.txt", plain2);
        copyResource("WEB-INF/autoreload_plain_2.txt", plain4);
        copyResource("WEB-INF/autoreload_plain_4.txt", plain3);

        // Слушание событий происходит в отдельном потоке
        // ждём немного, перед тем, чтобы сделать проверку
        Thread.sleep(1000);
        assertEquals("After auto reload", template1.renderAsString(dummy));
        assertEquals("After auto reload", template2.renderAsString(dummy));
        assertEquals("After auto reload", template3.renderAsString(dummy));
        assertEquals("After auto reload", template4.renderAsString(dummy));
    }

    @Test
    public void testPlain() {
        testTemplate(context,
                "WEB-INF/plain.txt", "Hello world!Привет мир!",
                dummy, ClassReference.create(Object.class));
    }

    @Test
    public void testValue() {
        testTemplate(context,
                "WEB-INF/greeting.txt", "Hello my friend!",
                new Pojo_1("my friend"), ClassReference.create(Pojo_1.class));
    }

    @Test
    public void testForeach() {
        testTemplate(context,
                "WEB-INF/foreach.txt", "<a>0: 10</a><a>1: 20</a><a>2: 30</a><a>10</a><a>20</a><a>30</a>",
                new Pojo_2(10, 20, 30), ClassReference.create(Pojo_2.class));
    }

    @Test
    public void testForeachCollection() {
        testTemplate(context,
                "WEB-INF/foreach.txt", "<a>0: 15</a><a>1: 25</a><a>2: 35</a><a>15</a><a>25</a><a>35</a>",
                new Pojo_5(15, 25, 35), ClassReference.create(Pojo_5.class));
    }

    @Test
    public void testForeachGeneric() {
        testTemplate(context,
                "WEB-INF/foreach_generic.txt", "<a>0: 50</a><a>1: 100</a><a>2: 200</a>",
                Arrays.asList(50, 100, 200), new TypeRef<List<Integer>>() {
                });
    }

    @Test
    public void testConditionFalseFalse() {
        testTemplate(context,
                "WEB-INF/condition.txt", "<a>Result is false</a>",
                new Pojo_3("Hello world", false, false), ClassReference.create(Pojo_3.class));
    }

    @Test
    public void testConditionFalseTrue() {
        testTemplate(context,
                "WEB-INF/condition.txt", "<a>Another condition</a>",
                new Pojo_3("Hello world", false, true), ClassReference.create(Pojo_3.class));
    }

    @Test
    public void testConditionTrue() {
        testTemplate(context,
                "WEB-INF/condition.txt", "<a>Hello world</a>",
                new Pojo_3("Hello world", true, false), ClassReference.create(Pojo_3.class));
    }

    @Test
    public void testSwitchCase_Condition() {
        testTemplate(context,
                "WEB-INF/switchcase.txt", "<a>Goodbye my friend</a>",
                new Pojo_4("Goodbye my friend", true), ClassReference.create(Pojo_4.class));
    }

    @Test
    public void testSwitchCase_Foreach() {
        testTemplate(context,
                "WEB-INF/switchcase.txt", "<a>0: 5</a><a>1: 10</a><a>2: 15</a><a>5</a><a>10</a><a>15</a>",
                new Pojo_4(new int[]{5, 10, 15}), ClassReference.create(Pojo_4.class));
    }

    @Test
    public void testSwitchCase_Value() {
        testTemplate(context,
                "WEB-INF/switchcase.txt", "Hello you!",
                new Pojo_4("you"), ClassReference.create(Pojo_4.class));
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
                                  T object, TypeReference<T> type) {
        String result = context.load(type).from(resource).renderAsString(object);
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
        private final boolean anotherCondition;

        public Pojo_3(String message, boolean condition, boolean anotherCondition) {
            this.message = message;
            this.condition = condition;
            this.anotherCondition = anotherCondition;
        }

        public String getMessage() {
            return message;
        }

        public boolean getCondition() {
            return condition;
        }

        public boolean getAnotherCondition() {
            return anotherCondition;
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

        public boolean getCondition() {
            return condition;
        }

        public boolean getAnotherCondition() {
            return false;
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
