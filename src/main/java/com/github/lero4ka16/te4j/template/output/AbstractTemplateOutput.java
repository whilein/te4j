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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;

/**
 * @author lero4ka16
 */
public abstract class AbstractTemplateOutput implements TemplateOutput {

    static final int[] INT_UNITS = new int[]{
            1, 10, 100,
            1_000, 10_000, 100_000,
            1_000_000, 10_000_000, 100_000_000,
            1_000_000_000
    };

    static final long[] LONG_UNITS = new long[]{
            1L, 10L, 100L,
            1_000L, 10_000L, 100_000L,
            1_000_000L, 10_000_000L, 100_000_000L,
            1_000_000_000L, 10_000_000_000L, 100_000_000_000L,
            1_000_000_000_000L, 10_000_000_000_000L, 100_000_000_000_000L,
            1_000_000_000_000_000L, 10_000_000_000_000_000L, 100_000_000_000_000_000L,
            1_000_000_000_000_000_000L
    };

    int longLength(long value) {
        if (value < 1000000000) {
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
                        return 9;
                    }
                }
            }
        } else {
            if (value < 100000000000000L) {
                if (value < 100000000000L) {
                    if (value < 10000000000L) {
                        return 10;
                    } else {
                        return 11;
                    }
                } else {
                    if (value < 1000000000000L) {
                        return 12;
                    } else {
                        if (value < 10000000000000L) {
                            return 13;
                        } else {
                            return 14;
                        }
                    }
                }
            } else {
                if (value < 10000000000000000L) {
                    if (value < 1000000000000000L) {
                        return 15;
                    } else {
                        return 16;
                    }
                } else {
                    if (value < 100000000000000000L) {
                        return 17;
                    } else {
                        if (value < 1000000000000000000L) {
                            return 18;
                        } else {
                            return 19;
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

    @Override
    public void put(@NotNull String value) {
        write(value.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void put(@Nullable Object object) {
        put(String.valueOf(object));
    }

    @Override
    public void put(double d) {
        put(String.valueOf(d));
    }

    @Override
    public void put(float f) {
        put(String.valueOf(f));
    }

    @Override
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

    @Override
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

    protected void writeDigit(int digit) {
        write('0' + digit);
    }

}
