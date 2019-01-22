package com.dd.server.io;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ServerFrameEncoder extends MessageToByteEncoder<FrameWrapper> {
    //private static final Logger logger = LoggerFactory.getLogger(ServerFrameEncoder.class);
    //private static final FrameCompressor compressor = new ServerFrameCompressor();
    //private int protocolCompressionThreshold;

    public ServerFrameEncoder() {
        //this.protocolCompressionThreshold = Server.getInstance().getConfiguration().getInt("protocol.compression.threshold");
    }

    public static void frameEncode(FrameWrapper msg, ByteBuf out) {
        int headerLength = msg.getHeadFrame().readableBytes();
        int bodyLength = msg.getDataFrame().readableBytes();

        //byte[] compressed = null;
        //boolean isEncrypted = false;
        //boolean isCompressed = false;
        //try {
        //    if ((!isEncrypted) && (readableBytes > this.protocolCompressionThreshold)) {
        //        byte[] data = new byte[readableBytes];
        //        int oldMsgReadIndex = msg.readerIndex();
        //        msg.readBytes(data);
        //        // !!!!!大于1MB 就不压缩了!!!!! compressed = compressor.compress(data);
        //          if (data != compressed) {
        //            isCompressed = true;
        //            readableBytes = compressed.length;
        //        } else {
        //            msg.readerIndex(oldMsgReadIndex);
        //        }
        //    }
        //} catch (Exception e) {
        //    logger.error("got error on compress message ", e);
        //}


        //HeaderByteInfo headerByteInfo = new HeaderByteInfo(true, isEncrypted, isCompressed, false, sizeBytes > 2);
        //byte header = headerByteInfo.toByte();

        out.ensureWritable(4 + 4 + headerLength + bodyLength);
        out.writeInt(headerLength + bodyLength + 4);
        out.writeInt(headerLength);
        byte[] data = new byte[headerLength];
        msg.getHeadFrame().readBytes(data);
        out.writeBytes(data);
        data = new byte[bodyLength];
        msg.getDataFrame().readBytes(data);
        out.writeBytes(data);
    }

    protected void encode(ChannelHandlerContext ctx, FrameWrapper msg, ByteBuf out) throws Exception {
        frameEncode(msg, out);
    }
}