#include "input_stream.h"
#include "ms_logger.h"
#include "ms_types.h"

namespace msutils
{
    namespace msstream
    {
        InputStream::InputStream(char* bytes, int32 size)
        {
            SetBuffer(bytes, size);
        }

        int32 InputStream::ReadInt32()
        {
            READ_BEGIN(0, TYPE_INT);

                int32 len = (firstByte & 0xF0) >> 4;
                return (int32)ResolveNumber(len);

            READ_END(0);
        }

        int64 InputStream::ReadInt64()
        {
            READ_BEGIN(0L, TYPE_LONG);

                int32 len = (firstByte & 0xF0) >> 4;
                return ResolveNumber(len);

            READ_END(0L);
        }

        float InputStream::ReadFloat()
        {
            READ_BEGIN(0.0f, TYPE_FLOAT);

                char* b = readV(LENGTH_INT);
                return BytesToFloat(b);

            READ_END(0.0f);
        }

        double InputStream::ReadDouble()
        {
            READ_BEGIN(0.0f, TYPE_DOUBLE);

                char* b = readV(LENGTH_LONG);
                return BytesToDouble(b);

            READ_END(0.0f);
        }

        bool InputStream::ReadBool()
        {
            READ_BEGIN(false, TYPE_BOOL);

                return (((firstByte & 0xF0) >> 4) & 1) == 1;

            READ_END(false);
        }

        char InputStream::ReadChar()
        {
            READ_BEGIN('\0', TYPE_BYTE);

                return readV(1)[0];

            READ_END('\0');
        }

        std::string InputStream::ReadString()
        {
            READ_BEGIN("", TYPE_STRING);

                int32 lenOflen = (firstByte & 0xF0) >> 4;
                int32 length = (int32)ResolveNumber(lenOflen);

                char* str = readV(length);
                std::string s(str);
                return s;

            READ_END("");
        }

        msmessage::MessageBase* InputStream::ReadMsg()
        {
            READ_BEGIN(nullptr, TYPE_MESSAGE);

                msmessage::MessageBase* msg;
                int32 id = BytesToInt(readV(LENGTH_INT));

                msg = msmessage::GetMsgById(id);
                msg->read(this);
                return msg;
                
            READ_END(nullptr);
        }

        int64 InputStream::ResolveNumber(int32 length)
        {
            char* bytes = readV(length);
            bool isNegative = (bytes[0] & (1 << 7)) == 128;
            int64 num = 0L;
            for (int32 i = 0; i < length; i++)
            {
                if (i == 0)
                    num |= (int64)(bytes[i] & 0x7F) << ((length - i - 1) * 8);
                else 
                {
                    num |= ((int64) bytes[i] & 0xFF) << ((length - i - 1) * 8);
                }
            }
            return isNegative ? -num : num;
        }

        char* InputStream::readV(int32 length)
        {
            return readV(cursor, length);
        }

        char* InputStream::readV(int32 begin, int32 length)
        {
            if (begin == actual_size)
            {
                mLogError("End of InputStream!");
                return nullptr;
            }

            if ((begin + length) > actual_size)
            {
                length = actual_size - begin;
            }
            
            char* bytes = new char[length + 1];
            memset(bytes, '\0', length + 1);
            memcpy(bytes, buffer + cursor, length);

            if (begin == cursor)
                cursor += length;

            return bytes;
        }
    }
}