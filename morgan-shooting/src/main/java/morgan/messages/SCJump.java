package morgan.messages;

import morgan.structure.serialize.InputStream;
import morgan.structure.serialize.OutputStream;



public class SCJump extends MessageBase {
    public int playerId;


    public SCJump() {
        msgId = 2004;
    }

    @Override
    public void writeOut(OutputStream out) {
        out.write(playerId);

    }

    @Override
    public void readIn(InputStream in) {
        playerId = in.read();

    }
}