//
// Created by Milk on 4/10/21.
//

#include "IO.h"
#include "Log.h"

jmethodID getAbsolutePathMethodId;

list<IO::RelocateInfo> relocate_rule;

char *replace(const char *str, const char *src, const char *dst) {
    const char *pos = str;
    int count = 0;
    while ((pos = strstr(pos, src))) {
        count++;
        pos += strlen(src);
    }

    size_t result_len = strlen(str) + (strlen(dst) - strlen(src)) * count + 1;
    char *result = (char *) malloc(result_len);
    memset(result, 0, strlen(result));

    const char *left = str;
    const char *right = nullptr;

    while ((right = strstr(left, src))) {
        strncat(result, left, right - left);
        strcat(result, dst);
        right += strlen(src);
        left = right;
    }
    strcat(result, left);
    return result;
}

const char *IO::redirectPath(const char *__path) {
    list<IO::RelocateInfo>::iterator iterator;
    for (iterator = relocate_rule.begin(); iterator != relocate_rule.end(); ++iterator) {
        IO::RelocateInfo info = *iterator;
        if (strstr(__path, info.targetPath) && !strstr(__path, "/blackbox/")) {
            char *ret = replace(__path, info.targetPath, info.relocatePath);
            // ALOGD("redirectPath %s  => %s", __path, ret);
            return ret;
        }
    }
    return __path;
}

jstring IO::redirectPath(JNIEnv *env, jstring path) {
    return BoxCore::redirectPathString(env, path);
}

jobject IO::redirectPath(JNIEnv *env, jobject path) {
    return BoxCore::redirectPathFile(env, path);
}

void IO::addRule(const char *targetPath, const char *relocatePath) {
    IO::RelocateInfo info{};
    info.targetPath = targetPath;
    info.relocatePath = relocatePath;
    relocate_rule.push_back(info);
}

void IO::init(JNIEnv *env) {
    jclass tmpFile = env->FindClass("java/io/File");
    getAbsolutePathMethodId = env->GetMethodID(tmpFile, "getAbsolutePath", "()Ljava/lang/String;");
}
