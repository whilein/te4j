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

import org.junit.jupiter.api.Test;
import te4j.template.compiler.exp.ExpNumber;
import te4j.template.compiler.exp.ExpParser;
import te4j.template.compiler.path.PathAccessor;
import te4j.util.TypeUtils;
import te4j.util.type.GenericInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author lero4ka16
 */
public class ExpressionTest {

    private final ExpParser parser = new ExpParser(Te4j.getFilters(), value -> {
        if (value.equals("message")) {
            return new PathAccessor(GenericInfo.STRING, "getMessage()");
        }

        if (value.equals("condition")) {
            return new PathAccessor(GenericInfo.PRIMITIVE_BOOLEAN, "isCondition()");
        }

        try {
            return new PathAccessor(TypeUtils.forName(value), "nothing()");
        } catch (ClassNotFoundException e) {
            return null;
        }
    });

    @Test
    public void expBooleanNegate() {
        assertEquals("!isCondition()", parser.recompile("!condition").getAccessor());
    }

    @Test
    public void testTypes() {
        assertEquals(boolean.class, parser.parse("condition").getType());
        assertEquals(int.class, parser.parse("10 + 100").getType());
        assertEquals(double.class, parser.parse("10D + 100").getType());
        assertEquals(long.class, parser.parse("10 + 100L").getType());
        assertEquals(String.class, parser.parse("10 + message").getType());
        assertEquals(String.class, parser.parse("10 + \"String\"").getType());
        assertEquals(float.class, parser.parse("float + int").getType());
        assertEquals(double.class, parser.parse("float + double").getType());
        assertEquals(long.class, parser.parse("long + byte").getType());
        assertEquals(int.class, parser.parse("short + byte").getType());
        assertEquals(int[].class, parser.parse("[&int, 1, 2, 3, 4, 5]").getType());
        assertEquals(long[].class, parser.parse("[&long, 1, 2, 3, 4, 5]").getType());
        assertEquals(float[].class, parser.parse("[&float, 1, 2, 3, 4, 5]").getType());
        //assertEquals(float[].class, parser.parse("[1f, 2, 3, 4, 5]").getType());
        assertEquals(boolean.class, parser.parse("(10 + 20) % 2 == 0.0").getType());
    }

    @Test
    public void testFilteredTypes() {
        assertEquals(int.class, parser.parse("[&int, 1, 2, 3, 4, 5]:sum").getType());
        assertEquals(double.class, parser.parse("[&int, 1, 2, 3, 4, 5]:average").getType());
        assertEquals(String.class, parser.parse("[&int, 1, 2, 3, 4, 5]:tostr").getType());
    }

    @Test
    public void testNumberParse() {
        ExpNumber exp = (ExpNumber) parser.parse("0x64");
        Number number = exp.getNumber();
        assertEquals(100, number);

        exp = (ExpNumber) parser.parse("0");
        number = exp.getNumber();
        assertEquals(0, number);
    }

    @Test
    public void expList() {
        assertEquals("new java.lang.Object[] {1,2,3,4,5}", parser.recompile("[1, 2, 3, 4, 5]").getAccessor());
    }

    @Test
    public void testIntEquals() {
        assertEquals("(1==5)", parser.recompile("1 == 5").getAccessor());
    }

    @Test
    public void testObjectEquals() {
        assertEquals("(getMessage().equals(\"Hello world!\"))", parser.recompile("message == \"Hello world!\"").getAccessor());
        assertEquals("(!getMessage().equals(\"Hello world!\"))", parser.recompile("message != \"Hello world!\"").getAccessor());
    }

    @Test
    public void expValue() {
        assertEquals("getMessage()", parser.recompile("message").getAccessor());
    }

}
