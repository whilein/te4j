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
package com.github.lero4ka16.te4j.util.type.ref

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * @author lero4ka16
 */
internal class TypeRefTest {
    @Test
    fun testTypeRef() {
        val listStringType: TypeRef<List<String>> = object : TypeRef<List<String>>() {}
        Assertions.assertEquals("List", listStringType.simpleName)
        Assertions.assertEquals("java.util.List<java.lang.String>", listStringType.canonicalName)

        val listListStringType: TypeRef<List<List<String>>> = object : TypeRef<List<List<String>>>() {}
        Assertions.assertEquals("List", listListStringType.simpleName)
        Assertions.assertEquals("java.util.List<java.util.List<java.lang.String>>", listListStringType.canonicalName)
    }
}