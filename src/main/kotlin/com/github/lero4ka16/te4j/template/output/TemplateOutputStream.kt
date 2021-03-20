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
package com.github.lero4ka16.te4j.template.output

import com.github.lero4ka16.te4j.template.exception.TemplateException
import java.io.IOException
import java.io.OutputStream

/**
 * @author lero4ka16
 */
class TemplateOutputStream(private val os: OutputStream) : TemplateOutput() {
    override fun write(bytes: ByteArray) {
        try {
            os.write(bytes)
        } catch (e: IOException) {
            throw TemplateException("Cannot write to output stream", e)
        }
    }

    override fun write(bytes: ByteArray, off: Int, len: Int) {
        try {
            os.write(bytes, off, len)
        } catch (e: IOException) {
            throw TemplateException("Cannot write to output stream", e)
        }
    }

    override fun flush() {
        try {
            os.flush()
        } catch (e: IOException) {
            throw TemplateException("Cannot flush output stream", e)
        }
    }

    override fun close() {
        try {
            os.flush()
        } catch (e: IOException) {
            throw TemplateException("Cannot close output stream", e)
        }
    }

    override fun write(ch: Int) {
        try {
            os.write(ch)
        } catch (e: IOException) {
            throw TemplateException("Cannot write to output stream", e)
        }
    }
}