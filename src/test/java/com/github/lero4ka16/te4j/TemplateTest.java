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

import com.github.lero4ka16.te4j.template.provider.TemplateProvider;
import com.github.lero4ka16.te4j.util.replace.ReplaceStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Лера
 */
public class TemplateTest {

    @Test
    public void testPlain() {
        testTemplate(Te4j.custom().useResources().build(),
                "plain.html", "Hello   world!\r\n\r\nПривет мир!",
                null, Object.class);
    }

    @Test
    public void testPlainReplacing() {
        testTemplate(Te4j.custom().useResources().replaceStrategy(ReplaceStrategy.ALL).build(),
                "plain.html", "Hello world!Привет мир!",
                null, Object.class);
    }

    @Test
    public void testValue() {
        testTemplate(Te4j.custom().useResources().build(),
                "value.html", "Hello   my friend!",
                new Example_1("my friend"), Example_1.class);
    }

    @Test
    public void testValueReplacing() {
        testTemplate(Te4j.custom().useResources().replaceStrategy(ReplaceStrategy.ALL).build(),
                "value.html", "Hello my friend!",
                new Example_1("my friend"), Example_1.class);
    }

    @Test
    public void testForeach() {
        testTemplate(Te4j.custom().useResources().replaceStrategy(ReplaceStrategy.ALL).build(),
                "foreach.html", "<a>1</a><a>2</a><a>3</a>",
                new Example_2(1, 2, 3), Example_2.class);
    }

    @Test
    public void testConditionFalse() {
        testTemplate(Te4j.custom().useResources().replaceStrategy(ReplaceStrategy.ALL).build(),
                "condition.html", "<a>Result is false</a>",
                new Example_3("Hello world", false), Example_3.class);
    }

    @Test
    public void testConditionTrue() {
        testTemplate(Te4j.custom().useResources().replaceStrategy(ReplaceStrategy.ALL).build(),
                "condition.html", "<a>Hello world</a>",
                new Example_3("Hello world", true), Example_3.class);
    }

    @Test
    public void testSwitchCase_Condition() {
        testTemplate(Te4j.custom().useResources().replaceStrategy(ReplaceStrategy.ALL).build(),
                "switchcase.html", "<a>Goodbye my friend</a>",
                new Example_4("Goodbye my friend", true), Example_4.class);
    }

    @Test
    public void testSwitchCase_Foreach() {
        testTemplate(Te4j.custom().useResources().replaceStrategy(ReplaceStrategy.ALL).build(),
                "switchcase.html", "<a>5</a><a>10</a><a>15</a>",
                new Example_4(new int[] { 5, 10, 15 }), Example_4.class);
    }

    @Test
    public void testSwitchCase_Value() {
        testTemplate(Te4j.custom().useResources().replaceStrategy(ReplaceStrategy.ALL).build(),
                "switchcase.html", "Hello you!",
                new Example_4("you"), Example_4.class);
    }

    private <T> void testTemplate(TemplateProvider provider, String resource, String expectText,
                                  T object, Class<T> cls) {
        String result = provider.load(cls, resource).renderAsString(object);
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

    public enum Check {

        CONDITION, FOREACH, VALUE;

        public String getName() {
            return name().toLowerCase();
        }

    }

}
