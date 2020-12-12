#ifndef _ms_vector3_
#define _ms_vector3_

#include <math.h>
#include "ms_types.h"
#include "ms_message.h"

#define VECTOR msutils::vector

namespace msutils
{
    namespace vector
    {
        class Vector3
        {
        public:
            float x = 0.0f;
            float y = 0.0f;
            float z = 0.0f;

            Vector3() {}
            Vector3(float x, float y, float z) : x(x), y(y), z(z) {}
            Vector3(msmessage::BVector3& bv3) : x(bv3.x),
                                               y(bv3.y),
                                               z(bv3.z)
            {
            }
            virtual ~Vector3() {}

            inline msmessage::BVector2 getMsg2()
            {
                msmessage::BVector2 msg = msmessage::BVector2();
                msg.x = x;
                msg.y = y;
                return msg;
            }

            inline msmessage::BVector3 getMsg3()
            {
                msmessage::BVector3 msg = msmessage::BVector3();
                msg.x = x;
                msg.y = y;
                msg.z = z;
                return msg;
            }

            Vector3& operator + (const Vector3 &add)
            {
                this->x += add.x;
                this->y += add.y;
                this->z += add.z;
                return *this;
            }

            Vector3 operator - (const Vector3 &minus)
            {
                Vector3 r;
                r.x = x - minus.x;
                r.y = y - minus.y;
                r.z = z - minus.z;
                return r;
            }

            bool operator == (const Vector3 &equal) const
            {
                return x == equal.x && y == equal.y && z == equal.z;
            }

            inline float dot(const Vector3 &dot) const
            {
                return x * dot.x + y * dot.y + z * dot.z;
            }

            inline float Length() const
            {
                return sqrt(x * x + y * y + z * z);
            }

            inline float Length2() const
            {
                return x * x + y * y + z * z;
            }

            inline Vector3 TwoDlize()
            {
                Vector3 v;
                v.x = x;
                v.z = z;
                return v;
            }
        };

        static Vector3 MoveTo(const Vector3& pos, const Vector3& dir, float speed, int64 time)
        {
            float distance = speed * time / 1000;
            if (distance <= 0.0000f)
                return pos;

            Vector3 finalPos;
            float dirLen = dir.Length();
            float dirLen2 = dir.Length2();
            float mod = dirLen * distance;
            finalPos.x = mod / (dirLen2 / dir.x) + pos.x;
            finalPos.y = mod / (dirLen2 / dir.y) + pos.y;
            finalPos.z = mod / (dirLen2 / dir.z) + pos.z;

            return finalPos;
        }

        static float Distance(const Vector3& p1, const Vector3& p2)
        {
            return sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y) + (p1.z - p2.z) * (p1.z - p2.z));
            // return (p1 - p2).Length();
        }
    }
}

#endif