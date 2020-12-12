#include "ms_game_msghandler.h"
#include "game_session.h"

namespace msmessage
{
    namespace handler
    {
        void GameMsgHandler::Handle(int64 senderId, CSJump& msg)
        {
            auto scjump = new SCJump();
            scjump->playerId = senderId;
            std::vector<int32> ids = owner->GetPlayerIds(senderId);
            owner->SendMsg(ids, scjump);
            delete scjump;
        }
    }
}