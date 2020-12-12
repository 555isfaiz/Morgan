#ifndef _ms_utils_
#define _ms_utils_

#include <string.h>
#include <chrono>
#include "ms_types.h"
namespace msutils
{
    static char* IntToBytes(int32 num)
    {
        char* r = new char[4];
        r[3] = (char) (num & 0xff);
        r[2] = (char) ((num & 0xff00) >> 8);
        r[1] = (char) ((num & 0xff0000) >> 16);
        r[0] = (char) ((num & 0xff000000) >> 24);
        return r;
    }

    static char* LongToBytes(int64 num)
    {
        char* r = new char[8];
        r[7] = (char) (num & 0xff);
        r[6] = (char) ((num & 0xff00) >> 8);
        r[5] = (char) ((num & 0xff0000) >> 16);
        r[4] = (char) ((num & 0xff000000) >> 24);
        int64 t = num >> 32;
        r[3] = (char) ((t & 0xff));
        r[2] = (char) ((t & 0xff00) >> 8);
        r[1] = (char) ((t & 0xff0000) >> 16);
        r[0] = (char) ((t & 0xff000000) >> 24);
        return r;
    }

    static int32 BytesToInt(char* bytes)
    {
        return ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
    }

    static int64 BytesToLong(char* bytes)
    {
        return (((int64) bytes[0] & 0xFF) << 56) |
                (((int64) bytes[1] & 0xFF) << 48) |
                (((int64) bytes[2] & 0xFF) << 40) |
                (((int64) bytes[3] & 0xFF) << 32) |
                (((int64) bytes[4] & 0xFF) << 24) |
                (((int64) bytes[5] & 0xFF) << 16) |
                (((int64) bytes[6] & 0xFF) << 8) |
                ((int64) bytes[7] & 0xFF);
    }

    static char* FloatToBytes(float f)
    {
        char* b = new char[5];
        memset(b, '\0', 5);
        char* ptr = (char*)&f;
        b[0] = ptr[3];
        b[1] = ptr[2];
        b[2] = ptr[1];
        b[3] = ptr[0];
        return b;
    }

    static char* DoubleToBytes(double d)
    {
        char* b = new char[9];
        memset(b, '\0', 9);
        char* ptr = (char*)&d;
        b[0] = ptr[7];
        b[1] = ptr[6];
        b[2] = ptr[5];
        b[3] = ptr[4];
        b[4] = ptr[3];
        b[5] = ptr[2];
        b[6] = ptr[1];
        b[7] = ptr[0];
        return b;
    }

    static float BytesToFloat(char* b)
    {
        float f;
        void* ptr = &f;
        ((char*)ptr)[0] = b[3];
        ((char*)ptr)[1] = b[2];
        ((char*)ptr)[2] = b[1];
        ((char*)ptr)[3] = b[0];
        return f;
    }

    static double BytesToDouble(char* b)
    {
        double d;
        void* ptr = &d;
        ((char*)ptr)[0] = b[7];
        ((char*)ptr)[1] = b[6];
        ((char*)ptr)[2] = b[5];
        ((char*)ptr)[3] = b[4];
        ((char*)ptr)[4] = b[3];
        ((char*)ptr)[5] = b[2];
        ((char*)ptr)[6] = b[1];
        ((char*)ptr)[7] = b[0];
        return d;
    }

    static int64 GetNowMillSec()
    {
        auto t = std::chrono::system_clock::now();
        return std::chrono::duration_cast<std::chrono::milliseconds>(t.time_since_epoch()).count();
    }
}
#endif