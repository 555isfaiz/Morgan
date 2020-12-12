#ifndef _ms_timer_
#define _ms_timer_

#include "ms_types.h"

namespace msutils
{
    enum
    {
        TYPE_ONCE = 0,
        TYPE_PERIOD,
    };

    class Timer
    {

    private:
        int32 type;
        int64 startTime;
        int64 lastTrigger;
        int64 interval;
        bool started;

    public:
        inline bool IsStarted(){ return started; }

        int64 NextElapse();
        bool IsTrigger();
        void StartUp();

        Timer(int32 type, int64 interval, bool delayStart);
        virtual ~Timer(){}

    };
    
}

#endif