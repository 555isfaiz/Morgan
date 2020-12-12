#include <memory.h>
#include "stream_base.h"
#include "ms_logger.h"

namespace msutils
{
    namespace msstream
    {
        char* StreamBase::GetBuffer()
        {
            char* buf = new char[actual_size + 1];
            memset(buf, '\0', actual_size + 1);
            memcpy(buf, buffer, actual_size);
            return buf;
        }

        void StreamBase::SetBufferSize(size_t size)
        {
            if (size > MAXIMUM_BUFFER_SIZE)
            {
                mLogError("buffer overflowed! size:" << size);
                return;
            }
            
            if (buffer == nullptr)
            {
                buffer = new char[size];
                memset(buffer, 0, size);
                return;
            }

            if (size < realSize_)
            {
                mLogError("can't set a smaller buffer, if you want to reset buffer, call reset()");
                return;
            } 
            else if (size > realSize_)
            {
                char* newBuf = new char[size];
                memcpy(newBuf, buffer, realSize_);
                delete(buffer);
                buffer = newBuf;
            }
        }

        void StreamBase::Reset()
        {
            delete(buffer);
            buffer = new char[DEFAULT_BUFFER_SIZE];
            actual_size = 0;
            realSize_ = DEFAULT_BUFFER_SIZE;
        }

        StreamBase::~StreamBase()
        {
            delete buffer;
        }
    }
}