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

import com.github.lero4ka16.te4j.expression.Expression;
import com.github.lero4ka16.te4j.expression.ExpressionList;
import com.github.lero4ka16.te4j.expression.ExpressionNumber;
import com.github.lero4ka16.te4j.expression.ExpressionParentheses;
import com.github.lero4ka16.te4j.expression.ExpressionParser;
import com.github.lero4ka16.te4j.expression.ExpressionString;
import com.github.lero4ka16.te4j.expression.ExpressionValue;
import com.github.lero4ka16.te4j.template.compiler.path.PathAccessor;
import com.github.lero4ka16.te4j.util.type.GenericInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author lero4ka16
 */
public class ExpressionTest {

    private final ExpressionParser parser = new ExpressionParser(
            value -> {
                if (value.equals("message")) {
                    return new PathAccessor(GenericInfo.STRING, "getMessage()");
                }

                if (value.equals("condition")) {
                    return new PathAccessor(GenericInfo.PRIMITIVE_BOOLEAN, "isCondition()");
                }

                return null;
            }
    );

    @Test
    public void expBooleanNegate() {
        Expression exp = parser.parseExpression("!condition");
        assertEquals(ExpressionValue.class, exp.getClass());
        assertEquals("!isCondition()", exp.compile());
    }

    @Test
    public void expList() {
        Expression exp = parser.parseExpression("[1, 2, 3, 4, 5]");
        assertEquals(ExpressionList.class, exp.getClass());
        assertEquals("new java.lang.Object[] {1,2,3,4,5}", exp.compile());
    }

    @Test
    public void testIntEquals() {
        Expression equals = parser.parseExpression("1 == 5");
        assertEquals(ExpressionParentheses.class, equals.getClass());
        assertEquals("(1==5)", equals.compile());
    }

    @Test
    public void testObjectEquals() {
        Expression equals = parser.parseExpression("message == \"Hello world!\"");
        assertEquals(ExpressionParentheses.class, equals.getClass());
        assertEquals("(getMessage().equals(\"Hello world!\"))", equals.compile());
    }

    @Test
    public void expValue() {
        Expression exp = parser.parseExpression("message");
        assertEquals(ExpressionValue.class, exp.getClass());
        assertEquals(GenericInfo.STRING, exp.getObjectType());
        assertEquals("getMessage()", exp.compile());
    }

    @Test
    public void expString() {
        assertEquals(ExpressionString.class, parser.parseExpression("\"Hello world!\"").getClass());
    }

    @Test
    public void expNumber() {
        assertEquals(ExpressionNumber.class, parser.parseExpression("12345").getClass());
        assertEquals(ExpressionNumber.class, parser.parseExpression("-12345").getClass());
    }

}
