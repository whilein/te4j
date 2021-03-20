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
package com.github.lero4ka16.te4j

import com.github.lero4ka16.te4j.expression.*
import com.github.lero4ka16.te4j.template.compiler.path.PathAccessor
import com.github.lero4ka16.te4j.util.type.GenericInfo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * @author lero4ka16
 */
class ExpTest {
    private val parser: ExpParser = ExpParser label@
    {
        if (it == "message") {
            return@label PathAccessor(GenericInfo.STRING, "getMessage()")
        }

        if (it == "condition") {
            return@label PathAccessor(GenericInfo.PRIMITIVE_BOOLEAN, "isCondition()")
        }

        null
    }

    @Test
    fun expBooleanNegate() {
        val exp: Exp = parser.parseExpression("!condition")
        assertEquals(ExpValue::class.java, exp.javaClass)
        assertEquals("!isCondition()", exp.compile())
    }

    @Test
    fun expList() {
        val exp: Exp = parser.parseExpression("[1, 2, 3, 4, 5]")
        assertEquals(ExpList::class.java, exp.javaClass)
        assertEquals("new java.lang.Object[] {1,2,3,4,5}", exp.compile())
    }

    @Test
    fun testIntEquals() {
        val equals: Exp = parser.parseExpression("1 == 5")
        assertEquals(ExpParentheses::class.java, equals.javaClass)
        assertEquals("(1==5)", equals.compile())
    }

    @Test
    fun testObjectEquals() {
        val equals: Exp = parser.parseExpression("message == \"Hello world!\"")
        assertEquals(ExpParentheses::class.java, equals.javaClass)
        assertEquals("(getMessage().equals(\"Hello world!\"))", equals.compile())
    }

    @Test
    fun expValue() {
        val exp: Exp = parser.parseExpression("message")
        assertEquals(ExpValue::class.java, exp.javaClass)
        assertEquals(GenericInfo.STRING, exp.objectType)
        assertEquals("getMessage()", exp.compile())
    }

    @Test
    fun expString() {
        assertEquals(ExpString::class.java, parser.parseExpression("\"Hello world!\"").javaClass)
    }

    @Test
    fun expNumber() {
        assertEquals(ExpNumber::class.java, parser.parseExpression("12345").javaClass)
        assertEquals(ExpNumber::class.java, parser.parseExpression("-12345").javaClass)
    }
}