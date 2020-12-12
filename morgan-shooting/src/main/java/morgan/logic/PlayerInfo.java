package morgan.logic;

import morgan.structure.serialize.InputStream;
import morgan.structure.serialize.OutputStream;
import morgan.structure.serialize.Serializable;

import java.io.IOException;

public class PlayerInfo implements Serializable {

    private int _id;
    private int _connId;
    private int _state;
    private int _sessionId;
    private String _node;
    private String _name;

    public PlayerInfo(){}

    public PlayerInfo(int id, int connId, String node, String name){
        _id = id;
        _connId = connId;
        _node = node;
        _name = name;
    }

    public int getId() {
        return _id;
    }

    public PlayerInfo setId(int _id) {
        this._id = _id;
        return this;
    }

    public int getConnId() {
        return _connId;
    }

    public PlayerInfo setConnId(int _connId) {
        this._connId = _connId;
        return this;
    }

    public int getState() {
        return _state;
    }

    public PlayerInfo setState(int _state) {
        this._state = _state;
        return this;
    }

    public int getSessionId() {
        return _sessionId;
    }

    public PlayerInfo setSessionId(int _sessionId) {
        this._sessionId = _sessionId;
        return this;
    }

    public String getNode() {
        return _node;
    }

    public PlayerInfo setNode(String _node) {
        this._node = _node;
        return this;
    }

    public String getName() {
        return _name;
    }

    public PlayerInfo setName(String _name) {
        this._name = _name;
        return this;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        out.write(_id);
        out.write(_connId);
        out.write(_state);
        out.write(_node);
        out.write(_name);
    }

    @Override
    public void readIn(InputStream in) throws IOException {
        _id = in.read();
        _connId = in.read();
        _state = in.read();
        _node = in.read();
        _name = in.read();
    }
}
