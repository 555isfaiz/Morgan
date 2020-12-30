package morgan.messages;

import morgan.structure.serialize.InputStream;
import morgan.structure.serialize.OutputStream;



public class BPlayer extends MessageBase {
    public int playerId;
    public String playerName;
    public int side;
    public BVector3 curPos;


    public BPlayer() {
        msgId = 101;
    }

    @Override
    public void writeOut(OutputStream out) {
        out.write(playerId);
        out.write(playerName);
        out.write(side);
        out.write(curPos);

    }

    @Override
    public void readIn(InputStream in) {
        playerId = in.read();
        playerName = in.read();
        side = in.read();
        curPos = in.read();

    }
}