#include <unordered_map>
#include <math.h>
#include "ms_collider.h"
#include "game_session.h"
#include "ms_logger.h"

namespace msgame
{
    namespace physics
    {
        int32 Collider::CollideCheck(int32 playerId, VECTOR::Vector3& direction, int32& collideId, float moveSpeed, int32 moveDelta)
        {
            auto movingPlayer = owner_->GetPlayer(playerId);
            auto players = owner_->GetPlayers();
            VECTOR::Vector3& player_pos = movingPlayer->pos();
            auto movePos = VECTOR::MoveTo(player_pos, direction, moveSpeed, moveDelta);
            float collideInfo[4] = {0.0f};

            for (auto iter = players.begin(); iter != players.end(); iter++)
            {
                if (iter->first == playerId)
                {
                    continue;
                }
                
                auto tmp_pos = iter->second->pos();
                float distance = VECTOR::Distance(tmp_pos, movePos);

                if (distance < (PLAYER_RADIUS * 2))
                {
                    // calcucate the point player is moving to
                    // if the point will collide with a player
                    // add collide count and record current distance between the two players
                    if (++collideInfo[0] == 1)
                    {
                        collideInfo[1] = VECTOR::Distance(player_pos, tmp_pos);
                        collideInfo[2] = iter->first;
                        auto delta = tmp_pos - player_pos;
                        // std::stringstream ss;
                        // ss<< std::setiosflags(std::ios::fixed) << std::setprecision(3)<<(double)(direction.dot(delta) / (direction.Length() * delta.Length()));
                        // ss >> collideInfo[3];
                        collideInfo[3] = direction.dot(delta) / (direction.Length() * delta.Length());
                    }
                }
            }

            if (collideInfo[0] == 0)
            {
                // road is clear, return ok
                return CHECK_RES_OK;
            }
            else if (collideInfo[0] == 1)
            {
                // one player on the road
                if (collideInfo[1] <= (PLAYER_RADIUS * 2) && 1.0f - collideInfo[3] > 0.0001f)
                {
                    // and they are colliding now
                    collideId = collideInfo[2];
                    return CHECK_RES_RECALC;
                }
                else if (collideInfo[1] <= (PLAYER_RADIUS * 2) && 1.0f - collideInfo[3] < 0.0001f)
                {
                    // going straightly toward another player
                    return CHECK_RES_UNREACHABLE;
                }
                else
                {
                    // they still have some distance, so this movement is ok
                    return CHECK_RES_OK;
                }
            }
            else
            {
                // many players on the road, return fail
                return CHECK_RES_UNREACHABLE;
            }
        }

        VECTOR::Vector3 Collider::RecalculatePos(VECTOR::Vector3& curPosition, VECTOR::Vector3& direction, VECTOR::Vector3& collidePos, float moveSpeed, int32 moveDelta)
        {
            // mapping function of the origin coordinate system and a new system formed by collidePos as origin point and curPosition as (0, -2)
            CoordinateMap mapFunc = MapCoordinateSystem(collidePos, curPosition);

            // length of the curve that the player passed
            float length = moveDelta * moveSpeed * 0.0005f / 1000;

            // the position in the new system after the movement
            VECTOR::Vector3 newPos(0.0f, curPosition.y, 0.0f);

            // the y axis of the new system
            auto delta = collidePos - curPosition;

            // simply explain: the x axis of the new system
            VECTOR::Vector3 vertical(delta.z, delta.y, -delta.x);

            // angle between the new point and curPosition
            float angle = length * 360 / (PI * (PLAYER_RADIUS * 2 * 2));

            // going left or right
            bool positive = vertical.dot(direction) > 0;

            newPos.x = (PLAYER_RADIUS * 2) * sin(angle) * (positive ? 1 : -1);
            newPos.z = (-1.0f) * sqrt((PLAYER_RADIUS * 2) * (PLAYER_RADIUS * 2) - newPos.x * newPos.x);

            // return the coordinates in the original system
            VECTOR::Vector3 finalPosss = mapFunc(newPos);
            // mLogInfo("calced pos, distance to current:"<<VECTOR::Distance(finalPosss, collidePos));
            return finalPosss;
        }

        // this function is used for mapping two coordinate system: the original one and the one formed by an origin and a point 
        // that should be (0, -2) in the new coordinate system.
        // And this will return a function that can calculate the coordinate in the original coordinate system while input the coordinate in the new system.
        CoordinateMap Collider::MapCoordinateSystem(VECTOR::Vector3& origin, VECTOR::Vector3& yminus2)
        {
            float Nan = nan("NaN");
            float k1;          //slope of the y axis  
            if (origin.x == yminus2.x)
            {
                k1 = Nan;
            }
            else
            {
                k1 = (origin.z - yminus2.z) / (origin.x - yminus2.x);      
            }
            
            float k2;          //slope of the x axis  
            if (k1 == Nan)
            {
                k2 = 0.0f;
            }
            else if (k1 == 0.0f)
            {
                k2 = Nan;
            }
            else
            {
                k2 = -1 / k1;
            }
            
            float b1 = origin.z - k1 * origin.x;
            float b2 = origin.z - k2 * origin.x;
            // VECTOR::Vector3 v(-1, 0, 0);
            // VECTOR::Vector3 v_x = origin - yminus2;

            // float cos_ = v_x.dot(v) / (v_x.Length() * v.Length());
            // float sin_ = sqrt(1 - cos_ * cos_);
            float flag_y = origin.z > yminus2.z ? 1.0f : -1.0f;
            float flag_x = origin.x > yminus2.x ? -1.0f : 1.0f;
            return [=, &origin, &yminus2](const VECTOR::Vector3& toTransform) 
            {
                VECTOR::Vector3 originCoord(0, toTransform.y, 0);

                float delta_y = sqrt(toTransform.x * toTransform.x + toTransform.x / k1 * toTransform.x / k1);
                delta_y = toTransform.x * flag_y > 0 ? -delta_y : delta_y;

                float delta_x = sqrt(toTransform.z * toTransform.z + toTransform.z / k2 * toTransform.z / k2);
                delta_x = toTransform.z * flag_x > 0 ? delta_x : -delta_x;

                // y axis of new system is vertical
                if (origin.x == yminus2.x)
                {
                    // y axis points up
                    if (origin.z > yminus2.z)
                    {
                        originCoord.x = toTransform.x + origin.x;
                        originCoord.z = toTransform.z + origin.z;
                    }
                    // y axis points down
                    else
                    {
                        originCoord.x = origin.x - toTransform.x;
                        originCoord.z = origin.z - toTransform.z;
                    }
                }
                // x axis of new system is vertical
                else if (origin.z == yminus2.z)
                {
                    // x axis points down
                    if (origin.x > yminus2.x)
                    {
                        originCoord.x = toTransform.z + origin.x;
                        originCoord.z = origin.z - toTransform.x;
                    }
                    // x axis points up
                    else
                    {
                        originCoord.x = origin.x - toTransform.z;
                        originCoord.z = origin.z + toTransform.x;
                    }
                }
                // calculate the origin coordinate by moving the x and y axis of the new system to get
                // new function of the two axises in the origin system and their cross point is the origin point.
                else
                {
                    originCoord.x = (b2 - b1 + k2 * delta_x - k1 * delta_y) / (k1 - k2);
                    originCoord.z = k1 * (originCoord.x + delta_y) + b1;
                }
                
                return originCoord;
            };
        }
    } // namespace physics
} // namespace msgame
