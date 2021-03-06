package morgan.messages;

import morgan.structure.serialize.InputStream;
import morgan.structure.serialize.OutputStream;



public class CSLogin extends MessageBase {
    public boolean isShooter;


    public CSLogin() {
        msgId = 1001;
    }

    @Override
    public void writeOut(OutputStream out) {
        out.write(isShooter);

    }

    @Override
    public void readIn(InputStream in) {
        isShooter = in.read();

    }
}