package morgan.messages;

import morgan.structure.serialize.InputStream;
import morgan.structure.serialize.OutputStream;



public class BVector2 extends MessageBase{
    public float x;
    public float y;


    public BVector2(){
        msgId = 102;
    }

    @Override
    public void writeOut(OutputStream out) {
        out.write(x);
        out.write(y);

    }

    @Override
    public void readIn(InputStream in) {
        x = in.read();
        y = in.read();

    }
}