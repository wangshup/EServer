package com.dd.server.io;

public class HeaderByteInfo {
    private final boolean binary;
    private final boolean compressed;
    private final boolean encrypted;
    private final boolean blueBoxed;
    private final boolean bigSized;

    public HeaderByteInfo(byte headerByte) {
        this((headerByte & 0x80) > 0, (headerByte & 0x40) > 0, (headerByte & 0x20) > 0, (headerByte & 0x10) > 0,
                (headerByte & 0x8) > 0);
    }

    public HeaderByteInfo(boolean binary, boolean encrypted, boolean compressed, boolean blueBoxed, boolean bigSized) {
        this.binary = binary;
        this.compressed = compressed;
        this.encrypted = encrypted;
        this.blueBoxed = blueBoxed;
        this.bigSized = bigSized;
    }

    public byte toByte() {
        byte headerByte = 0;
        if (isBinary()) {
            headerByte |= 0x80;
        }
        if (isEncrypted()) {
            headerByte |= 0x40;
        }
        if (isCompressed()) {
            headerByte |= 0x20;
        }
        if (isBlueBoxed()) {
            headerByte |= 0x10;
        }
        if (isBigSized()) {
            headerByte |= 0x08;
        }
        return headerByte;
    }

    public boolean isBinary() {
        return this.binary;
    }

    public boolean isCompressed() {
        return this.compressed;
    }

    public boolean isEncrypted() {
        return this.encrypted;
    }

    public boolean isBlueBoxed() {
        return this.blueBoxed;
    }

    public boolean isBigSized() {
        return this.bigSized;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("\n---------------------------------------------\n");
        buf.append("Binary:  \t" + isBinary() + "\n");
        buf.append("Compressed:\t" + isCompressed() + "\n");
        buf.append("Encrypted:\t" + isEncrypted() + "\n");
        buf.append("BlueBoxed:\t" + isBlueBoxed() + "\n");
        buf.append("BigSized:\t" + isBigSized() + "\n");
        buf.append("---------------------------------------------\n");
        return buf.toString();
    }

    public static HeaderByteInfo valueOf(byte headerBytes) {
        return new HeaderByteInfo(headerBytes);
    }
}