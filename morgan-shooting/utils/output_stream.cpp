#include <math.h>
#include <typeinfo>
#include <string>
#include <memory.h>
#include <string.h>
#include <vector>
#include "ms_utils.h"
#include "output_stream.h"

namespace msutils
{
    namespace msstream
    {
        void OutputStream::write(int32 val)
        {
            WRITE_NULL;

            char *b = ResolveNumber(val);
            WriteCompressedTL(TYPE_INT, b[0]);
            writeV(b + 1, b[0]);

            delete b;
        }

        void OutputStream::write(int64 val)
        {
            WRITE_NULL;

            char *b = ResolveNumber(val);
            WriteCompressedTL(TYPE_LONG, b[0]);
            writeV(b + 1, b[0]);

            delete b;
        }

        void OutputStream::write(float val)
        {
            WRITE_NULL;

            char t[2] = {(char)TYPE_FLOAT, '\0'};
            writeV(t, 1);
            char* b = FloatToBytes(val);
            writeV(b, 4);

            delete b;
        }

        void OutputStream::write(double val)
        {
            WRITE_NULL;

            char t[2] = {(char)TYPE_DOUBLE, '\0'};
            writeV(t, 1);
            char *b = DoubleToBytes(val);
            writeV(b, 8);

            delete b;
        }

        void OutputStream::write(bool val)
        {
            WRITE_NULL;

            char b[2] = {(char)(((val ? 1 : 0) << 4) | TYPE_BOOL), '\0'};
            writeV(b, 1);
        }

        void OutputStream::write(char val)
        {
            WRITE_NULL;

            char b[3] = {(char)TYPE_BYTE, val, '\0'};
            writeV(b, 2);
        }

        void OutputStream::write(std::string val)
        {
            WRITE_NULL;

            char *lenB = ResolveNumber((int64)val.length());
            WriteTandVL(TYPE_STRING, lenB[0], lenB + 1);
            char *b = const_cast<char *>(val.c_str());
            writeV(b, val.length());

            delete lenB;
        }

        void OutputStream::write(msmessage::MessageBase *val)
        {
            WRITE_NULL;

            WriteTandId(TYPE_MESSAGE, val->id);
            val->write(this);
        }

        void OutputStream::WriteNull()
        {
            char t[2] = {(char)TYPE_NULL, '\0'};
            writeV(t, 1);
        }

        char *OutputStream::ResolveNumber(int64 number)
        {
            if (number == 0)
                return new char[2]{1, 0};
            int64 absI = abs(number);
            int64 div = ~0x7fffffffffffffff;
            int32 index = 0;

            while ((absI & div) == 0)
            {
                div = div >> 1;
                if (index == 0)
                    div = div ^ (~0x7fffffffffffffff);
                index++;
            }

            int32 len = 8 - (index % 8 == 0 ? (index / 8 - 1) : (index / 8));
            char *res = new char[len + 1];
            memset(res, '\0', len + 1);
            res[0] = len;
            for (int j = 0; j < len; j++)
            {
                res[j + 1] = (char)(absI >> (8 * (len - j - 1)));
            }
            if (number < 0)
            {
                res[1] |= (char)128;
            }
            return res;
        }

        void OutputStream::WriteCompressedTL(int32 tag, int32 length)
        {
            char t[2] = {(char)(length << 4 | tag), '\0'};
            writeV(t, 1);
        }

        void OutputStream::WriteTandVL(int32 tag, int32 length, char *lenBuf)
        {
            WriteCompressedTL(tag, length);
            writeV(lenBuf, length);
        }

        void OutputStream::WriteTandId(int32 tag, int32 id)
        {
            char bytes[6] = {'\0', '\0', '\0', '\0', '\0', '\0'};
            bytes[0] = (char)tag;
            char *idB = IntToBytes(id);

            for (int32 i = 1; i < 5; i++)
                bytes[i] = idB[i - 1];

            writeV(bytes, 5);
            delete idB;
        }

        void OutputStream::writeV(char *buf, int32 bufLen)
        {
            writeV(actual_size, buf, bufLen);
        }

        void OutputStream::writeV(int32 begin, char* buf, int32 bufLen)
        {
            if (begin + bufLen >= realSize_)
            {
                SetBufferSize(realSize_ << 1);
            }

            memcpy(buffer + actual_size, buf, bufLen);

            if ((begin + bufLen) > actual_size)
                actual_size = begin + bufLen;
        }
    } // namespace msstream
} // namespace msutils