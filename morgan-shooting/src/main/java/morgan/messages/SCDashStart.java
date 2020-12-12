package morgan.messages;

import morgan.structure.serialize.InputStream;
import morgan.structure.serialize.OutputStream;



public class SCDashStart extends MessageBase{
    public int playerId;
    public BVector3 direction;


    public SCDashStart(){
        msgId = 2006;
    }

    @Override
    public void writeOut(OutputStream out) {
        out.write(playerId);
        out.write(direction);

    }

    @Override
    public void readIn(InputStream in) {
        playerId = in.read();
        direction = in.read();

    }
}