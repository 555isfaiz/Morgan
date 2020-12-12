#ifndef _ms_dash_task_
#define _ms_dash_task_

#include "ms_simple_task.h"
#include "game_session.h"

#define DASH_SPEED 20.0f
#define GAMETASKS msgame::gametasks

namespace msgame
{
    namespace gametasks
    {
        class DashTask : public msutils::MSSimpleTask
        {
        private:
            int32 playerId_;
            int64 lastExec_;
            int64 endT_;
            VECTOR::Vector3 direction_;

        public:
            void Execute() override;
            void OnFinished() override;

            DashTask(GAMESESSION::GameSession* game, int32 playerId, int64 duration, VECTOR::Vector3 direction);
            virtual ~DashTask() {}
        };
    } // namespace gametasks
} // namespace msgame


#endif