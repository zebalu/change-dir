/*
Copyright 2025 Balázs Zaicsek

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

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
