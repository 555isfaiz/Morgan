package morgan.logic;

import morgan.messages.MessageBase;
import morgan.structure.Node;
import morgan.structure.Worker;
import morgan.structure.serialize.OutputStream;
import morgan.support.Log;
import morgan.support.functions.Function1;
import morgan.support.functions.Function2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalPlayerManager extends Worker {
    private Map<Integer, PlayerInfo> _players = new HashMap<>();

    public GlobalPlayerManager(Node node) {
        super(node, "GlobalPlayerManager");
    }

    public void pulseOverride() {
    }

    public void playerLogin(PlayerInfo playerInfo){
        if (_players.containsKey(playerInfo.getId()))
            Log.global.info("duplicated login. may be reconnect? playerId:{}, playerName:{}", playerInfo.getId(), playerInfo.getName());
        playerInfo.setState(Connection.CONNECTION_STATE_WAITING);
        _players.put(playerInfo.getId(), playerInfo);
        Lobby.addPlayer_(playerInfo);
    }

    public static void playerLogin_(PlayerInfo playerInfo) {
        CallWithStack(playerInfo);
    }

    public void playerLogout(int id){
        if (!_players.containsKey(id))
            return;
        var p = _players.remove(id);
        if (p.getState() == Connection.CONNECTION_STATE_WAITING)
            Lobby.playerLogOut_(p.getId());
        else if (p.getState() == Connection.CONNECTION_STATE_GAMING)
            Game.removePlayer_(p.getSessionId(), p.getId(), true);
    }

    public static void playerLogout_(int id) {
        CallWithStack(id);
    }

    public void informGameStart(List<PlayerInfo> players, int sessionId){
        for (var player : players){
            if (!_players.containsKey(player.getId()))
                continue;

            var p = _players.get(player.getId());
            p.setState(Connection.CONNECTION_STATE_GAMING);
            p.setSessionId(sessionId);
            Connection.changeState_(p.getConnId(), Connection.CONNECTION_STATE_GAMING, sessionId);
        }
    }

    public static void informGameStart_(List<PlayerInfo> players, int sessionId) {
        CallWithStack(players, sessionId);
    }

    public void sendMsg(int id, MessageBase msg){
        OutputStream out = new OutputStream();
        out.write(msg);
        sendMsgBytes(id, out.getBuffer());
    }

    public static void sendMsg_(int id, MessageBase msg) {
        CallWithStack((Object)id, msg);
    }

    public void sendMsgBytes(int id, byte[] msg){
        if (!_players.containsKey(id)){
            return;
        }
        var p = _players.get(id);
        Connection.sendMsgBytes_(p.getConnId(), msg);
    }

    public static void sendMsgBytes_(int id, byte[] msg) {
        CallWithStack((Object)id, msg);
    }

    public void sendMsgToAll(MessageBase msg){
        if (msg == null)
            return;
        for (var p : _players.values()){
            Connection.sendMsg_(p.getConnId(), msg);
        }
    }

    public static void sendMsgToAll_(MessageBase msg) {
        CallWithStack(msg);
    }

    public void sendMsgToGame(int sessionId, MessageBase msg){
        for (var p : _players.values()){
            if (p.getState() != Connection.CONNECTION_STATE_GAMING)
                continue;
            if (p.getSessionId() != sessionId)
                continue;
            Connection.sendMsg_(p.getConnId(), msg);
        }
    }

    public static void sendMsgToGame_(int sessionId, MessageBase msg) {
        CallWithStack((Object)sessionId, msg);
    }

    @Override
    public void registMethods() {
        _methodManager.registMethod("playerLogin", (Function1<PlayerInfo>)this::playerLogin);
        _methodManager.registMethod("playerLogout", (Function1<Integer>)this::playerLogout, Integer.class);
        _methodManager.registMethod("informGameStart", (Function2<List<PlayerInfo>, Integer>)this::informGameStart, List.class, Integer.class);
        _methodManager.registMethod("sendMsg", (Function2<Integer, MessageBase>)this::sendMsg, Integer.class);
        _methodManager.registMethod("sendMsgBytes", (Function2<Integer, byte[]>)this::sendMsgBytes, Integer.class);
        _methodManager.registMethod("sendMsgToAll", (Function1<MessageBase>)this::sendMsgToAll, MessageBase.class);
        _methodManager.registMethod("sendMsgToGame", (Function2<Integer, MessageBase>)this::sendMsgToGame, Integer.class);
    }
}
