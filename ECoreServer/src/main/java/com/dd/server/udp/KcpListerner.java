/**
 *
 */
package com.dd.server.udp;

import io.netty.buffer.ByteBuf;

public interface KcpListerner {

    /**
     * kcp message
     *
     * @param bb
     *            the data
     * @param kcp
     */
    void handleReceive(ByteBuf bb, KcpOnUdp kcp);

    /**
     *
     * kcp异常，之后此kcp就会被关闭
     *
     * @param ex
     *            异常
     * @param kcp
     *            发生异常的kcp，null表示非kcp错误
     */
    void handleException(Throwable ex, KcpOnUdp kcp);

    /**
     * 关闭
     *
     * @param kcp
     */
    void handleClose(KcpOnUdp kcp);
}
