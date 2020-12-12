package morgan.messages;

import morgan.structure.serialize.InputStream;
import morgan.structure.serialize.OutputStream;



public class SCMove extends MessageBase{
    public int playerId;
    public BVector3 curPos;
    public BVector3 direction;
    public long timeStamp;
    public int result;


    public SCMove(){
        msgId = 2002;
    }

    @Override
    public void writeOut(OutputStream out) {
        out.write(playerId);
        out.write(curPos);
        out.write(direction);
        out.write(timeStamp);
        out.write(result);

    }

    @Override
    public void readIn(InputStream in) {
        playerId = in.read();
        curPos = in.read();
        direction = in.read();
        timeStamp = in.read();
        result = in.read();

    }
}