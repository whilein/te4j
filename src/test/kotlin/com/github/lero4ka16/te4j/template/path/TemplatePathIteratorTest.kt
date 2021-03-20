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
package com.github.lero4ka16.te4j.template.path

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * @author lero4ka16
 */
internal class TemplatePathIteratorTest {
    @Test
    fun testOnlyOneElement() {
        val iterator = TemplatePathIterator("element")
        Assertions.assertEquals("element", iterator.next())
        Assertions.assertFalse(iterator.hasNext())
        iterator.previous()
        Assertions.assertEquals("element", iterator.next())
        Assertions.assertFalse(iterator.hasNext())
    }

    @Test
    fun testIterator() {
        val iterator = TemplatePathIterator("a.b.c.d.e.f")
        Assertions.assertEquals("a", iterator.next())
        iterator.previous()
        Assertions.assertEquals("a", iterator.next())
        Assertions.assertEquals("b", iterator.next())
        Assertions.assertEquals("c", iterator.next())
        Assertions.assertEquals("d", iterator.next())
        iterator.previous()
        Assertions.assertEquals("d", iterator.next())
        Assertions.assertEquals("e", iterator.next())
        Assertions.assertEquals("f", iterator.next())
        Assertions.assertFalse(iterator.hasNext())
    }
}