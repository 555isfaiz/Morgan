#include <jni.h>
#include "morgan_natives_NtvManager.h"
#include "jniUtils.h"
#include "ms_mesh.h"
#include "ms_collider.h"

JNIEXPORT void JNICALL Java_morgan_natives_NtvManager_setNtvFunctions(JNIEnv * env, jclass jclz, jobject jobj)
{
    jenvdump(env);
    obj_jniUtils.SetNtvFunctions(env, jobj);
    jenvpop();
}

JNIEXPORT void JNICALL Java_morgan_natives_NtvManager_loadMeshes(JNIEnv * env, jclass jclz, jstring path)
{
    jenvdump(env);
    auto strptr = env->GetStringUTFChars(path, 0);
    auto cpath = std::string(strptr);
    PHYSICS::LoadFromObj(cpath);
    env->ReleaseStringUTFChars(path, strptr);
    jenvpop();
}

JNIEXPORT void JNICALL Java_morgan_natives_NtvManager_jniTest(JNIEnv * env, jclass jclz)
{
    jenvdump(env);
    PHYSICS::Collider co(NULL);
    VECTOR::Vector3 origin(1, 0, 1);
    VECTOR::Vector3 yminus2(-1, 0, 0);
    auto map = co.MapCoordinateSystem(origin, yminus2);
    VECTOR::Vector3 toTransform(-1, 0, 0);
    auto p = map(toTransform);
    jenvpop();
}