package morgan.connection;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import morgan.logic.Connection;
import morgan.support.Log;
import morgan.support.Utils;

import java.util.concurrent.atomic.AtomicInteger;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    public static final AtomicInteger idAllocate = new AtomicInteger();

    public static final AtomicInteger connNum = new AtomicInteger();

    private Connection conn;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        Channel channel = ctx.channel();

        conn = new Connection(ConnStartUp.connNode, channel, idAllocate.getAndIncrement());
        ConnStartUp.connNode.addWorker(conn);

        Log.connection.info("morgan.connection established, from:{}, currentNum:{}", channel.remoteAddress(), connNum.incrementAndGet());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            byte[] buf = (byte[]) msg;

            int len = Utils.bytesToInt(buf);
            if (len > 64 * 1024)
                return;

            conn.recv(buf);
        } catch (Exception e) {
            Log.connection.error("read channel error ", e);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);

        if (conn == null)
            return;

        conn.closeConnection();
//        Log.connection.info("morgan.connection closed, id:{}", conn.getId());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        super.exceptionCaught(ctx, cause);
        if (cause.getMessage().contains("Connection reset") || cause.getMessage().contains("远程主机强迫关闭了一个现有的连接"))
            Log.connection.error("exception: connId:{}, cause:{}", conn.getId(), cause.getMessage());
        else
            Log.connection.error("Connection Exception: connId={}", conn.getId(), cause);
    }
}
