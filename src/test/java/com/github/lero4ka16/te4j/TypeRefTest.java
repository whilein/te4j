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

import com.github.lero4ka16.te4j.util.type.ref.TypeRef;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author lero4ka16
 */
class TypeRefTest {


    @Test
    public void testTypeRef() {
        TypeRef<List<String>> listStringType = new TypeRef<List<String>>() {
        };
        assertEquals("List", listStringType.getSimpleName());
        assertEquals("java.util.List<java.lang.String>", listStringType.getCanonicalName());

        TypeRef<List<List<String>>> listListStringType = new TypeRef<List<List<String>>>() {
        };
        assertEquals("List", listListStringType.getSimpleName());
        assertEquals("java.util.List<java.util.List<java.lang.String>>",
                listListStringType.getCanonicalName());

    }

}