#include "ms_game_msghandler.h"
#include "game_session.h"
#include "player.h"
#include "ms_dash_task.h"

namespace msmessage
{
    namespace handler
    {
        void GameMsgHandler::Handle(int64 senderId, CSDash& msg)
        {
            VECTOR::Vector3 direction(msg.direction);
            int64 duration = msg.duration;

            // add task
            GAMETASKS::DashTask* dashTask = new GAMETASKS::DashTask(owner, senderId, duration, direction);
            owner->AddLongTask(dashTask);

            SCDashStart* scmsg = new SCDashStart;
            scmsg->playerId = senderId;
            scmsg->direction = msg.direction;
            std::vector<int32> ids = owner->GetPlayerIds();
            owner->SendMsg(ids, scmsg);
            delete scmsg;
        }
    }
}