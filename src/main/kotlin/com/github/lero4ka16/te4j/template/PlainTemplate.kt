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
package com.github.lero4ka16.te4j.template

import java.io.IOException
import java.io.OutputStream

/**
 * @author lero4ka16
 */
class PlainTemplate<BoundType>(
    private val value: ByteArray,
    private val offset: Int,
    private val length: Int
) : Template<BoundType>() {

    private val chars: String = String(value, offset, length)

    override val includes: Array<String>
        get() = arrayOf()

    override fun renderAsString(input: BoundType): String {
        return chars
    }

    override fun renderAsBytes(input: BoundType): ByteArray {
        return value.copyOfRange(offset, offset + length)
    }

    @Throws(IOException::class)
    override fun renderTo(input: BoundType, os: OutputStream) {
        os.write(value, offset, offset + length)
    }

}