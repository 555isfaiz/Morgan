package morgan.natives;

public class NtvGame extends NtvObject {

    public native void create(int sessionId);

    public native void pulse();

    public native void handleMsg(int senderId, byte[] buf);

    public native void addPlayer(int id, String name);

    public native void initGame(int[] ids, String[] name);

    public native void removePlayer(int id);

    public native void destroy();
}
