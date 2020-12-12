package morgan.natives;

public class NtvObject {
    public long _ntvPtr;
    public Object _ntvJvObj = null;
    public NtvObject(){
    }

    public NtvObject(long ptr){
        _ntvPtr = ptr;
    }

    public Object getJvObj(){
        return _ntvJvObj;
    }

    public void setJvObj(Object jvObj){
        _ntvJvObj = jvObj;
    }
}
