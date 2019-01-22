package com.dd.server.io;

import com.dd.server.exceptions.ServerRuntimeException;
import com.dd.server.utils.ByteUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class ServerFrameCompressor implements FrameCompressor {
    public static final int MAX_SIZE_FOR_COMPRESSION = 1000000;
    private static final int MAX_LOOP_COUNT = 10000;
    private static final int compressionBufferSize = 512;

    @Override
    public byte[] compress(byte[] data) throws Exception {
        int readableBytes = data.length;
        if (readableBytes > MAX_SIZE_FOR_COMPRESSION) {
            return data;
        } else {
            Deflater compressor = new Deflater();
            compressor.setInput(data);
            compressor.finish();
            ByteArrayOutputStream bos = null;
            try {
                bos = new ByteArrayOutputStream(data.length);
                byte[] buf = new byte[compressionBufferSize];
                int loopCount = 0;
                while (!compressor.finished() && loopCount++ < MAX_LOOP_COUNT) {
                    int count = compressor.deflate(buf);
                    bos.write(buf, 0, count);
                }
                if (loopCount >= MAX_LOOP_COUNT) {
                    throw new ServerRuntimeException(String.format("compression loop count reach max [%d]. data detail: %s", MAX_LOOP_COUNT, ByteUtils.fullHexDump(data)));
                }
                return bos.toByteArray();
            } finally {
                if (bos != null) {
                    bos.close();
                }
            }
        }
    }

    @Override
    public byte[] uncompress(byte[] data) throws Exception {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
        byte[] buf = new byte[compressionBufferSize];
        try {
            int loopCount = 0;
            while (!inflater.finished() && loopCount++ < MAX_LOOP_COUNT) {
                int count = inflater.inflate(buf);
                if (count < 1 && inflater.needsInput()) {
                    throw new IOException("Bad Compression Format! Packet will be dropped");
                }
                bos.write(buf, 0, count);
            }
            if (loopCount >= MAX_LOOP_COUNT) {
                throw new ServerRuntimeException(String.format("decompression loop count reach max [%d]. data detail: %s", MAX_LOOP_COUNT, ByteUtils.fullHexDump(data)));
            }
            return bos.toByteArray();
        } finally {
            bos.close();
        }
    }
}
