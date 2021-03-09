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

package com.github.lero4ka16.te4j.template;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * @author lero4ka16
 */
public final class PlainTemplate<BoundType> extends Template<BoundType> {

    private final byte[] value;

    private final int offset;
    private final int length;

    private final String chars;

    public PlainTemplate(byte[] value, int offset, int length) {
        this.value = value;
        this.offset = offset;
        this.length = length;

        this.chars = new String(value, offset, length);
    }

    @Override
    public @NotNull String[] getIncludes() {
        return new String[0];
    }

    @Override
    public @NotNull String renderAsString(@NotNull BoundType object) {
        return chars;
    }

    @Override
    public byte @NotNull [] renderAsBytes(@NotNull BoundType object) {
        return Arrays.copyOfRange(value, offset, offset + length);
    }

    @Override
    public void render(@NotNull BoundType object, @NotNull OutputStream os) throws IOException {
        os.write(value, offset, offset + length);
    }

}
