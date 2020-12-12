#ifndef _ms_linked_queue_
#define _ms_linked_queue_

#include "ms_types.h"

namespace msutils
{
    struct LQNode
    {
    public:
        LQNode *next_;
        void *val_;
    };

    template <class T>
    class MSLinkedQueue
    {
    private:
        int32 count_;
        LQNode head_;
        LQNode tail_;

    public:
        inline int32 Size() { return count_; }
        inline bool IsEmpty() { return count_ == 0; }

        const T &Peek()
        {
            return *reinterpret_cast<T *>(head_.next_->val_);
        }

        T Pop()
        {
            auto n = head_.next_;
            head_.next_ = n->next_;
            T *ptr = reinterpret_cast<T *>(n->val_);
            T val = T(*ptr);

            delete n;
            delete ptr;
            count_--;

            return val;
        }

        void Push(const T &t)
        {
            T *n_t = new T(t);
            LQNode *n_node = new LQNode();

            if (count_ == 0)
            {
                head_.next_ = n_node;
            }
            else
            {
                tail_.next_->next_ = n_node;
            }

            n_node->val_ = n_t;
            tail_.next_ = n_node;
            count_++;
        }

        MSLinkedQueue()
        {
            count_ = 0;
            head_ = LQNode();
            tail_ = LQNode();
        }

        ~MSLinkedQueue()
        {
            auto ptr = head_.next_;
            while (ptr->next_ != nullptr)
            {
                auto tmp = ptr;
                ptr = ptr->next_;
                T *t_val = reinterpret_cast<T *>(tmp->val_);
                delete tmp;
                delete t_val;
            }
        }
    };
} // namespace msutils

#endif