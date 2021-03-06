#ifndef _ms_messages_
#define _ms_messages_

#include <vector>
#include <string>
#include "ms_types.h"

namespace msutils
{
    namespace msstream
    {
        class OutputStream;
        class InputStream;
    }
}

namespace msmessage
{
    class MessageBase
    {
    public:
        int64 id;
        virtual void write(msutils::msstream::OutputStream* out){}
        virtual void read(msutils::msstream::InputStream* in){}
        MessageBase(){}
        virtual ~MessageBase(){}
    };

    class BVector2 : public MessageBase 
    {
    public:
        float x;
        float y;

        void write(msutils::msstream::OutputStream* out) override ;
        void read(msutils::msstream::InputStream* in) override ;
        BVector2(){ id = 102; }
        virtual ~BVector2(){}
    };

    class BVector3 : public MessageBase 
    {
    public:
        float x;
        float y;
        float z;

        void write(msutils::msstream::OutputStream* out) override ;
        void read(msutils::msstream::InputStream* in) override ;
        BVector3(){ id = 103; }
        virtual ~BVector3(){}
    };

    class BPlayer : public MessageBase 
    {
    public:
        int32 playerId;
        std::string playerName;
        int32 side;
        BVector3 curPos;

        void write(msutils::msstream::OutputStream* out) override ;
        void read(msutils::msstream::InputStream* in) override ;
        BPlayer(){ id = 101; }
        virtual ~BPlayer(){}
    };

    class CSLogin : public MessageBase 
    {
    public:
        bool isShooter;

        void write(msutils::msstream::OutputStream* out) override ;
        void read(msutils::msstream::InputStream* in) override ;
        CSLogin(){ id = 1001; }
        virtual ~CSLogin(){}
    };

    class SCJoinGame : public MessageBase 
    {
    public:
        int32 sessionId;
        int32 mySide;
        std::vector<BPlayer> players;

        void write(msutils::msstream::OutputStream* out) override ;
        void read(msutils::msstream::InputStream* in) override ;
        SCJoinGame(){ id = 1003; }
        virtual ~SCJoinGame(){}
    };

    class SCLogin : public MessageBase 
    {
    public:
        int32 playerId;

        void write(msutils::msstream::OutputStream* out) override ;
        void read(msutils::msstream::InputStream* in) override ;
        SCLogin(){ id = 1004; }
        virtual ~SCLogin(){}
    };

    class SCGameSync : public MessageBase 
    {
    public:
        int32 sessionId;
        std::vector<BPlayer> players;

        void write(msutils::msstream::OutputStream* out) override ;
        void read(msutils::msstream::InputStream* in) override ;
        SCGameSync(){ id = 1005; }
        virtual ~SCGameSync(){}
    };

    class CSMove : public MessageBase 
    {
    public:
        int32 playerId;
        BVector3 curPos;
        BVector3 direction;
        int64 timeStamp;

        void write(msutils::msstream::OutputStream* out) override ;
        void read(msutils::msstream::InputStream* in) override ;
        CSMove(){ id = 2001; }
        virtual ~CSMove(){}
    };

    class SCMove : public MessageBase 
    {
    public:
        int32 playerId;
        BVector3 curPos;
        BVector3 direction;
        int64 timeStamp;
        int32 result;

        void write(msutils::msstream::OutputStream* out) override ;
        void read(msutils::msstream::InputStream* in) override ;
        SCMove(){ id = 2002; }
        virtual ~SCMove(){}
    };

    class CSJump : public MessageBase 
    {
    public:
        int32 playerId;

        void write(msutils::msstream::OutputStream* out) override ;
        void read(msutils::msstream::InputStream* in) override ;
        CSJump(){ id = 2003; }
        virtual ~CSJump(){}
    };

    class SCJump : public MessageBase 
    {
    public:
        int32 playerId;

        void write(msutils::msstream::OutputStream* out) override ;
        void read(msutils::msstream::InputStream* in) override ;
        SCJump(){ id = 2004; }
        virtual ~SCJump(){}
    };

    class CSDash : public MessageBase 
    {
    public:
        BVector3 direction;
        int64 duration;

        void write(msutils::msstream::OutputStream* out) override ;
        void read(msutils::msstream::InputStream* in) override ;
        CSDash(){ id = 2005; }
        virtual ~CSDash(){}
    };

    class SCDashStart : public MessageBase 
    {
    public:
        int32 playerId;
        BVector3 direction;

        void write(msutils::msstream::OutputStream* out) override ;
        void read(msutils::msstream::InputStream* in) override ;
        SCDashStart(){ id = 2006; }
        virtual ~SCDashStart(){}
    };

    class SCDashStop : public MessageBase 
    {
    public:
        int32 playerId;
        BVector3 finalPos;

        void write(msutils::msstream::OutputStream* out) override ;
        void read(msutils::msstream::InputStream* in) override ;
        SCDashStop(){ id = 2007; }
        virtual ~SCDashStop(){}
    };



    MessageBase* GetMsgById(int64 id);
}

#endif