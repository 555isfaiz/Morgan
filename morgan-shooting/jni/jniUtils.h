#include <vector>
#include <jni.h>
#include <string>
#include "game_session.h"

struct msJniUtils
{
    jclass NtvObject_Class = nullptr;
    jfieldID NtvObject_fieldPtr = nullptr;
    jobject NtvFuncs = nullptr;

    jlong GetNtvPtr(JNIEnv *env, jobject jobj);
    void SetNtvPtr(JNIEnv *env, jobject jobj, void* ptr);

    static void Init(JNIEnv *env);
    static void defineLogger(int type, const std::string &msg, const char *file, int line);

    void sendMsg(msgame::msgamesession::GameSession* game, int32 id, msmessage::MessageBase *msg);
    void sendMsgMulti(msgame::msgamesession::GameSession* game, std::vector<int32>& ids, msmessage::MessageBase *msg);

    void SetNtvFunctions(JNIEnv *env, jobject jobj);

private:
    jmethodID NtvFuncs_log = nullptr;
    jmethodID NtvFuncs_sendMsg = nullptr;
    jmethodID NtvFuncs_sendMsgMulti = nullptr;
};

extern msJniUtils &GetJniUtils();
extern void setCurrentEnv(JNIEnv *env);
extern JNIEnv *getCurrentEnv();

#define obj_jniUtils GetJniUtils()
#define jenvdump(env)   \
    setCurrentEnv(env); \
    {
#define jenvpop() }
