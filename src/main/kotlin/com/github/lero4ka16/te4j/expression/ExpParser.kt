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
package com.github.lero4ka16.te4j.expression

import com.github.lero4ka16.te4j.template.compiler.path.PathAccessor
import com.github.lero4ka16.te4j.util.io.CharsReader
import com.github.lero4ka16.te4j.util.io.DataReader
import java.util.*
import java.util.function.Function

class ExpParser(private val mapper: Function<String, PathAccessor?>) {
    fun parseExpression(value: String): Exp {
        val reader: DataReader = CharsReader("($value)")
        val exp = parseNext(null, reader, 0)

        if (exp is ExpParentheses) {
            if (exp.canOpenParentheses()) {
                return exp.openParentheses()
            }
        }

        return exp!!
    }

    private fun parseNext(prev: Exp?, reader: DataReader, eof: Int): Exp? {
        var closed = false

        while (reader.isReadable) {
            var ch = reader.read()
            var token: Exp? = null
            if (ch == eof) {
                closed = true
                break
            } else if (ch == '('.toInt()) {
                token = parseParentheses(reader)
            } else if (ch == '"'.toInt() || ch == '\''.toInt()) {
                token = parseString(reader, ch)
            } else if (ch == '['.toInt()) {
                token = parseList(reader)
            } else if (ch == '#'.toInt()) {
                token = parseEnum(parseText(reader, eof))
            } else if (ch != ' '.toInt() && ch != ','.toInt()) {
                val readable = reader.isReadable
                var nextDigit: Boolean
                var logicalNeg: Boolean
                if (readable) {
                    val next = reader.read()
                    nextDigit = next >= '0'.toInt() && next <= '9'.toInt()
                    logicalNeg = ch == '!'.toInt() && next != '='.toInt()
                    reader.roll()
                } else {
                    logicalNeg = false
                    nextDigit = logicalNeg
                }
                reader.roll()
                token = if (Operator.isOperator(ch) && prev !is ExpOperator && !nextDigit
                    && !logicalNeg
                ) {
                    parseOperator(reader)
                } else {
                    parseValue(parseText(reader, eof))
                }
            }

            if (token != null) {
                if (reader.isReadable) {
                    ch = reader.read()
                    while (ch == ':'.toInt()) {
                        val filter = parseText(reader, eof)
                        check(token !is ExpOperator) { "Unexpected filter" }
                        token.addFilter(filter)
                        ch = reader.read()
                    }
                    reader.roll()
                }
                return token
            }
        }

        check(closed) { "End of file" }
        return null
    }

    private fun parseParentheses(reader: DataReader): Exp {
        val tokens: MutableList<Exp> = ArrayList()
        var prev: Exp? = null
        var token: Exp?

        while (parseNext(prev, reader, ')'.toInt()).also { token = it } != null) {
            tokens.add(token!!)
            prev = token
        }

        return if (tokens.size == 1) tokens[0] else ExpParentheses(tokens.toTypedArray())
    }

    private fun getClass(name: String): Class<*>? {
        return when (name) {
            "byte" -> Byte::class.javaPrimitiveType
            "short" -> Short::class.javaPrimitiveType
            "int" -> Int::class.javaPrimitiveType
            "long" -> Long::class.javaPrimitiveType
            "float" -> Float::class.javaPrimitiveType
            "char" -> Char::class.javaPrimitiveType
            "boolean" -> Boolean::class.javaPrimitiveType
            "double" -> Double::class.javaPrimitiveType
            else -> try {
                Class.forName(name)
            } catch (e: ClassNotFoundException) {
                throw RuntimeException(e)
            }
        }
    }

    private fun parseList(reader: DataReader): Exp {
        var type: Class<*>? = Any::class.java
        val pos = reader.position()
        var ch = reader.read()
        if (ch == '&'.toInt()) {
            while (true) {
                ch = reader.read()
                if (ch == ','.toInt() || ch == ']'.toInt()) {
                    type = getClass(reader.substring(pos + 1, reader.position() - 1).trim { it <= ' ' })

                    if (ch == ']'.toInt()) {
                        return ExpList(type!!, arrayOf())
                    }

                    break
                }
            }
        } else {
            reader.roll()
        }
        val tokens: MutableList<Exp> = ArrayList()
        var prev: Exp? = null
        var token: Exp

        while (parseNext(prev, reader, ']'.toInt()).also { token = it!! } != null) {
            tokens.add(token)

            prev = token
            ch = reader.readNonWhitespace()

            if (ch == ']'.toInt()) break

            check(ch == ','.toInt()) { "Unexpected char: " + ch.toChar() }
        }

        return ExpList(type!!, tokens.toTypedArray())
    }

    private fun parseString(reader: DataReader, quote: Int): Exp {
        val start = reader.position()
        while (reader.isReadable) {
            val ch = reader.read()
            if (ch == quote) break
        }
        return ExpString(reader.substring(start, reader.position() - 1))
    }

    private fun parseEnum(text: String): Exp {
        return ExpEnum(text)
    }

    private fun parseOperator(reader: DataReader): Exp {
        val startPos = reader.position()
        var types = Operator.VALUES
        var pos = 0
        while (true) {
            val ch = reader.read()
            if (ch == -1) break
            if (ch == ' '.toInt()) {
                reader.roll()
                break
            }
            val newTypes = Operator.filter(types, pos++, ch)
            if (newTypes.isEmpty()) {
                reader.roll()
                break
            }
            types = newTypes
        }
        val op = reader.substring(startPos, reader.position())

        val type = Operator.search(types, op)
            .orElseThrow { IllegalStateException("Unknown operator: $op") }

        return ExpOperator(type)
    }

    private fun parseText(reader: DataReader, eof: Int): String {
        val start = reader.position()

        while (true) {
            val ch = reader.read()

            if (ch == -1) break

            if (ch == eof || ch == ' '.toInt() || ch == ':'.toInt() || ch == ','.toInt()) {
                reader.roll()
                break
            }

            if (ExpNegation.byChar(ch) !== ExpNegation.NONE
                && start == reader.position() - 1
            ) { // negation
                continue
            }
            if (Operator.isOperator(ch)) {
                reader.roll()
                break
            }
        }

        return reader.substring(start, reader.position())
    }

    private fun parseValue(value: String): Exp {
        var idx = 0
        val negation = ExpNegation.byChar(value[idx])

        if (negation !== ExpNegation.NONE) {
            idx++
        }

        if (value[idx] in '0'..'9') {
            return ExpNumber(value)
        }

        val text = value.substring(idx)
        val accessor = mapper.apply(text) ?: throw IllegalStateException("Accessor not found: $text")

        return ExpValue(accessor, negation)
    }
}