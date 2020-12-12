#ifndef _ms_simple_task_
#define _ms_simple_task_

namespace msutils
{
    class MSSimpleTask
    {
    protected:
        void* context_;
        bool finished_;

    public:
        virtual void Execute() {}
        virtual void OnFinished() {}
        inline bool IsFinished() { return finished_; }

        MSSimpleTask(void* context) : context_(context) { finished_ = false; }
        virtual ~MSSimpleTask() {}
    };
} // namespace msutils

#endif 