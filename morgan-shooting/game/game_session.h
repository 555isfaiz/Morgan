#ifndef _ms_gamesession_
#define _ms_gamesession_

#include <vector>
#include <unordered_map>
#include "player.h"
#include "ms_game_msghandler.h"

#define GAMESESSION msgame::msgamesession

namespace msgame
{
    namespace msgamesession
    {
        enum
        {
            PHASE_PREPARE = 0,
            PHASE_GAMEING,
            PHASE_END,
        };

        typedef msutils::MSLinkedQueue<msutils::MSSimpleTask> OnceTasksPipe;
        typedef std::vector<msutils::MSSimpleTask*> LongTasksPipe;

        class GameSession
        {
        private:
            int32 sessionId_;
            int32 curPhase_;
            int64 createTime_;
            void* jvobj_prt_;

            MSGHANDLER::GameMsgHandler* msgHandler_;
            PHYSICS::Collider* collider_;
            std::unordered_map<int32, GAMEOBJECT::Player*> players_;
            OnceTasksPipe oncePipe_;
            LongTasksPipe longPipe_;

            void PulseHuman();

        public:
            inline void* GetJvObj(){ return jvobj_prt_; }
            inline void SetJvObj(void* ptr){ jvobj_prt_ = ptr; }
            inline int SessionId(){ return sessionId_; }

            void Pulse();
            inline void AddOnceTask(msutils::MSSimpleTask t) { oncePipe_.Push(t); }
            inline void AddLongTask(msutils::MSSimpleTask* t) { longPipe_.push_back(t); }
            void AddPlayer(int32 id, std::string& name);
            void InitGame(std::vector<int32>& ids, std::vector<std::string>& names);
            std::vector<int32> GetPlayerIds();
            std::vector<int32> GetPlayerIds(int32 exclude);
            inline GAMEOBJECT::Player* GetPlayer(int32 id) { return players_[id]; }
            inline std::unordered_map<int32, GAMEOBJECT::Player*>& GetPlayers() { return players_; }
            void RemovePlayer(int32 id);
            int32 MovementCheck(int32 playerId, VECTOR::Vector3& direction, int32& collideId, float moveSpeed = MOVE_SPEED, int32 moveDelta = MOVE_DELTA);
            VECTOR::Vector3 CalcMovePos(int32 playerId, VECTOR::Vector3& direction, int32 collideId, float moveSpeed = MOVE_SPEED, int32 moveDelta = MOVE_DELTA);

            void SendMsg(int32 id, msmessage::MessageBase *msg);
            void SendMsg(std::vector<int32>& ids, msmessage::MessageBase *msg);
            void HandleMsg(int32 senderId, msmessage::MessageBase* msg);

            GameSession(int32 sessionId_);
            virtual ~GameSession();
        };
    }
}

#endif