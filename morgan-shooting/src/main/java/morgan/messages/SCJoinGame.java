package morgan.messages;

import morgan.structure.serialize.InputStream;
import morgan.structure.serialize.OutputStream;

import java.util.ArrayList;
import java.util.List;

public class SCJoinGame extends MessageBase {
    public int sessionId;
    public int mySide;
    public List<BPlayer> players = new ArrayList<>();


    public SCJoinGame() {
        msgId = 1003;
    }

    @Override
    public void writeOut(OutputStream out) {
        out.write(sessionId);
        out.write(mySide);
        out.write(players);

    }

    @Override
    public void readIn(InputStream in) {
        sessionId = in.read();
        mySide = in.read();
        players = in.read();

    }
}