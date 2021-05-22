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

package te4j;

import lombok.Data;
import lombok.NonNull;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import te4j.modifiable.watcher.ModifyWatcherManager;
import te4j.template.context.TemplateContext;
import te4j.template.option.output.Output;
import te4j.util.type.ref.TypeRef;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author whilein
 */
public class TemplateTest {

    private static final String TRIM_INPUT
            = "Inline if:{% if condition %} Yes{% else %} No{% endif %}\n\nMultiline list:\n{% for element : list %}\n  - {{{ element }}}\n{% endfor %}";

    private static final String TRIM_EXPECT
            = "Inline if: Yes\n\nMultiline list:\n  - {Hi}\n  - {Hello}\n  - {Bye}\n  - {Goodbye}\n";

    private static final String NAME_INPUT = "Hello   {{ name }}!";
    private static final String NAME_EXPECT = "Hello my friend!";

    private static final String CONDITION_INPUT
            = "{% if condition %}\n<a>Result is true</a>\n{% elif another_condition %}\n<a>Another result is true</a>\n{% else %}\n<a>Result is false</a>\n{% endif %}";

    private static final String CONDITION_EXPECT_FALSE_FALSE = "<a>Result is false</a>";
    private static final String CONDITION_EXPECT_FALSE_TRUE = "<a>Another result is true</a>";
    private static final String CONDITION_EXPECT_TRUE = "<a>Result is true</a>";

    private static final String FOREACH_INPUT
            = "{% for element : elements %}\n<a>{{ loop.index }}: {{ element }}</a>\n{% endfor %}\n" +
            "\n{% for element : elements %}\n<a>[{{ loop.first }}/{{ loop.last }}]: {{ element }}</a>\n{% endfor %}";

    private static final String FOREACH_GENERIC_INPUT
            = "{% for element : this %}\n<a>{{ loop.index }}: {{ element }}</a>\n{% endfor %}";

    private static final String FOREACH_EXPECT_10_20_30
            = "<a>0: 10</a><a>1: 20</a><a>2: 30</a><a>[true/false]: 10</a><a>[false/false]: 20</a><a>[false/true]: 30</a>";

    private static final String FOREACH_GENERIC_EXPECT_15_25_35
            = "<a>0: 15</a><a>1: 25</a><a>2: 35</a>";

    private static final String FOREACH_EXPECT_10_50_100
            = "<a>0: 10</a><a>1: 50</a><a>2: 100</a><a>[true/false]: 10</a><a>[false/false]: 50</a><a>[false/true]: 100</a>";


    private static final String SWITCHCASE_INPUT
            = "{% case check.name : [\"condition\", \"foreach\", \"name\"] %}\n{% include [$] %}\n{% endcase %}";

    private static final String SWITCHCASE_EXPECT_NAME
            = "Hello you!";

    private Object dummy;

    private TemplateContext context;
    private TemplateContext trimContext;
    private TemplateContext autoReloadContext;

    private ModifyWatcherManager modifyManager;

    private Map<String, byte[]> files;

    private void putTemplate(final @NotNull String file, final @NotNull String content) {
        files.put(file, content.getBytes(StandardCharsets.UTF_8));
    }

    private void removeTemplate(final @NotNull String file) {
        files.remove(file);
    }

    @BeforeEach
    public void init() {
        dummy = new Object();

        files = new HashMap<>();

        modifyManager = FakeModifyWatcherManager.create();

        val resolver = FakeTemplateResolver.create(files);

        context = Te4j.custom()
                .resolver(resolver)
                .minifyAll()
                .output(Output.STRING)
                .build();

        trimContext = Te4j.custom()
                .resolver(resolver)
                .build();

        autoReloadContext = Te4j.custom()
                .resolver(resolver)
                .minifyAll()
                .enableAutoReloading(modifyManager)
                .build();
    }

    @AfterEach
    public void finish() throws IOException {
        modifyManager.close();
    }

    @Test
    public void testTrim() {
        val template = trimContext.load(Trim.class)
                .fromString(TRIM_INPUT);

        assertEquals(TRIM_EXPECT, template.renderAsString(
                new Trim(true, Arrays.asList("Hi", "Hello", "Bye", "Goodbye"))));
    }

    @Test
    public void testAutoReload() {
        putTemplate("test", "Hi {{ name }}!");

        val template = autoReloadContext.load(Name.class)
                .from("test");

        assertEquals("Hi Lera!", template.renderAsString(new Name("Lera")));

        putTemplate("test", "Goodbye {{ name }}!");
        modifyManager.handle("test");

        assertEquals("Goodbye John!", template.renderAsString(new Name("John")));

        removeTemplate("test");
        modifyManager.handle("test");

        assertEquals("", template.renderAsString(new Name("Empty")));
    }

    @Test
    public void testPlain() {
        val input = "Hello   world!\n\nПривет мир!";
        val expect = "Hello world!Привет мир!";

        val template = context.load(Object.class)
                .fromString(input);

        assertEquals(expect, template.renderAsString(dummy));
    }

