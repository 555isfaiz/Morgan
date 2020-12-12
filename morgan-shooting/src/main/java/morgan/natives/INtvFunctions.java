package morgan.natives;

public interface INtvFunctions {
    void log(int type, String msg, String file, int line);

    void sendMsg(NtvGame ntvGame, int id, byte[] msg);

    void sendMsgMulti(NtvGame ntvGame, int[] ids, byte[] msg);
}
