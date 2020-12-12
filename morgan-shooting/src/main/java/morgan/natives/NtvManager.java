package morgan.natives;

public class NtvManager {
    public static native void setNtvFunctions(INtvFunctions ntvF);
    public static native void loadMeshes(String path);
    public static native void jniTest();
}
