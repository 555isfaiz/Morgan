package morgan.logic;

import morgan.messages.MsgHandler;
import morgan.structure.Node;
import morgan.structure.Worker;
import morgan.support.Log;
import morgan.support.Time;
import morgan.support.Timmer;
import morgan.support.functions.Function1;
import morgan.support.functions.Function2;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Lobby extends Worker {

    private Map<Integer, PlayerInfo> _lobby_players = new LinkedHashMap<>();
    private MsgHandler _msgHandler = new MsgHandler(this);
    private Timmer printTimmer = new Timmer(Timmer.TYPE_PERIOD, 3 * Time.MIN, false);

    public Lobby(Node node) {
        super(node, "Lobby");
    }

    public void pulseOverride(){
        if (printTimmer.isTrigger())
            printInfo();

        pulseMatch();
    }

    public void addPlayer(PlayerInfo p){
        if (_lobby_players.containsKey(p.getId()))
            return;
        _lobby_players.put(p.getId(), p);
        Log.lobby.info("player logged in. playerId:{}, connId:{}", p.getId(), p.getConnId());
    }

    public static void addPlayer_(PlayerInfo p) {
        CallWithStack(p);
    }

    public void handleLobbyMsg(int senderId, byte[] msg){
        if (!_lobby_players.containsKey(senderId))
            return;
        _msgHandler.handle(senderId, msg);
    }

    public static void handleLobbyMsg_(int senderId, byte[] msg) {
        CallWithStack((Object) senderId, msg);
    }

    public void playerLogOut(int playerId){
        var p = _lobby_players.remove(playerId);
        Log.lobby.info("player logged out. playerId:{}", playerId);
    }

    public static void playerLogOut_(int playerId) {
        CallWithStack(playerId);
    }

    private void printInfo(){
        Log.lobby.info("current player count in lobby is:{}", _lobby_players.size());
    }

    private void pulseMatch(){
        if (_lobby_players.size() < 2)
            return;

        List<PlayerInfo> list = new ArrayList<>();
        List<PlayerInfo> all = new ArrayList<>(_lobby_players.values());
        for (int i = 0; i < 2; i++) {
            var p = all.get(i);
            _lobby_players.remove(p.getId());
            list.add(p);
        }

        GameManager.createGame_(list);
    }

    @Override
    public void registMethods() {
        _methodManager.registMethod("addPlayer", (Function1<PlayerInfo>)this::addPlayer, PlayerInfo.class);
        _methodManager.registMethod("handleLobbyMsg", (Function2<Integer, byte[]>)this::handleLobbyMsg, Integer.class, byte[].class);
        _methodManager.registMethod("playerLogOut", (Function1<Integer>)this::playerLogOut, Integer.class);
    }
}
