import com.dd.server.io.FrameWrapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ClientFrameEncoder extends MessageToByteEncoder<FrameWrapper> {    
    public ClientFrameEncoder() {       
    }

    protected void encode(ChannelHandlerContext ctx, FrameWrapper msg, ByteBuf out) throws Exception {
        frameEncode(msg, out);
    }

    public static void frameEncode(FrameWrapper msg, ByteBuf out) {        
        int headReadableBytes = msg.getHeadFrame().readableBytes();
        int dataReadableBytes = msg.getDataFrame().readableBytes();
        out.ensureWritable(4 + 4 + headReadableBytes + dataReadableBytes);
        out.writeInt(headReadableBytes + dataReadableBytes + 4);
        out.writeInt(headReadableBytes);
        byte[] data = new byte[headReadableBytes];
        msg.getHeadFrame().readBytes(data);
        out.writeBytes(data);
        data = new byte[dataReadableBytes];
        msg.getDataFrame().readBytes(data);
        out.writeBytes(data);        
    }
}