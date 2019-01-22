package com.dd.server.io;

public interface FrameCompressor {
	byte[] compress(byte[] paramArrayOfByte) throws Exception;

	byte[] uncompress(byte[] paramArrayOfByte) throws Exception;
}