package morgan.messages;

import morgan.structure.serialize.InputStream;
import morgan.structure.serialize.OutputStream;



public class CSMove extends MessageBase{
    public int playerId;
    public BVector3 curPos;
    public BVector3 direction;
    public long timeStamp;


    public CSMove(){
        msgId = 2001;
    }

    @Override
    public void writeOut(OutputStream out) {
        out.write(playerId);
        out.write(curPos);
        out.write(direction);
        out.write(timeStamp);

    }

    @Override
    public void readIn(InputStream in) {
        playerId = in.read();
        curPos = in.read();
        direction = in.read();
        timeStamp = in.read();

    }
}