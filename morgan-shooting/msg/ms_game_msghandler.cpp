#include "ms_game_msghandler.h"
#include "ms_logger.h"

namespace msmessage
{
    namespace handler
    {
        void GameMsgHandler::Handle(int64 senderId, MessageBase *msg)
        {
            if (senderId == 0 || &msg == nullptr)
            {
                mLogError("unknown message!");
                return;
            }
            
            switch (msg->id)
            {
                case 2001:
                {
                    CSMove* csmove = reinterpret_cast<CSMove*>(msg);
                    Handle(senderId, *csmove);
                    break;
                }

                case 2003:
                {
                    CSJump* csjump = reinterpret_cast<CSJump*>(msg);
                    Handle(senderId, *csjump);
                    break;
                }

                case 2005:
                {
                    CSDash* csdash = reinterpret_cast<CSDash*>(msg);
                    Handle(senderId, *csdash);
                    break;
                }
            }
        }
    }
}