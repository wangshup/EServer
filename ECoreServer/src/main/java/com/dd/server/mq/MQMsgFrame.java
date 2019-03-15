package com.dd.server.mq;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;

public class MQMsgFrame {
    private ByteBuf headFrame;
    private ByteBuf dataFrame;

    public static MQMsgFrame frameDecode(ByteBuf frame) throws Exception {
        // 数据包长度 默认4byte
        // 通信包依次由5段组成
        // 1）整包长度（去除本字段长度）（字节数） 整型，占4字节
        // 2）包头长度（字节数） 整型，占4字节
        // 3）包头内容（字节流） 由PacketHead通过protobuf序列化实现
        // 4）包身内容（字节流） 由自定义的protobuf类序列化及反序列化

        MQMsgFrame wrapper = new MQMsgFrame();
        // 取包头长度 4字节
        int packageSizeField = 4;
        long frameLength = getFrameLength(frame, 0, packageSizeField);
        int headSizeField = 4;
        long headLength = getFrameLength(frame, packageSizeField, headSizeField);
        int headLengthInt = (int) headLength;
        frame.skipBytes(packageSizeField + headSizeField);
        int readerIndex = frame.readerIndex();
        ByteBuf headFrame = frame.retainedSlice(readerIndex, headLengthInt);
        wrapper.setHeadFrame(headFrame);
        frame.skipBytes(headLengthInt);
        readerIndex = frame.readerIndex();
        int dataLengthInt = (int) (frameLength - headSizeField - headLengthInt);
        ByteBuf dataFrame = frame.retainedSlice(readerIndex, dataLengthInt);
        wrapper.setDataFrame(dataFrame);
        return wrapper;
    }

    public static long getFrameLength(ByteBuf buf, int offset, int length) {
        long frameLength;
        switch (length) {
            case 1:
                frameLength = buf.getUnsignedByte(offset);
                break;
            case 2:
                frameLength = buf.getUnsignedShort(offset);
                break;
            case 3:
                frameLength = buf.getUnsignedMedium(offset);
                break;
            case 4:
                frameLength = buf.getUnsignedInt(offset);
                break;
            case 8:
                frameLength = buf.getLong(offset);
                break;
            default:
                throw new DecoderException("unsupported lengthFieldLength: (expected: 1, 2, 3, 4, or 8)");
        }
        return frameLength;
    }

    public static void frameEncode(MQMsgFrame msg, ByteBuf out) {
        int headReadableBytes = msg.getHeadFrame().readableBytes();
        int dataReadableBytes = msg.getDataFrame().readableBytes();
        out.ensureWritable(4 + 4 + headReadableBytes + dataReadableBytes);
        out.writeInt(headReadableBytes + dataReadableBytes + 4);
        out.writeInt(headReadableBytes);
        out.writeBytes(msg.getHeadFrame());
        out.writeBytes(msg.getDataFrame());
    }

    public static MQMsgFrame responseEncode(MQMsg msg) {
        MqProto.MsgHead head = msg.getHead();
        ByteBuf headBuffer = Unpooled.wrappedBuffer(head.toByteArray());
        byte[] data = new byte[0];
        if (msg.getBody() != null) {
            if (msg.getBody() instanceof com.google.protobuf.Message)
                data = ((com.google.protobuf.Message) msg.getBody()).toByteArray();
            else data = msg.getBody();
        }
        ByteBuf dataBuffer = Unpooled.wrappedBuffer(data);
        MQMsgFrame wrapper = new MQMsgFrame();
        wrapper.setHeadFrame(headBuffer);
        wrapper.setDataFrame(dataBuffer);
        return wrapper;
    }

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

    public void retain() {
        if (headFrame != null) headFrame.retain();
        if (dataFrame != null) dataFrame.retain();
    }

    public void release() {
        if (headFrame != null) headFrame.release();
        if (dataFrame != null) dataFrame.release();
    }
}
