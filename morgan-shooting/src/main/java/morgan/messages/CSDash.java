package morgan.messages;

import morgan.structure.serialize.InputStream;
import morgan.structure.serialize.OutputStream;



public class CSDash extends MessageBase {
    public BVector3 direction;
    public long duration;


    public CSDash() {
        msgId = 2005;
    }

    @Override
    public void writeOut(OutputStream out) {
        out.write(direction);
        out.write(duration);

    }

    @Override
    public void readIn(InputStream in) {
        direction = in.read();
        duration = in.read();

    }
}