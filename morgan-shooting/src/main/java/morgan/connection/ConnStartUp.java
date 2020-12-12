package morgan.connection;

import morgan.structure.Node;

public class ConnStartUp {
    public static Node connNode;
    public static NettyServer nettyThread;

    public static void main(String[] arg){
        Node node = new Node("Conn", "tcp://127.0.0.1:3330");
        node.startUp();
        startUp(node);
    }

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
