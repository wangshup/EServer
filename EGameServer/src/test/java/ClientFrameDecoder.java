import java.nio.ByteOrder;

import com.dd.server.io.FrameWrapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class ClientFrameDecoder extends LengthFieldBasedFrameDecoder {
    private final ByteOrder byteOrder;

    public ClientFrameDecoder(int maxFrameLength, int lengthFieldOffset) {
        super(ByteOrder.BIG_ENDIAN, maxFrameLength, lengthFieldOffset, 4, 0, 0, true);
        byteOrder = ByteOrder.BIG_ENDIAN;
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }
        return frameDecode(frame, byteOrder);
    }

    public static FrameWrapper frameDecode(ByteBuf frame, ByteOrder byteOrder) throws Exception {
        // 数据包长度 默认4byte
        /*
         *
         * 通信包依次由5段组成 1）整包长度（去除本字段长度）（字节数） 整型，占4字节 2）包头长度（字节数） 整型，占4字节
         * 3）包头内容（字节流） 由PacketHead通过protobuf序列化实现 5）包身内容（字节流）
         * 由自定义的protobuf类序列化及反序列化
         *
         */
        FrameWrapper wrapper = new FrameWrapper();
        // 取包头长度 4字节
        int packageSizeField = 4;
        long frameLength = getFrameLength(frame, 0, packageSizeField, byteOrder);
        int headSizeField = 4;
        long headLength = getFrameLength(frame, packageSizeField, headSizeField, byteOrder);
        int headLengthInt = (int) headLength;
        frame.skipBytes(packageSizeField + headSizeField);
        int readerIndex = frame.readerIndex();
        frame.retain();
        ByteBuf headFrame = frame.slice(readerIndex, headLengthInt);       
        wrapper.setHeadFrame(headFrame);
        frame.skipBytes(headLengthInt);
        readerIndex = frame.readerIndex();
        int dataLengthInt = (int) (frameLength - headSizeField - headLengthInt);
        frame.retain();
        ByteBuf dataFrame = frame.slice(readerIndex, dataLengthInt);       
        wrapper.setDataFrame(dataFrame);
        return wrapper;
    }

    @Override
    protected ByteBuf extractFrame(ChannelHandlerContext ctx, ByteBuf buffer, int index, int length) {
        return buffer.slice(index, length);
    }

    @SuppressWarnings("deprecation")
    private static long getFrameLength(ByteBuf buf, int offset, int length, ByteOrder order) {
        buf = buf.order(order);
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
}