package morgan.messages;

import morgan.structure.serialize.InputStream;
import morgan.structure.serialize.OutputStream;

import java.util.ArrayList;
import java.util.List;

public class SCGameSync extends MessageBase{
    public int sessionId;
    public List<BPlayer> players = new ArrayList<>();


    public SCGameSync(){
        msgId = 1005;
    }

    @Override
    public void writeOut(OutputStream out) {
        out.write(sessionId);
        out.write(players);

    }

    @Override
    public void readIn(InputStream in) {
        sessionId = in.read();
        players = in.read();

    }
}