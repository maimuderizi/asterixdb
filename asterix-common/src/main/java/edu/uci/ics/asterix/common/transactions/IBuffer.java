/*
 * Copyright 2009-2013 by The Regents of the University of California
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * you may obtain a copy of the License from
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.uci.ics.asterix.common.transactions;

import java.nio.ByteBuffer;

/**
 * Represents a wrapper over @see ByteBuffer supporting some custom APIs for
 * transaction support.
 */
public interface IBuffer {

    public byte getByte(int offset);

    public int getSize();

    public byte getByte();

    public void getBytes(byte[] bytes, int start, int size);

    public int readInt();

    public int readInt(int offset);

    public long readLong(int offset);

    public void put(byte b);

    public void put(int offset, byte b);

    public void put(byte[] bytes, int start, int length);

    public void put(byte[] bytes);

    public void writeInt(int value);

    public void writeLong(long value);

    public void writeInt(int offset, int value);

    public void writeLong(int offset, long value);

    public byte[] getArray();

    public void erase();

    public ByteBuffer getByteBuffer();

}
