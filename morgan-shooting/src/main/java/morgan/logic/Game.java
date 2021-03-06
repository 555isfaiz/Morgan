package morgan.logic;

import morgan.messages.MessageBase;
import morgan.messages.MessageSender;
import morgan.natives.NtvGame;
import morgan.structure.Node;
import morgan.structure.Worker;
import morgan.support.functions.Function1;
import morgan.support.functions.Function2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game extends Worker {

    private int sessionId_;
    private NtvGame _ntvGame;
    private Map<Integer, PlayerInfo> _players = new HashMap<>();
    private boolean gameEnded_ = false;

    public Game(Node node, int id) {
        super(node, "Game-" + id);

        _ntvGame = new NtvGame();
        _ntvGame.create(id);
        _ntvGame.setJvObj(this);
    }

    public void pulseOverride(){
        if (gameEnded_)
            return;
        _ntvGame.pulse();
    }

    public void initGame(List<PlayerInfo> players) {
        int[] ids = new int[players.size()];
        String[] names = new String[players.size()];

        for (int i = 0; i < players.size(); i++) {
            ids[i] = players.get(i).getId();
            names[i] = players.get(i).getName();

            _players.put(players.get(i).getId(), players.get(i));
        }

        _ntvGame.initGame(ids, names);
    }

    public static void initGame_(int workerId, List<PlayerInfo> players) {
        CallWithStack0(workerId, players);
    }

    public void removePlayer(int id, boolean logOut){
        if (!_players.containsKey(id))
            return;

        var p = _players.remove(id);
        if (_players.size() == 0) {
            gameEnded_ = true;
            _ntvGame.destroy();
            GameManager.sessionDestroy_(sessionId_);
        } else {
            _ntvGame.removePlayer(id);
        }

        if (!logOut){
            Lobby.addPlayer_(p);
        }
    }

    public static void removePlayer_(int workerId, int id, boolean logOut) {
        CallWithStack0(workerId, id, logOut);
    }

    public void handleGameMsg(int senderId, byte[] msg){
        _ntvGame.handleMsg(senderId, msg);
    }

    public static void handleGameMsg_(int workerId, int senderId, byte[] msg) {
        CallWithStack0(workerId, senderId, msg);
    }

    public void sendMsg(int id, MessageBase msg)
    {
        if (msg == null || !_players.containsKey(id))
            return;
        List<Integer> ids = new ArrayList<>();
        ids.add(id);
        sendMsg(ids, msg);
    }

    public void sendMsg(List<Integer> ids, MessageBase msg)
    {
        if (ids == null || msg == null)
            return;
        for (var id : ids) {
            if (!_players.containsKey(id))
                continue;
            MessageSender.sendMsgToPlayer(_players.get(id), msg);
        }
    }

    public void sendMsgBytes(int id, byte[] msg){
        if (msg == null || !_players.containsKey(id))
            return;
        int[] ids = {id};
        sendMsgBytes(ids, msg);
    }

    public void sendMsgBytes(int[] ids, byte[] msg) {
        if (ids == null || msg == null)
            return;
        for (var id : ids) {
            if (!_players.containsKey(id))
                continue;
            MessageSender.sendMsgToPlayer(_players.get(id), msg);
        }
    }

    @Override
    public void registMethods() {
        _methodManager.registMethod("handleGameMsg", (Function2<Integer, byte[]>)this::handleGameMsg, Integer.class, byte[].class);
        _methodManager.registMethod("initGame", (Function1<List<PlayerInfo>>)this::initGame, List.class);
        _methodManager.registMethod("removePlayer", (Function2<Integer, Boolean>)this::removePlayer, Integer.class, boolean.class);
    }

}
