#ifndef _ms_collider_
#define _ms_collider_

#include <functional>
#include "ms_utils.h"
#include "ms_vector3.h"

#define PLAYER_RADIUS 0.6f
#define MOVE_SPEED 5.0f         // 5.0f equals to move speed in client, 
#define MOVE_DELTA 20           // 20 equals to frame time in unity
#define PI 3.14159f
#define PHYSICS msgame::physics

namespace msgame
{
    namespace msgamesession
    {
        class GameSession;
    } // namespace msgamesession
} // namespace msgame


namespace msgame 
{
    namespace physics
    {
        typedef std::function<VECTOR::Vector3(const VECTOR::Vector3&)> CoordinateMap;

        enum 
        {
            CHECK_RES_OK = 1,
            CHECK_RES_UNREACHABLE,
            CHECK_RES_RECALC,       //need recalculation
        };

        class Collider
        {
        private:
            msgame::msgamesession::GameSession *owner_;

        public:
            Collider(msgame::msgamesession::GameSession *owner) : owner_(owner) {}

            int32 CollideCheck(int32 playerId, VECTOR::Vector3& direction, int32& collideId, float moveSpeed = MOVE_SPEED, int32 moveDelta = MOVE_DELTA);
            VECTOR::Vector3 RecalculatePos(VECTOR::Vector3& curPosition, VECTOR::Vector3& direction, VECTOR::Vector3& collidePos, float moveSpeed = MOVE_SPEED, int32 moveDelta = MOVE_DELTA);
            CoordinateMap MapCoordinateSystem(VECTOR::Vector3& origin, VECTOR::Vector3& yminus2);

            virtual ~Collider() {}
        };
    }
}

#endif