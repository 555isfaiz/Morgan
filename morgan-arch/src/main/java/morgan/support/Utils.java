/*
 * Usage:
 */
package morgan.support;

import java.util.Random;

/**
 * <p>
 * 
 * @author Mark D</a>
 * @version 1.0, 2019.4.16
 */
public class Utils {

    private static final Random RANDOM = new Random();

    public static byte[] intToBytes(int data)
    {
        byte[] bytes = new byte[4];
        bytes[3] = (byte) (data & 0xff);
        bytes[2] = (byte) ((data & 0xff00) >> 8);
        bytes[1] = (byte) ((data & 0xff0000) >> 16);
        bytes[0] = (byte) ((data & 0xff000000) >> 24);
        return bytes;
    }

    public static byte[] longToBytes(long data)
    {
        byte[] bytes = new byte[8];
        bytes[7] = (byte) (data & 0xff);
        bytes[6] = (byte) ((data & 0xff00) >> 8);
        bytes[5] = (byte) ((data & 0xff0000) >> 16);
        bytes[4] = (byte) ((data & 0xff000000) >> 24);
        long t = data >> 32;
        bytes[3] = (byte) ((t & 0xff));
        bytes[2] = (byte) ((t & 0xff00) >> 8);
        bytes[1] = (byte) ((t & 0xff0000) >> 16);
        bytes[0] = (byte) ((t & 0xff000000) >> 24);
        return bytes;
    }

    public static int bytesToInt(byte[] bytes){
        if (bytes.length < 4){
            throw new IllegalArgumentException("bytes length of a int must be 4!");
        }

        return (bytes[0] & 0xFF) << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | bytes[3] & 0xFF;
    }

    public static long bytesToLong(byte[] bytes){
        if (bytes.length < 8){
            throw new IllegalArgumentException("bytes length of a long must be 8!");
        }

        return (((long) bytes[0] & 0xFF) << 56) |
                (((long) bytes[1] & 0xFF) << 48) |
                (((long) bytes[2] & 0xFF) << 40) |
                (((long) bytes[3] & 0xFF) << 32) |
                (((long) bytes[4] & 0xFF) << 24) |
                (((long) bytes[5] & 0xFF) << 16) |
                (((long) bytes[6] & 0xFF) << 8) |
                ((long) bytes[7] & 0xFF);
    }

    public static int nextInt(int min, int max){
        if (min >= max)
            return min;
        return min + RANDOM.nextInt(max - min);
    }
}
