package morgan.logic;

import morgan.structure.Node;
import morgan.structure.Worker;
import morgan.support.Log;
import morgan.support.Time;
import morgan.support.Timmer;
import morgan.support.functions.Function1;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class GameManager extends Worker {

    private Set<Integer> sessions = new HashSet<>();
    private AtomicInteger idAllocate = new AtomicInteger();
    private Timmer printTimmer = new Timmer(Timmer.TYPE_PERIOD, 3 * Time.MIN, false);

    public GameManager(Node node) {
        super(node, "GameManager");
    }

    public void pulseOverride(){
        if (printTimmer.isTrigger())
            Log.gameMngr.info("current session num:{}", sessions.size());
    }

    public void createGame(List<PlayerInfo> players){
        if (players.size() <= 0 || players.size() > 100){
            returns("result", -2);
            return;
        }

        //limit the number of sessions in the future

        int sessionId = idAllocate.getAndIncrement();
        sessions.add(sessionId);
        Game g = new Game(_node, sessionId);
        g.initGame(players);
        _node.addWorker(g);

        GlobalPlayerManager.informGameStart_(players, sessionId);

        returns("result", 0, "gameSessionId", sessionId);
    }

    public static void createGame_(List<PlayerInfo> players) {
        CallWithStack(players);
    }

    public void sessionDestroy(int sessionId) {
        sessions.remove(sessionId);
    }

    public static void sessionDestroy_(int sessionId) {
        CallWithStack((Object)sessionId);
    }

    @Override
    public void registMethods() {
        _methodManager.registMethod("createGame", (Function1<List<PlayerInfo>>)this::createGame, List.class);
        _methodManager.registMethod("sessionDestroy", (Function1<Integer>)this::sessionDestroy, Integer.class);
    }
}
