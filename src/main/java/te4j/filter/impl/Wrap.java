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

/**
 * @author lero4ka16
 */
public final class Wrap implements Filter {

    @Override
    public String getName() {
        return "wrap";
    }

    public static Byte process(byte i) {
        return i;
    }

    public static Character process(char i) {
        return i;
    }

    public static Boolean process(boolean i) {
        return i;
    }

    public static Short process(short i) {
        return i;
    }

    public static Double process(double i) {
        return i;
    }

    public static Integer process(int i) {
        return i;
    }

    public static Long process(long i) {
        return i;
    }

    public static Float process(float i) {
        return i;
    }


}