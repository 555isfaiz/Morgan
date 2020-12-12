#include <stdlib.h>
#include "game_session.h"
#include "ms_logger.h"
#include "jniUtils.h"

namespace msgame
{
    namespace msgamesession
    {
        GameSession::GameSession(int32 sessionId)
        :sessionId_(sessionId)
        ,curPhase_(PHASE_PREPARE)
        {
            msgHandler_ = new msmessage::handler::GameMsgHandler(this);
            collider_ = new msgame::physics::Collider(this);
            createTime_ = msutils::GetNowMillSec();
        }

        void GameSession::Pulse()
        {
            while (!oncePipe_.IsEmpty())
            {
                auto t = oncePipe_.Pop();
                t.Execute();
            }

            auto iter = longPipe_.begin();
            while (iter != longPipe_.end())
            {
                (*iter)->Execute();
                if ((*iter)->IsFinished())
                {
                    (*iter)->OnFinished();
                    iter = longPipe_.erase(iter);
                    continue;
                }
                iter++;
            }
            
            PulseHuman();

            //SCGameSync   sync in the end of the pulse
            {
                std::vector<int32> ids;
                auto msg = new msmessage::SCGameSync;
                for (auto iter = players_.begin(); iter != players_.end(); iter++)
                {
                    ids.push_back(iter->first);
                    msg->players.push_back(iter->second->GetMsg());
                }
                msg->sessionId = sessionId_;
                SendMsg(ids, msg);
                delete msg;
            }
        }

        void GameSession::PulseHuman()
        {
            for (auto iter = players_.begin(); iter != players_.end(); iter++)
            {
                iter->second->Pulse();
            }
        }

        //deprecated
        void GameSession::AddPlayer(int32 id, std::string& name)
        {
            // if (curPhase != PHASE_PREPARE)
            // {
            //     mLogError("game started already, sessionId:"<<sessionId<<"playerId:"<<id);
            //     return;
            // }
            
            // if (players.count(id) != 0)
            // {
            //     mLogError("player already exist, sessionId:"<<sessionId<<"playerId:"<<id);
            //     return;
            // }
            
            // gameobject::Player* p = new gameobject::Player(id, name);
            // players.insert(std::pair<int32, gameobject::Player>(id, *p));
            // mLogInfo("player joined game! sessionId:"<<sessionId<<", playerId:"<<id<<", playerName:"<<name);
        }

        void GameSession::InitGame(std::vector<int32>& ids, std::vector<std::string>& names)
        {
            msmessage::SCJoinGame *msg = new msmessage::SCJoinGame;
            msg->sessionId = sessionId_;
            int32 sideOnes = 0;
            
            int32 index[] = {0, 0, 0, 0};
            for (int32 i = 0; i < ids.size(); i++)
            {
                int32 id = ids.at(i);
                std::string name = names.at(i);

                int32 number = 0;

                rand:
                number = rand() % 4 + 1;
                if (index[number - 1] == 1)
                {
                    goto rand;
                } 
                else
                {
                    index[number - 1] = 1;
                }
                
                GAMEOBJECT::Player *p = new GAMEOBJECT::Player(id, name, number);
                players_.insert(std::pair<int32, gameobject::Player*>(id, p));
                msg->players.push_back(p->GetMsg());
                mLogInfo("player joined game! sessionId:"<<sessionId_<<", playerId:"<<id<<", playerName:"<<name);
            }

            for (int32 i = 0; i < ids.size(); i++)
            {
                msg->mySide = (players_.find(ids.at(i))->second->number_) % 2 == 0 ? 1 : 2;
                SendMsg(ids.at(i), msg);
            }

            delete msg;
        }

        std::vector<int32> GameSession::GetPlayerIds()
        {
            return GetPlayerIds(-1);
        }

        std::vector<int32> GameSession::GetPlayerIds(int32 exclude)
        {
            std::vector<int32> ids;
            for (auto pair : players_)
            {
                if (pair.first == exclude)
                {
                    continue;
                }
                
                ids.push_back(pair.first);
            }
            return ids;
        }

        void GameSession::RemovePlayer(int32 id)
        {
            players_.erase(id);

            // sync to client
        }

        // minus errorcode stands for movement failure, otherwise status limit.
        // if this move failed because of colliding with another player, collideId will be assigned
        int32 GameSession::MovementCheck(int32 playerId, VECTOR::Vector3& direction, int32& collideId, float moveSpeed, int32 moveDelta)
        {
            // player status check
            // ...

            // collide check
            int32 result = collider_->CollideCheck(playerId, direction, collideId, moveSpeed, moveDelta);
            switch (result)
            {
            case PHYSICS::CHECK_RES_UNREACHABLE:
                return -1;

            case PHYSICS::CHECK_RES_RECALC:
                return -2;

            default:
                break;
            }

            return 0;
        }

        VECTOR::Vector3 GameSession::CalcMovePos(int32 playerId, VECTOR::Vector3& direction, int32 collideId, float moveSpeed, int32 moveDelta)
        {
            VECTOR::Vector3 playerPos = players_[playerId]->pos();
            if (collideId == 0)
            {
                return VECTOR::MoveTo(playerPos, direction, moveSpeed, moveDelta);
            }
            else
            {
                VECTOR::Vector3 colliderPos = players_[collideId]->pos();
                if ((colliderPos - playerPos).dot(direction) <= 0)
                {
                    return VECTOR::MoveTo(playerPos, direction, moveSpeed, moveDelta);
                }
                return collider_->RecalculatePos(playerPos, direction, colliderPos, moveSpeed, moveDelta);
            }
        }

        void GameSession::SendMsg(int32 id, msmessage::MessageBase *msg)
        {
            // TODO: should pack msgs before call jni methods to reduce expands
            obj_jniUtils.sendMsg(this, id, msg);
        }

        void GameSession::SendMsg(std::vector<int32>& ids, msmessage::MessageBase *msg)
        {
            obj_jniUtils.sendMsgMulti(this, ids, msg);
        }

        void GameSession::HandleMsg(int32 senderId, msmessage::MessageBase* msg)
        {
            msgHandler_->Handle(senderId, msg);
        }

        GameSession::~GameSession()
        {
            delete msgHandler_;
            delete collider_;
        }
    }
}