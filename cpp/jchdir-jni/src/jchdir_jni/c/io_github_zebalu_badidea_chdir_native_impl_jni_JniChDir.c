#include "io_github_zebalu_badidea_chdir_native_impl_jni_JniChDir.h"

#ifdef _WIN32
    #include <direct.h>
#else
    #include <unistd.h>
#endif

JNIEXPORT jint JNICALL Java_io_github_zebalu_badidea_chdir_native_1impl_jni_JniChDir_chDir(JNIEnv *env, jobject jobj, jstring jstr) {
    const char *nativeString = (*env)->GetStringUTFChars(env, jstr, NULL);
    int result;
    #ifdef _WIN32
        result = _chdir(nativeString);
    #else
        result = chdir(nativeString);
    #endif
    (*env)->ReleaseStringUTFChars(env, jstr, nativeString);
    return result;
}
