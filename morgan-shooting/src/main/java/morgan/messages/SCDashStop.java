package morgan.messages;

import morgan.structure.serialize.InputStream;
import morgan.structure.serialize.OutputStream;



public class SCDashStop extends MessageBase {
    public int playerId;
    public BVector3 finalPos;


    public SCDashStop() {
        msgId = 2007;
    }

    @Override
    public void writeOut(OutputStream out) {
        out.write(playerId);
        out.write(finalPos);

    }

    @Override
    public void readIn(InputStream in) {
        playerId = in.read();
        finalPos = in.read();

    }
}