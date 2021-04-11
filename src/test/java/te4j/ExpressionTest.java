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
import te4j.template.compiler.exp.ExpParser;
import te4j.template.compiler.path.PathAccessor;
import te4j.util.type.GenericInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author lero4ka16
 */
public class ExpressionTest {

    private final ExpParser parser = new ExpParser(
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
        assertEquals("!isCondition()", parser.recompile("!condition").getAccessor());
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
    }

    @Test
    public void expValue() {
        assertEquals("getMessage()", parser.recompile("message").getAccessor());
    }

}
