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

package com.github.lero4ka16.te4j.util.reader;

/**
 * @author lero4ka16
 */
public abstract class DataReader {

    protected int position;

    public static boolean isSpace(int ch) {
        return ch <= 0x20;
    }

    public abstract String substring(int start, int end);

    public abstract int get(int position);

    public abstract int getLength();

    public boolean isReadable() {
        return position != getLength();
    }

    public boolean move(int value) {
        int pos = position();

        for (; ; ) {
            int ch = read();

            if (ch == -1) {
                position(pos);
                return false;
            }

            if (ch != value) {
                continue;
            } else {
                position--;
            }

            return true;
        }
    }

    public boolean moveNonWhitespace() {
        int pos = position();

        for (; ; ) {
            int ch = read();

            if (ch == -1) {
                position(pos);
            } else if (isSpace(ch)) {
                continue;
            } else {
                position--;
            }

            return ch != -1;
        }
    }

    public int position() {
        return position;
    }

    public void next() {
        if (position != getLength()) position++;
    }

    public int readNonWhitespace() {
        return moveNonWhitespace() ? read() : -1;
    }

    public String readString() {
        return substring(position, getLength());
    }

    public int read() {
        return isReadable() ? get(position++) : -1;
    }

    public void position(int position) {
        this.position = position;
    }

    public void roll() {
        position--;
    }
}
