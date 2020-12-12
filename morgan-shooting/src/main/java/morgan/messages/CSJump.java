package morgan.messages;

import morgan.structure.serialize.InputStream;
import morgan.structure.serialize.OutputStream;



public class CSJump extends MessageBase{
    public int playerId;


    public CSJump(){
        msgId = 2003;
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