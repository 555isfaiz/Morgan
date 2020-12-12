package morgan.logic;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import morgan.messages.MessageBase;
import morgan.messages.SCLogin;
import morgan.structure.Node;
import morgan.structure.Worker;
import morgan.structure.serialize.OutputStream;
import morgan.support.Log;
import morgan.support.Utils;
import morgan.support.functions.Function1;
import morgan.support.functions.Function2;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Connection extends Worker {

    public static final int CONNECTION_STATE_WAITING = 0;
    public static final int CONNECTION_STATE_GAMING = 1;

    private Channel _channel;
    private int _connId;
    private int _playerId;
    private int _state;   //0: in lobby 1: in game
    private int _sessionId;    //lobbyId or gamesessionId
    private boolean _started;
    private LinkedBlockingQueue<byte[]> _recv = new LinkedBlockingQueue<>();
    private ConcurrentLinkedQueue<byte[]> _send = new ConcurrentLinkedQueue<>();

    private static final AtomicInteger idMalloc = new AtomicInteger();

    public Connection(Node node, Channel channel, int id) {
        super(node, "Connection$" + id);

        _channel = channel;
        _connId = id;
        _playerId = idMalloc.incrementAndGet();
        _started = true;
    }

    public void pulseOverride(){
        if (!_started)
            return;
        pulseInput();
        pulseOutput();
    }

    private void pulseInput(){
        while (!_recv.isEmpty()){
            handleMsg(_recv.poll());
        }
    }

    private void pulseOutput(){
        boolean sent = false;
        while (_send != null){
            byte[] msg = _send.poll();
            if (msg == null)
                break;

            if (!_channel.isActive())
                return;
            if (!_channel.isWritable())
                return;

            //fill the first four bytes with the whole length of the buffer.
            var bytes = new byte[msg.length + 4];
            System.arraycopy(msg, 0, bytes, 4, msg.length);
            System.arraycopy(Utils.intToBytes(bytes.length), 0, bytes, 0, 4);

            var buf = Unpooled.wrappedBuffer(bytes, 0, bytes.length);
//            Log.connection.info("msg sent, length:{}", bytes.length);
            _channel.write(buf);
            sent = true;
//            Log.connection.info("msg sent! playerId:{}, connId:{}", _playerId, _connId);
        }

        if (sent)
            _channel.flush();
    }

    private void handleMsg(byte[] buf) {
        byte[] msgBuf = new byte[buf.length - 4];
        System.arraycopy(buf, 4, msgBuf, 0, buf.length - 4);

        int msgId = Utils.bytesToInt(new byte[]{msgBuf[1], msgBuf[2], msgBuf[3], msgBuf[4]});

        //randomly distrbute to a lobby
        if (msgId == 1001) {
            PlayerInfo p = new PlayerInfo(_playerId, _connId, _node.getName(), "player" + _playerId);

            GlobalPlayerManager.playerLogin_(p);

            SCLogin m = new SCLogin();
            m.playerId = _playerId;
            sendMsg(m);

            return;
        }

        if (_state == CONNECTION_STATE_WAITING) {
            Lobby.handleLobbyMsg_(_playerId, msgBuf);
        } else if (_state == CONNECTION_STATE_GAMING) {
            Game.handleGameMsg_(_sessionId, _playerId, msgBuf);
        }
    }

    public void recv(byte[] buf){
        try {
            _recv.put(buf);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public void sendMsg(MessageBase msg){
        if (msg == null)
            return;
        OutputStream out = new OutputStream();
        out.write(msg);
        sendMsgBytes(out.getBuffer());
    }

    public static void sendMsg_(int workerId, MessageBase msg) {
        CallWithStack0(workerId, msg);
    }

    public void sendMsgBytes(byte[] msg){
        if (msg == null || msg.length == 0)
            return;
        _send.add(msg);
    }

    public static void sendMsgBytes_(int workerId, byte[] msg) {
        CallWithStack0(workerId, (Object) msg);
    }

    public void changeState(int state, int sessionId){
        _state = state;
        _sessionId = sessionId;
    }

    public static void changeState_(int workerId, int state, int sessionId) {
        CallWithStack0(workerId, state, sessionId);
    }

    public void closeConnection(){
        if (!_started)
            return;

        _started = false;
        this.schdule(100, () -> {
			if (_state == 0)
				Lobby.playerLogOut_(_playerId);
			else
				Game.removePlayer_(_sessionId, _playerId, true);
		});
        Log.connection.info("connection closed! playerId:{}, connId:{}", _playerId, _connId);
        deleteMe();
    }

    public int getId(){
        return _connId;
    }

    @Override
    public void registMethods() {
        _methodManager.registMethod("changeState", (Function2<Integer, Integer>)this::changeState, int.class, int.class);
        _methodManager.registMethod("sendMsgBytes", (Function1<byte[]>)this::sendMsgBytes, byte[].class);
        _methodManager.registMethod("sendMsg", (Function1<MessageBase>)this::sendMsg, MessageBase.class);
    }
}
