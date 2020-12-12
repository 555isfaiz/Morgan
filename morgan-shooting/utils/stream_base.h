#ifndef _ms_stream_
#define _ms_stream_

#include <string.h>

#define DEFAULT_BUFFER_SIZE 512
#define MAXIMUM_BUFFER_SIZE 50*1024

#define LENGTH_INT 4
#define LENGTH_LONG 8

namespace msutils
{
    namespace msstream
    {
        enum
        {
            TYPE_INT            = 1,      //int
            TYPE_LONG           = 2,      //long long
            TYPE_FLOAT          = 3,      //float
            TYPE_DOUBLE         = 4,      //double
            TYPE_BOOL           = 5,      //bool
            TYPE_BYTE           = 6,      //char
            TYPE_STRING         = 7,      //string
            TYPE_LIST           = 8,      //vector
            TYPE_MESSAGE        = 11,     
            TYPE_NULL           = 13,     //nullptr
        };

        class StreamBase
        {
        private:
            /* data */
        public:
            char* GetBuffer();
            inline void SetBuffer(char* buf, size_t size)
            {
                if (size > realSize_)
                {
                    SetBufferSize(realSize_ << 1);
                }
                
                memcpy(buffer, buf, size);
                realSize_ = size;
                actual_size = size; 
            }
            inline size_t GetActualSize(){ return actual_size; }
            void SetBufferSize(size_t size);
            void Reset();

        protected:
            char *buffer;
            size_t actual_size = 0;     //indecate the length of the real content
            size_t realSize_ = 0;       //indecate the length of the container buffer
            StreamBase(){ buffer = new char[DEFAULT_BUFFER_SIZE]; realSize_ = DEFAULT_BUFFER_SIZE; }
            virtual ~StreamBase();
        };
    }
}

#endif
