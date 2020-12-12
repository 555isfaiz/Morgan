#ifndef _ms_outputstream_
#define _ms_outputstream_

#include <typeinfo>
#include <string>
#include "stream_base.h"
#include "ms_message.h"

#define STREAM msutils::msstream

#define WRITE_NULL if (&val == NULL)    \
            {                           \
                WriteNull();            \
                return;                 \
            }                           

namespace msutils
{
    namespace msstream
    {
        class OutputStream : public StreamBase
        {
        private:
            void writeV(char *buf, int32 bufLen);
            void writeV(int32 begin, char* buf, int32 bufLen);
            void WriteNull();
        public:
            OutputStream() : OutputStream(DEFAULT_BUFFER_SIZE) {}

            OutputStream(int32 size)
            {
                SetBufferSize(size);
            }

            char* ResolveNumber(int64 number);
            void WriteCompressedTL(int32 tag, int32 length);
            void WriteTandId(int32 tag, int32 id);
            void WriteTandVL(int32 tag, int32 length, char* lenBuf);
            void write(int32 val);
            void write(int64 val);
            void write(float val);
            void write(double val);
            void write(bool val);
            void write(char val);
            void write(std::string val);
            void write(msmessage::MessageBase *val);

            virtual ~OutputStream(){}
        };
    }
}

#endif