#include <jni.h>
#include <vector>
#include "morgan_natives_NtvGame.h"
#include "ms_logger.h"
#include "jniUtils.h"
#include "output_stream.h"
#include "input_stream.h"
#include "game_session.h"

using namespace msgame::msgamesession;

JNIEXPORT void JNICALL Java_morgan_natives_NtvGame_create(JNIEnv *env, jobject jobj, jint sessionId)
{
    jenvdump(env);
    msJniUtils::Init(env);

    auto g = new GameSession(sessionId);
    g->SetJvObj(env->NewWeakGlobalRef(jobj));
    obj_jniUtils.SetNtvPtr(env, jobj, g);
    jenvpop();
}

JNIEXPORT void JNICALL Java_morgan_natives_NtvGame_pulse(JNIEnv *env, jobject jobj)
{
    jenvdump(env);
    auto g = reinterpret_cast<GameSession*>(obj_jniUtils.GetNtvPtr(env, jobj));
    if (g == nullptr)
    {
        //should throw exception...
        mLogError("nullptr of GameSession!");
        return;
    }
    g->Pulse();
    jenvpop();
}

JNIEXPORT void JNICALL Java_morgan_natives_NtvGame_handleMsg(JNIEnv *env, jobject jobj, jint senderId, jbyteArray msgBuf)
{
    jenvdump(env);
    jboolean isCopy = false;
    auto g = reinterpret_cast<GameSession*>(obj_jniUtils.GetNtvPtr(env, jobj));
    auto buffer = env->GetByteArrayElements(msgBuf, &isCopy);
    char* buf = (char*)buffer;
    jsize size = env->GetArrayLength(msgBuf);
    STREAM::InputStream in(buf, size);
    msmessage::MessageBase* msg = in.ReadMsg();
    g->HandleMsg(senderId, msg);
    delete msg;
    jenvpop();
}

JNIEXPORT void JNICALL Java_morgan_natives_NtvGame_addPlayer(JNIEnv *env, jobject jobj, jint playerId, jstring playerName)
{
    jenvdump(env);
    auto g = reinterpret_cast<GameSession*>(obj_jniUtils.GetNtvPtr(env, jobj));

    auto strptr = env->GetStringUTFChars(playerName, 0);
    auto string = std::string(strptr);
    g->AddPlayer(playerId, string);
    env->ReleaseStringUTFChars(playerName, strptr);
    jenvpop();
}

JNIEXPORT void JNICALL Java_morgan_natives_NtvGame_initGame(JNIEnv *env, jobject jobj, jintArray playerIds, jobjectArray playerNames)
{
    jenvdump(env);
    jboolean isCopy = false;
    auto g = reinterpret_cast<GameSession*>(obj_jniUtils.GetNtvPtr(env, jobj));
    std::vector<int32> ids;
    auto idSize = env->GetArrayLength(playerIds);
    for (int32 i = 0; i < idSize; i++)
    {
        ids.push_back(env->GetIntArrayElements(playerIds, 0)[i]);
    }
    

    std::vector<std::string> names;
    auto nameSize = env->GetArrayLength(playerNames);
    for (int32 i = 0; i < nameSize; i++)
    {
        auto jstr = (jstring)env->GetObjectArrayElement(playerNames, i);
        auto strptr = env->GetStringUTFChars(jstr, 0);
        names.push_back(strptr);
        env->ReleaseStringUTFChars(jstr, strptr);
    }
    
    g->InitGame(ids, names);
    jenvpop();
}

JNIEXPORT void JNICALL Java_morgan_natives_NtvGame_removePlayer(JNIEnv *env, jobject jobj, jint playerId)
{
    jenvdump(env);
    auto g = reinterpret_cast<GameSession*>(obj_jniUtils.GetNtvPtr(env, jobj));
    jenvpop();
}

JNIEXPORT void JNICALL Java_morgan_natives_NtvGame_destroy(JNIEnv *env, jobject jobj)
{
    jenvdump(env);
    auto g = reinterpret_cast<GameSession*>(obj_jniUtils.GetNtvPtr(env, jobj));
    if (g == nullptr)
    {
        return;
    }
    int sessionId = g->SessionId();
    if (auto jvPtr = g->GetJvObj())
    {
        env->DeleteWeakGlobalRef((jweak)jvPtr);
        g->SetJvObj(nullptr);
    }

    obj_jniUtils.SetNtvPtr(env, jobj, nullptr);
    delete g;
    
    mLogInfo("game session destroyed, session Id:"<<sessionId);
    jenvpop();
}
