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

package com.github.lero4ka16.te4j.template.output;

/**
 * @author lero4ka16
 */
public class TemplateOutputString extends TemplateOutput {

    private StringBuilder builder;

    public TemplateOutputString() {
        this(32);
    }

    public TemplateOutputString(int capacity) {
        this.builder = new StringBuilder(capacity);
    }

    @Override
    public String toString() {
        return builder.toString();
    }

    public int getLength() {
        return builder.length();
    }

    @Override
    public void put(String value) {
        builder.append(value);
    }

    @Override
    public void put(double d) {
        builder.append(d);
    }

    @Override
    public void put(float f) {
        builder.append(f);
    }

    @Override
    public void put(long value) {
        builder.append(value);
    }

    @Override
    public void put(int value) {
        builder.append(value);
    }

    @Override
    public void write(int b) {
        throw new UnsupportedOperationException();
    }

    public void write(byte[] bytes) {
        throw new UnsupportedOperationException();
    }

    public void write(byte[] bytes, int off, int len) {
        throw new UnsupportedOperationException();
    }

    public void write(String value) {
        builder.append(value);
    }

    public void reset() {
        builder.setLength(0);
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
        builder = null;
    }

}
