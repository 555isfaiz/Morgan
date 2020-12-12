#ifndef _ms_game_msghandler_
#define _ms_game_msghandler_

#include "ms_types.h"
#include "ms_message.h"

#define MSGHANDLER msmessage::handler

namespace msgame
{
    namespace msgamesession
    {
        class GameSession;
    }
}

namespace msmessage
{
    namespace handler
    {
        class GameMsgHandler
        {
        private:
            msgame::msgamesession::GameSession* owner;
        public:
            void Handle(int64 senderId, MessageBase *msg);
        public:
            void Handle(int64 senderId, CSMove& msg);
            void Handle(int64 senderId, CSJump& msg);
            void Handle(int64 senderId, CSDash& msg);
            GameMsgHandler(msgame::msgamesession::GameSession* owner) : owner(owner) {}
            virtual ~GameMsgHandler(){}
        };
    }
}

#endif