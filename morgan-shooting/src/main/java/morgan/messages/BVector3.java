package morgan.messages;

import morgan.structure.serialize.InputStream;
import morgan.structure.serialize.OutputStream;



public class BVector3 extends MessageBase {
    public float x;
    public float y;
    public float z;


    public BVector3() {
        msgId = 103;
    }

    @Override
    public void writeOut(OutputStream out) {
        out.write(x);
        out.write(y);
        out.write(z);

    }

    @Override
    public void readIn(InputStream in) {
        x = in.read();
        y = in.read();
        z = in.read();

    }
}