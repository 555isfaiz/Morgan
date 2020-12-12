#ifndef _ms_vector2_
#define _ms_vector2_

#include "ms_types.h"
#include "ms_message.h"

#define VECTOR msutils::vector

namespace msutils
{
    namespace vector
    {
        class Vector2
        {
        public:
            float x = 0.0f;
            float y = 0.0f;

            Vector2();
            virtual ~Vector2(){}

            msmessage::BVector2D getMsg()
            {
                msmessage::BVector2D msg = msmessage::BVector2D();
                msg.x = x;
                msg.y = y;
                return msg;
            }
        };
    }
}

#endif