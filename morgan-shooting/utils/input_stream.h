#ifndef _ms_inputstream_
#define _ms_inputstream_

#include <string.h>
#include <string>
#include <vector>
#include "stream_base.h"
#include "ms_utils.h"
#include "ms_message.h"

#define STREAM msutils::msstream

#define READ_BEGIN(defaultV, type) char firstByte = readV(1)[0];        \
            int32 tag = ResolveTag(firstByte);                          \
            if (tag == TYPE_NULL)                                       \
            {                                                           \
                return defaultV;                                        \
                                                                        \
            } else if (tag == type)                                     \
            {                                                           

#define READ_END(defaultV) }                                            \
            return defaultV;                                            

namespace msutils
{
    namespace msstream
    {
        class InputStream : public StreamBase
        {
        private:
            int32 cursor = 0;
            char* readV(int32 begin, int32 length);
            inline int32 ResolveTag(char first){ return first & 0xf; }

        public:
            inline void ResetCursor(){ cursor = 0; }
            
            char* readV(int32 length);
            int64 ResolveNumber(int32 length);

            int32 ReadInt32();
            int64 ReadInt64();
            float ReadFloat();
            double ReadDouble();
            bool ReadBool();
            char ReadChar();
            std::string ReadString();
            msmessage::MessageBase* ReadMsg();

            InputStream(){}
            InputStream(char* buf, int32 size);
            virtual ~InputStream(){}
        };
    }
}

#endif