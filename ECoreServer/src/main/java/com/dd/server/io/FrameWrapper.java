package com.dd.server.io;

import io.netty.buffer.ByteBuf;

public class FrameWrapper {
	private ByteBuf headFrame;
	private ByteBuf dataFrame;
	private int gateSessionId;
	private byte innerCmd;

	public ByteBuf getHeadFrame() {
		return headFrame;
	}

	public void setHeadFrame(ByteBuf headFrame) {
		this.headFrame = headFrame;
	}

	public ByteBuf getDataFrame() {
		return dataFrame;
	}

	public void setDataFrame(ByteBuf dataFrame) {
		this.dataFrame = dataFrame;
	}

	public int getGateSessionId() {
		return gateSessionId;
	}

	public void setGateSessionId(int gateSessionId) {
		this.gateSessionId = gateSessionId;
	}

	public byte getInnerCmd() {
		return innerCmd;
	}

	public void setInnerCmd(byte innerCmd) {
		this.innerCmd = innerCmd;
	}

	public void release() {
		if (headFrame != null)
			headFrame.release();
		if (dataFrame != null)
			dataFrame.release();
	}
}
