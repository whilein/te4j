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

package com.github.lero4ka16.te4j.util.hash;

import java.util.Arrays;

/**
 * @author lero4ka16
 */
public abstract class Hash {

    private final int hash;

    public Hash(int hash) {
        this.hash = hash;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    public static Hash forArray(byte[] array) {
        return new Bytes(array);
    }

    private static class Bytes extends Hash {

        private final byte[] value;

        public Bytes(byte[] value) {
            super(Arrays.hashCode(value));

            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Bytes)) return false;

            Bytes that = (Bytes) obj;
            return Arrays.equals(value, that.value);
        }

    }

}
