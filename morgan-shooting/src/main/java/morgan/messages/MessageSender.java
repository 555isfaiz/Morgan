package morgan.messages;

import morgan.logic.PlayerInfo;
import morgan.structure.Call;
import morgan.structure.Node;
import morgan.structure.Worker;
import morgan.structure.serialize.OutputStream;

public class MessageSender {

    public static void sendMsgToPlayer(int playerId, MessageBase msg){
        OutputStream out = new OutputStream();
        out.write(msg);
        sendMsgToPlayer(playerId, out.getBuffer());
    }

    public static void sendMsgToPlayer(int playerId, byte[] msg){
        Worker worker = Worker.getCurrentWorker();
        Node from = worker.getNode();
        String workerName = "GlobalPlayerManager";
        Call call = new Call();
        call.callType = Call.CALL_TYPE_RPC;
        call.method = workerName + "&" + "sendMsgBytes";
        call.from = from.getName();
        call.caller = worker.getName();
        call.parameters = new Object[]{playerId, msg};
        call.id = call.hashCode();
        call.dest = worker.getNode().getName();

        from.sendCall(call);
    }

    public static void sendMsgToPlayer(PlayerInfo player, MessageBase msg){
        sendMsgToPlayer(player.getNode(), player.getConnId(), msg);
    }

    public static void sendMsgToPlayer(PlayerInfo player, byte[] msg){
        sendMsgToPlayer(player.getNode(), player.getConnId(), msg);
    }

    public static void sendMsgToPlayer(String node, int connId, MessageBase msg){
        OutputStream out = new OutputStream();
        out.write(msg);
        sendMsgToPlayer(node, connId, out.getBuffer());
    }

    public static void sendMsgToPlayer(String node, int connId, byte[] msg){
        Worker worker = Worker.getCurrentWorker();
        Node from = worker.getNode();
        String workerName = "Connection-" + connId;
        Call call = new Call();
        call.callType = Call.CALL_TYPE_RPC;
        call.method = workerName + "&" + "sendMsgBytes";
        call.from = from.getName();
        call.caller = worker.getName();
        call.parameters = new Object[]{msg};
        call.id = call.hashCode();
        call.dest = node;

        from.sendCall(call);
    }
}
