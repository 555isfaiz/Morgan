package morgan.connection;

import io.netty.channel.ChannelInboundHandlerAdapter;
import morgan.structure.Node;

public class ConnStarter {
    public static Node connNode;
    public static NettyServer nettyThread;

    public static void startUp(Node node){
        connNode = node;
        nettyThread = new NettyServer();
        nettyThread.start();
    }

    public static void stopServer(){
        nettyThread.serverStop();
        nettyThread = null;
    }
}
