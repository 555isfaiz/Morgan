#include "ms_timer.h"
#include "ms_utils.h"

namespace msutils
{
    Timer::Timer(int32 type, int64 interval, bool delayStart)
     :type(type % 2), 
      interval(interval), 
      started(!delayStart)
    {
        if (!delayStart)
        {
            StartUp();            
        }
    }

    int64 Timer::NextElapse()
    {
        if (type == TYPE_ONCE)
        {
            return startTime + interval - GetNowMillSec();
        } 
        else 
        {
            return lastTrigger + interval - GetNowMillSec();
        }
    }

    bool Timer::IsTrigger()
    {
        int64 now = GetNowMillSec();
        bool flag = (lastTrigger + interval) <= now;

        if (flag && type == TYPE_PERIOD)
        {
            lastTrigger = now;
        }
        return flag;
    }

    void Timer::StartUp()
    {
        startTime = GetNowMillSec();
        if (type == TYPE_PERIOD)
        {
            lastTrigger = startTime;
        }
    }
}