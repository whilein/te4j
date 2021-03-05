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

import com.github.lero4ka16.te4j.template.compiled.path.PathAccessor;
import com.github.lero4ka16.te4j.util.expression.*;
import com.github.lero4ka16.te4j.util.type.info.GenericInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Лера
 */
public class ExpTest {

    private final ExpParser parser = new ExpParser(
            value -> {
                if (value.equals("message")) {
                    return new PathAccessor(GenericInfo.STRING, "getMessage()", false);
                }

                return null;
            }
    );

    @Test
    public void expList() {
        Exp exp = parser.parseExpression("[1, 2, 3, 4, 5]");
        assertEquals(ExpList.class, exp.getClass());
        assertEquals("new java.lang.Object[] {1,2,3,4,5}", exp.compile());
    }

    @Test
    public void testIntEquals() {
        Exp equals = parser.parseExpression("1 == 5");
        assertEquals(ExpParentheses.class, equals.getClass());
        assertEquals("(1==5)", equals.compile());
    }

    @Test
    public void testObjectEquals() {
        Exp equals = parser.parseExpression("message == \"Hello world!\"");
        assertEquals(ExpParentheses.class, equals.getClass());
        assertEquals("(getMessage().equals(\"Hello world!\"))", equals.compile());
    }

    @Test
    public void expValue() {
        Exp exp = parser.parseExpression("message");
        assertEquals(ExpValue.class, exp.getClass());
        assertEquals(GenericInfo.STRING, exp.getObjectType());
        assertEquals("getMessage()", exp.compile());
    }

    @Test
    public void expString() {
        assertEquals(ExpString.class, parser.parseExpression("\"Hello world!\"").getClass());
    }

    @Test
    public void expNumber() {
        assertEquals(ExpNumber.class, parser.parseExpression("12345").getClass());
        assertEquals(ExpNumber.class, parser.parseExpression("-12345").getClass());
    }

}
