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

package te4j.filter.impl;

import te4j.filter.Filter;

import java.util.Collection;

/**
 * @author lero4ka16
 */
public final class Average implements Filter {
    @Override
    public String getName() {
        return "average";
    }

    public static double process(byte[] value) {
        return value.length == 0 ? 0 : (double) Sum.process(value) / value.length;
    }

    public static double process(short[] value) {
        return value.length == 0 ? 0 : (double) Sum.process(value) / value.length;
    }

    public static double process(int[] value) {
        return value.length == 0 ? 0 : (double) Sum.process(value) / value.length;
    }

    public static double process(long[] value) {
        return value.length == 0 ? 0 : (double) Sum.process(value) / value.length;
    }

    public static double process(float[] value) {
        return value.length == 0 ? 0 : Sum.process(value) / value.length;
    }

    public static double process(double[] value) {
        return value.length == 0 ? 0 : Sum.process(value) / value.length;
    }

    public static double process(Object[] value) {
        return value.length == 0 ? 0 : Sum.process(value) / value.length;
    }

    public static double process(Collection<?> value) {
        return value.isEmpty() ? 0 : Sum.process(value) / value.size();
    }

}
