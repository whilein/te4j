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
import te4j.template.path.DefaultTemplatePathIterator;
import te4j.template.path.TemplatePathIterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * @author lero4ka16
 */
class TemplatePathIteratorTest {

    @Test
    public void testOnlyOneElement() {
        TemplatePathIterator iterator = DefaultTemplatePathIterator.create("element");
        assertFalse(iterator.hasPrev());
        assertEquals("element", iterator.next());
        assertFalse(iterator.hasNext());
        iterator.prev();
        assertFalse(iterator.hasPrev());
        assertEquals("element", iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testIterator() {
        TemplatePathIterator iterator = DefaultTemplatePathIterator.create("a.b.c.d.e.f");
        assertFalse(iterator.hasPrev());
        assertEquals("a", iterator.next());
        iterator.prev();
        assertFalse(iterator.hasPrev());
        assertEquals("a", iterator.next());
        assertEquals("b", iterator.next());
        assertEquals("c", iterator.next());
        assertEquals("d", iterator.next());
        iterator.prev();
        assertEquals("d", iterator.next());
        assertEquals("e", iterator.next());
        assertEquals("f", iterator.next());
        assertFalse(iterator.hasNext());
    }

}