package com.dd.server.utils;

import java.io.IOException;

import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Message;

public final class BytesHolder {
    private final ThreadLocal<CompositeByte> byteHolder = new ThreadLocal<CompositeByte>() {
        @Override
        protected CompositeByte initialValue() {
            CompositeByte bytes = new CompositeByte();
            bytes.data = new byte[BUFF_SIZE_INIT];
            return bytes;
        }
    };
    private static final int BUFF_SIZE_INIT = 64;
    private static final int BUFF_SIZE_INCR = 512;
    private static final int BUFF_SIZE_MAX = 1024 * 1024 * 10;

    public CompositeByte getCompositeByte(int len) {
        CompositeByte bytes = byteHolder.get();
        int l = bytes.data.length;
        if (l < len) {
            do {
                if (l < BUFF_SIZE_MAX)
                    l <<= 1;
                else
                    l += BUFF_SIZE_INCR;
            } while (l < len);
            bytes.data = new byte[l];
        }
        bytes.length = len;
        return bytes;
    }

    public static class CompositeByte {
        public byte[] data;
        public int length;
    }

    public CompositeByte toByteArray(Message msg) {
        try {
            CompositeByte bytes = getCompositeByte(msg.getSerializedSize());
            CodedOutputStream output = CodedOutputStream.newInstance(bytes.data, 0, bytes.length);
            msg.writeTo(output);
            output.checkNoSpaceLeft();
            return bytes;
        } catch (IOException e) {
            throw new RuntimeException("Serializing to a byte array threw an IOException (should never happen).", e);
        }
    }
}