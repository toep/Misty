package com.misty.engine.network;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class ByteBufferBackedOutputStream extends OutputStream {
    ByteBuffer buf;

    ByteBufferBackedOutputStream(ByteBuffer buf) {
        this.buf = buf;
    }

    void write(byte b) throws IOException {
        buf.put(b);
    }

    public void write(int b) throws IOException {
        buf.putInt(b);
    }

    public void write(byte[] bytes, int off, int len)
            throws IOException {
        buf.put(bytes, off, len);
    }

    void clearBuffer() {
        buf.clear();
    }

}