#include <iostream>
#include <mutex>
#include <string.h>
#include "jniUtils.h"
#include "ms_logger.h"
#include "output_stream.h"

thread_local JNIEnv *obj_CurrentEnv;
std::once_flag _once_falg;

extern void setCurrentEnv(JNIEnv *env)
{
    obj_CurrentEnv = env;
}

extern JNIEnv *getCurrentEnv()
{
    return obj_CurrentEnv;
}

jlong msJniUtils::GetNtvPtr(JNIEnv* env, jobject jobj)
{
    if (jobj == nullptr)
    {
        return 0;
    }
    return env->GetLongField(jobj, obj_jniUtils.NtvObject_fieldPtr);
}

void msJniUtils::SetNtvPtr(JNIEnv *env, jobject jobj, void* ptr)
{
    env->SetLongField(jobj, obj_jniUtils.NtvObject_fieldPtr, (jlong)ptr);
}

void msJniUtils::Init(JNIEnv *env)
{
    std::call_once(_once_falg, [&] {
        obj_jniUtils.NtvObject_Class = env->FindClass("morgan/natives/NtvObject");
        if (obj_jniUtils.NtvObject_Class != nullptr)
            obj_jniUtils.NtvObject_Class = (jclass)env->NewGlobalRef(obj_jniUtils.NtvObject_Class);

        obj_jniUtils.NtvObject_fieldPtr = env->GetFieldID(obj_jniUtils.NtvObject_Class, "_ntvPtr", "J");
    });
}

msJniUtils &GetJniUtils()
{
    static msJniUtils s_obj_jniUtils;
    return s_obj_jniUtils;
}

void msJniUtils::SetNtvFunctions(JNIEnv *env, jobject jobj)
{
    auto jclz = env->GetObjectClass(jobj);
    this->NtvFuncs = env->NewGlobalRef(jobj);
    NtvFuncs_log = env->GetMethodID(jclz, "log", "(ILjava/lang/String;Ljava/lang/String;I)V");
    NtvFuncs_sendMsg = env->GetMethodID(jclz, "sendMsg", "(Lmorgan/natives/NtvGame;I[B)V");
    NtvFuncs_sendMsgMulti = env->GetMethodID(jclz, "sendMsgMulti", "(Lmorgan/natives/NtvGame;[I[B)V");
    msutils::logger::fLogger = &msJniUtils::defineLogger;
}

void msJniUtils::defineLogger(int type, const std::string &msg, const char *file, int line)
{

    if (obj_jniUtils.NtvFuncs == nullptr || obj_jniUtils.NtvFuncs_log == nullptr)
        return;

    auto env = getCurrentEnv();
    auto jmsg = env->NewStringUTF(msg.c_str());
    auto fileN = env->NewStringUTF(file);
    env->CallVoidMethod(obj_jniUtils.NtvFuncs, obj_jniUtils.NtvFuncs_log, type, jmsg, fileN, line);
    env->DeleteLocalRef(jmsg);
    env->DeleteLocalRef(fileN);
}

void msJniUtils::sendMsg(msgame::msgamesession::GameSession* game, int32 id, msmessage::MessageBase *msg)
{
    auto env = getCurrentEnv();
    if (this->NtvFuncs == nullptr || this->NtvFuncs_sendMsg == nullptr)
    {
        return;
    }
    
    jboolean isCopy = false;
    msutils::msstream::OutputStream out;
    out.write(msg);
    char* buf = out.GetBuffer();
    auto jbuf = env->NewByteArray(out.GetActualSize());
    auto ptr = env->GetByteArrayElements(jbuf, &isCopy);
    memcpy(ptr, buf, out.GetActualSize());
    env->ReleaseByteArrayElements(jbuf, ptr, JNI_COMMIT);
    env->CallVoidMethod(NtvFuncs, NtvFuncs_sendMsg, (jobject)game->GetJvObj(), id, jbuf);
    env->DeleteLocalRef(jbuf);
}

void msJniUtils::sendMsgMulti(msgame::msgamesession::GameSession* game, std::vector<int32>& ids, msmessage::MessageBase *msg)
{
    auto env = getCurrentEnv();
    if (this->NtvFuncs == nullptr || this->NtvFuncs_sendMsg == nullptr)
    {
        return;
    }
    
    jboolean isCopy = false;
    msutils::msstream::OutputStream out;
    out.write(msg);
    char* buf = out.GetBuffer();
    auto jbuf = env->NewByteArray(out.GetActualSize());
    auto ptr = env->GetByteArrayElements(jbuf, &isCopy);
    memcpy(ptr, buf, out.GetActualSize());
    env->ReleaseByteArrayElements(jbuf, ptr, JNI_COMMIT);
    
    auto jids = env->NewIntArray(ids.size());
    auto ptrI = env->GetIntArrayElements(jids, &isCopy);
    for (size_t i = 0; i < ids.size(); i++)
    {
        ptrI[i] = ids.at(i);
    }
    
    env->ReleaseIntArrayElements(jids, ptrI, JNI_COMMIT);

    env->CallVoidMethod(NtvFuncs, NtvFuncs_sendMsgMulti, (jobject)game->GetJvObj(), jids, jbuf);
    env->DeleteLocalRef(jbuf);
    env->DeleteLocalRef(jids);
}