package morgan.messages;

import morgan.logic.Connection;
import morgan.logic.Game;
import morgan.logic.Lobby;
import morgan.structure.Worker;
import morgan.structure.serialize.InputStream;
import morgan.support.Factory;
import morgan.support.Log;
import morgan.support.functions.Function3;

import java.util.HashMap;
import java.util.Map;

public class MsgHandler {

    private Worker _owner;

    @SuppressWarnings("unused")
    private int _last_handleId;

    private Map<Integer, Object> _handler_map = new HashMap<>();

    InputStream in = null;

    public MsgHandler(Worker worker){
        _owner = worker;
        loadHandlers(this);
    }

    public void handle(int sender, byte[] msg){
        if (in == null) {
            in = new InputStream(msg);
        } else {
            in.resetCursor();
            in.setBuffer(msg);
        }
        MessageBase m = in.read();
        handle(sender, m);
    }

    public void handle(int sender, MessageBase msg){
        handle(sender, Factory.messageMapInstance().getMessageId(msg), msg);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void handle(int sender, int msgId, MessageBase msg){
        if (!_handler_map.containsKey(msgId)){
            Log.msghandler.error("can't handle this message! owner:{}, msgId:{}, sender:{}", _owner.getName(), msgId, sender);
            return;
        }

        try {
            Function3 f = (Function3)_handler_map.get(msgId);
            f.apply(this, sender, msg);

            //set last handle id after the message was successfully handled
            _last_handleId = msgId;
        } catch (Exception e){
            Log.msghandler.error("error handling msg! owner:{}, msgId:{}, sender:{}", _owner.getName(), msgId, sender, e);
        }
    }

    //TODO: FILL THE METHOD!
    private static void loadHandlers(MsgHandler handler){
        Worker owner = handler._owner;
        if (owner instanceof Connection){
            // handlers of Connection...
        } else if (owner instanceof Lobby){
            // handlers of Lobby...
//            handler._handler_map.put(1001, (Function3<MsgHandler, Integer, CSLogin>)MsgHandler::onCSLogin);
        } else if (owner instanceof Game){
            // handlers of Game...
//            handler._handler_map.put(1002, (Function3<MsgHandler, Integer, CSMove>)MsgHandler::onCSMove);
        }
    }

    //*********************
    //
    // handlers of Lobby
    //
    //*********************
//    private static void onCSLogin(MsgHandler handler, int sender, CSLogin msg){
//        Lobby l = (Lobby)handler._owner;
//        l.playerLogOut(sender);
//    }

    //*********************
    //
    // handlers of Game
    //
    //*********************
//    private static void onCSMove(MsgHandler handler, int sender, CSMove msg){
//
//    }
}
