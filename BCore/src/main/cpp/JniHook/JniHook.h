#ifndef BLACKBOX_JNIHOOK_H
#define BLACKBOX_JNIHOOK_H

#include "ArtMethod.h"

#define HOOK_JNI(ret, func, ...) \
  ret (*orig_##func)(__VA_ARGS__); \
  ret new_##func(__VA_ARGS__)

class JniHook {
public:
    static void InitJniHook(JNIEnv *env, int api_level);
    static void HookJniFun(JNIEnv *env, const char *class_name, const char *method_name, const char *sign, void *new_fun, void **orig_fun, bool is_static);
    static void HookJniFun(JNIEnv *env, jobject java_method, void *new_fun, void **orig_fun, bool is_static);
};

#endif // BLACKBOX_JNIHOOK_H
