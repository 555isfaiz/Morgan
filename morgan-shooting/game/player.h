#ifndef _ms_player_
#define _ms_player_

#include <string>
#include "ms_message.h"
#include "physics/ms_collider.h"
#include "ms_link_queue.h"
#include "ms_simple_task.h"

#define GAMEOBJECT msgame::gameobject

namespace msgame
{
    namespace gameobject
    {
        class Player
        {
        private:
            int32 id_;
            std::string name_;
            VECTOR::Vector3 pos_;

        public:
            int32 number_;
            Player(int32 id, std::string &name, int32 side);
            virtual ~Player() {}

            void Pulse();

            msmessage::BPlayer GetMsg();
            inline VECTOR::Vector3 &pos() { return pos_; }
            inline void pos(VECTOR::Vector3 &position) { pos_ = position; }
            inline int32 id() { return id_; }
            inline void id(int32 id) { id_ = id; }
        };
    } // namespace gameobject
} // namespace msgame

#endif