    @Test
    public void testValue() {
        val template = context.load(NameCheck.class)
                .fromString(NAME_INPUT);

        assertEquals(NAME_EXPECT, template.renderAsString(new Name("my friend")));
    }

    @Test
    public void testForeach() {
        val template = context.load(ForeachCheck.class)
                .fromString(FOREACH_INPUT);

        assertEquals(FOREACH_EXPECT_10_20_30, template.renderAsString(Foreach.of(10, 20, 30)));
    }

    @Test
    public void testForeachCollection() {
        val expect = "<a>0: 15</a><a>1: 25</a><a>2: 35</a><a>[true/false]: 15</a><a>[false/false]: 25</a><a>[false/true]: 35</a>";

        val template = context.load(ForeachCollectionCheck.class)
                .fromString(FOREACH_INPUT);

        assertEquals(expect, template.renderAsString(ForeachCollection.of(15, 25, 35)));
    }

    @Test
    public void testForeachGeneric() {
        val template = context.load(new TypeRef<List<Integer>>() {})
                .fromString(FOREACH_GENERIC_INPUT);

        assertEquals(FOREACH_GENERIC_EXPECT_15_25_35, template.renderAsString(Arrays.asList(15, 25, 35)));
    }

    @Test
    public void testCondition() {
        val template = context.load(ConditionCheck.class)
                .fromString(CONDITION_INPUT);

        assertEquals(CONDITION_EXPECT_FALSE_FALSE, template.renderAsString(
                new Condition(false, false)));

        assertEquals(CONDITION_EXPECT_FALSE_TRUE, template.renderAsString(
                new Condition(false, true)));

        assertEquals(CONDITION_EXPECT_TRUE, template.renderAsString(
                new Condition(true, false)));
    }

    @Test
    public void testSwitchCase() {
        putTemplate("condition", CONDITION_INPUT);
        putTemplate("foreach", FOREACH_INPUT);
        putTemplate("name", NAME_INPUT);

        val template = context.load(SwitchCase.class)
                .fromString(SWITCHCASE_INPUT);

        assertEquals(SWITCHCASE_EXPECT_NAME,
                template.renderAsString(SwitchCase.create("you")));

        assertEquals(CONDITION_EXPECT_TRUE,
                template.renderAsString(SwitchCase.create(true, false)));

        assertEquals(FOREACH_EXPECT_10_50_100,
                template.renderAsString(SwitchCase.create(10, 50, 100)));
    }

    public interface Check {
        @NotNull CheckType getCheck();
    }

    public interface NameCheck extends Check {
        @NotNull String getName();
    }

    public interface ForeachCheck extends Check {
        int @NotNull [] getElements();
    }

    public interface ForeachCollectionCheck extends Check {
        @NotNull List<@NotNull Integer> getElements();
    }

    public interface ConditionCheck extends Check {
        boolean isCondition();
        boolean isAnotherCondition();
    }

    @Data
    public static class SwitchCase implements NameCheck, ForeachCheck, ConditionCheck {
        private final CheckType check;
        private final String name;
        private final int[] elements;
        private final boolean condition;
        private final boolean anotherCondition;

        public static SwitchCase create(final @NonNull String name) {
            return new SwitchCase(CheckType.NAME, name, null, false, false);
        }

        public static SwitchCase create(final int @NonNull ... elements) {
            return new SwitchCase(CheckType.FOREACH, null, elements, false, false);
        }

        public static SwitchCase create(final boolean condition, final boolean anotherCondition) {
            return new SwitchCase(CheckType.CONDITION, null, null, condition, anotherCondition);
        }
    }

    @Data
    public static class Name implements NameCheck {
        private final String name;

        @Override
        public @NotNull CheckType getCheck() {
            return CheckType.NAME;
        }
    }

    @Data
    public static class Foreach implements ForeachCheck {
        private final int[] elements;

        public static Foreach of(int... elements) {
            return new Foreach(elements);
        }

        @Override
        public @NotNull CheckType getCheck() {
            return CheckType.FOREACH;
        }
    }

    @Data
    public static class ForeachCollection implements ForeachCollectionCheck {
        private final List<Integer> elements;

        public static ForeachCollection of(int... elements) {
            return new ForeachCollection(IntStream.of(elements).boxed().collect(Collectors.toList()));
        }

        @Override
        public @NotNull CheckType getCheck() {
            return CheckType.NAME;
        }

    }

    @Data
    public static class Condition implements ConditionCheck {
        private final boolean condition;
        private final boolean anotherCondition;

        @Override
        public @NotNull CheckType getCheck() {
            return CheckType.CONDITION;
        }
    }

    @Data
    public static class Trim {
        private final boolean condition;
        private final List<String> list;
    }

    public enum CheckType {

        CONDITION, FOREACH, NAME;

        public String getName() {
            return name().toLowerCase();
        }

    }

}
