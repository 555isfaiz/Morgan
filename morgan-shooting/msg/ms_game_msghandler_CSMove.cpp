#include "ms_game_msghandler.h"
#include "game_session.h"
#include "ms_logger.h"
#include "ms_utils.h"

namespace msmessage
{
    namespace handler
    {
        void GameMsgHandler::Handle(int64 senderId, CSMove& msg)
        {
            //check move 

            //caculate new pos
            auto player = owner->GetPlayer(msg.playerId);
            VECTOR::Vector3 posBefore = VECTOR::Vector3(player->pos());
            VECTOR::Vector3 posCli = VECTOR::Vector3(msg.curPos);
            VECTOR::Vector3 direction = VECTOR::Vector3(msg.direction);
            int64 now = msutils::GetNowMillSec();
            int64 deltaTime = now - msg.timeStamp;
            VECTOR::Vector3 finalPos;
            int32 checkRes = 0;
            auto scmove = new SCMove;

            // if ping is higher than 200ms
            if (deltaTime > 200)
            {
                //discard this movement
                finalPos = posBefore;
                scmove->result = 2;
            }
            else
            {
                int32 collideId = 0;
                checkRes = owner->MovementCheck(senderId, direction, collideId);
                if (checkRes != -2 && checkRes != 0)
                {
                    float dist = VECTOR::Distance(player->pos(), posCli);
                    if (dist <= 1)
                    {
                        finalPos = posCli;
                    }
                    else
                    {
                        finalPos = player->pos();
                    }
                    goto end;
                }
                
                VECTOR::Vector3 calPos = owner->CalcMovePos(senderId, direction, collideId);
                //pull back if the plane distance of calculated pos and client pos is bigger than 1
                if (VECTOR::Distance(calPos.TwoDlize(), posCli.TwoDlize()) > 1) //TBD
                {
                    finalPos = calPos;
                }
                else
                {
                    finalPos = posCli;
                }
            }
            
            // mLogInfo("pos from client, x:"<<msg.curPos.x<<" y:"<<msg.curPos.y<<" z:"<<msg.curPos.z<<", finalPos, x:"<<finalPos.x<<" y:"<<finalPos.y<<" z:"<<finalPos.z);
            player->pos(finalPos); 

            end:
            scmove->playerId = senderId;
            scmove->curPos.x = finalPos.x;
            scmove->curPos.y = finalPos.y;
            scmove->curPos.z = finalPos.z;
            scmove->direction.x = msg.direction.x;
            scmove->direction.y = msg.direction.y;
            scmove->direction.z = msg.direction.z;
            scmove->timeStamp = now;
            scmove->result = checkRes;
            std::vector<int32> ids = owner->GetPlayerIds();
            owner->SendMsg(ids, scmove);
            delete scmove;
        }
    }
}