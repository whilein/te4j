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

import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author lero4ka16
 */
public abstract class TemplateOutput {

    private static final byte[] ARRAY_DELIMITER = ", ".getBytes(StandardCharsets.UTF_8);

    private static final int[] INT_UNITS = new int[]{
            1, 10, 100,
            1_000, 10_000, 100_000,
            1_000_000, 10_000_000, 100_000_000,
            1_000_000_000
    };

    private static final long[] LONG_UNITS = new long[]{
            1L, 10L, 100L,
            1_000L, 10_000L, 100_000L,
            1_000_000L, 10_000_000L, 100_000_000L,
            1_000_000_000L, 10_000_000_000L, 100_000_000_000L,
            1_000_000_000_000L, 10_000_000_000_000L, 100_000_000_000_000L,
            1_000_000_000_000_000L, 10_000_000_000_000_000L, 100_000_000_000_000_000L,
            1_000_000_000_000_000_000L
    };

    public void put(String value) {
        write(value.getBytes(StandardCharsets.UTF_8));
    }

    private int longLength(long n) {
        if (n < 1000000000) {
            if (n < 10000) {
                if (n < 1000) {
                    if (n < 100) {
                        if (n < 10) {
                            return 1;
                        } else {
                            return 2;
                        }
                    } else {
                        return 3;
                    }
                } else {
                    return 4;
                }
            } else {
                if (n < 100000) {
                    return 5;
                } else {
                    if (n < 1000000) {
                        return 6;
                    } else {
                        if (n < 10000000) {
                            return 7;
                        } else {
                            if (n < 100000000) {
                                return 8;
                            } else {
                                return 9;
                            }
                        }
                    }
                }
            }
        } else {
            if (n < 100000000000000L) {
                if (n < 10000000000L) {
                    return 10;
                } else {
                    if (n < 100000000000L) {
                        return 11;
                    } else {
                        if (n < 1000000000000L) {
                            return 12;
                        } else {
                            if (n < 10000000000000L) {
                                return 13;
                            } else {
                                return 14;
                            }
                        }
                    }
                }
            } else {
                if (n < 1000000000000000L) {
                    return 15;
                } else {
                    if (n < 10000000000000000L) {
                        return 16;
                    } else {
                        if (n < 100000000000000000L) {
                            return 17;
                        } else {
                            if (n < 1000000000000000000L) {
                                return 18;
                            } else {
                                return 19;
                            }
                        }
                    }
                }
            }
        }
    }

    // https://www.baeldung.com/java-number-of-digits-in-int#5-divide-and-conquer
    private int intLength(int value) {
        if (value < 100000) {
            if (value < 100) {
                if (value < 10) {
                    return 1;
                } else {
                    return 2;
                }
            } else {
                if (value < 1000) {
                    return 3;
                } else {
                    if (value < 10000) {
                        return 4;
                    } else {
                        return 5;
                    }
                }
            }
        } else {
            if (value < 10000000) {
                if (value < 1000000) {
                    return 6;
                } else {
                    return 7;
                }
            } else {
                if (value < 100000000) {
                    return 8;
                } else {
                    if (value < 1000000000) {
                        return 9;
                    } else {
                        return 10;
                    }
                }
            }
        }
    }

    public void put(Object object) {
        Class<?> cls = object.getClass();

        if (cls.isArray()) {
            Class<?> type = cls.getComponentType();

            if (type.isPrimitive()) {
                if (type == byte.class) {
                    put(Arrays.toString((byte[]) object));
                } else if (type == short.class) {
                    put(Arrays.toString((short[]) object));
                } else if (type == int.class) {
                    put(Arrays.toString((int[]) object));
                } else if (type == long.class) {
                    put(Arrays.toString((long[]) object));
                } else if (type == float.class) {
                    put(Arrays.toString((float[]) object));
                } else if (type == double.class) {
                    put(Arrays.toString((double[]) object));
                } else if (type == boolean.class) {
                    put(Arrays.toString((boolean[]) object));
                } else if (type == char.class) {
                    put(Arrays.toString((char[]) object));
                }
            } else {
                int len = Array.getLength(object);
                write('[');

                for (int i = 0; i < len; i++) {
                    if (i != 0) write(ARRAY_DELIMITER);

                    Object element = Array.get(object, i);
                    put(element);
                }

                write(']');
            }
        } else {
            put(String.valueOf(object));
        }
    }

    public void put(double d) {
        put(String.valueOf(d));
    }

    public void put(float f) {
        put(String.valueOf(f));
    }

    public void put(long value) {
        boolean negative = value < 0;

        if (negative) {
            write('-');
            value = -value;
        }

        int length = longLength(value);

        for (int i = length - 1; i >= 0; i--) {
            writeDigit((int) (value / LONG_UNITS[i] % 10L));
        }
    }

    public void put(int value) {
        if (value == 0) {
            write('0');
            return;
        }

        boolean negative = value < 0;

        if (negative) {
            write('-');
            value = -value;
        }

        int length = intLength(value);

        for (int i = length - 1; i >= 0; i--) {
            writeDigit(value / INT_UNITS[i] % 10);
        }
    }

    private void writeDigit(int digit) {
        write('0' + digit);
    }

    public abstract void write(byte[] bytes);

    public abstract void write(byte[] bytes, int off, int len);

    public abstract void flush();

    public abstract void close();

    public abstract void write(int ch);

}
