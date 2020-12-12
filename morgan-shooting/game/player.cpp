#include "player.h"
#include "ms_message.h"

namespace msgame
{
    namespace gameobject
    {
        Player::Player(int32 id, std::string& name, int32 number)
        :id_(id)
        ,name_(name)
        ,number_(number)
        {
            VECTOR::Vector3 v;

            //side == 2
            if (number_ % 2 == 0)
                v.z = 6.0f;
            else
                v.z = -6.0f;

            if (number_ / 2 == 1)
                v.x = 6.0f;
            else
                v.x = -6.0f;

            v.y = 0.7f;

            pos(v);
        }

        void Player::Pulse()
        {
        }

        msmessage::BPlayer Player::GetMsg()
        {
            msmessage::BPlayer m;
            m.playerId = id_;
            m.playerName = name_;
            m.side = number_ % 2 == 0 ? 1 : 2;
            m.curPos.x = pos_.x;
            m.curPos.y = pos_.y;
            m.curPos.z = pos_.z;
            return m;
        }
    }
}