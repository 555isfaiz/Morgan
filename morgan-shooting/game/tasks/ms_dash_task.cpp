#include "ms_dash_task.h"
#include "ms_message.h"

namespace msgame
{
    namespace gametasks
    {
        DashTask::DashTask(GAMESESSION::GameSession* game, int32 playerId, int64 duration, VECTOR::Vector3 direction)
        : msutils::MSSimpleTask(game)
        , direction_(direction)
        , playerId_(playerId)
        {
            int64 now = msutils::GetNowMillSec();
            lastExec_ = now;
            endT_ = now + duration;
        }

        void DashTask::Execute()
        {
            int64 now = msutils::GetNowMillSec();
            GAMESESSION::GameSession* g = reinterpret_cast<GAMESESSION::GameSession*>(context_);
            GAMEOBJECT::Player* p = g->GetPlayer(playerId_);
            int64 diff = now - lastExec_;
            lastExec_ = now;

            int32 collideId = 0;
            int32 checkRes = g->MovementCheck(playerId_, direction_, collideId, DASH_SPEED, diff);
            if (checkRes != -2 && checkRes != 0)
            {
                finished_ = true;
            }
            else
            {
                VECTOR::Vector3 calPos = g->CalcMovePos(playerId_, direction_, collideId);
                p->pos(calPos);
            }

            if (now >= endT_)
            {
                finished_ = true;
            }
        }

        void DashTask::OnFinished()
        {
            GAMESESSION::GameSession *g = reinterpret_cast<GAMESESSION::GameSession *>(context_);
            GAMEOBJECT::Player *p = g->GetPlayer(playerId_);

            msmessage::SCDashStop *msg = new msmessage::SCDashStop;
            msg->playerId = playerId_;
            msg->finalPos = p->pos().getMsg3();
            std::vector<int32> ids = g->GetPlayerIds(playerId_);
            g->SendMsg(ids, msg);
            delete msg;
        }
    } // namespace gametasks
} // namespace msgame
