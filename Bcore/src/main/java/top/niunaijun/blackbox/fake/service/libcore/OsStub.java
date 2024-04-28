package top.niunaijun.blackbox.fake.service.libcore;

import android.os.Process;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import black.libcore.io.BRLibcore;
import black.libcore.io.BROs;
import black.libcore.io.Os;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.core.IOCore;
import top.niunaijun.blackbox.fake.hook.ClassInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;
import top.niunaijun.blackbox.utils.Reflect;
import top.niunaijun.blackbox.utils.Reflector;

/**
 * Created by Milk on 4/9/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class OsStub extends ClassInvocationStub {
    public static final String TAG = "OsStub";
    private Object mBase;

    public OsStub() {
        mBase = BRLibcore.get().os();
    }

    @Override
    protected Object getWho() {
        return mBase;
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        BRLibcore.get()._set_os(proxyInvocation);
    }

    @Override
    protected void onBindMethod() {
    }

    @Override
    public boolean isBadEnv() {
        return BRLibcore.get().os() != getProxyInvocation();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                if (args[i] == null)
                    continue;
                if (args[i] instanceof String && ((String) args[i]).startsWith("/")) {
                    String orig = (String) args[i];
                    args[i] = IOCore.get().redirectPath(orig);
//                    if (!ObjectsCompat.equals(orig, args[i])) {
//                        Log.d(TAG, "redirectPath: " + orig + "  => " + args[i]);
//                    }
                }
            }
        }
        return super.invoke(proxy, method, args);
    }

    @ProxyMethod("getuid")
    public static class getuid extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            int callUid = (int) method.invoke(who, args);
            return getFakeUid(callUid);
        }
    }

    @ProxyMethod("stat")
    public static class stat extends MethodHook {
        private static Field st_uid;

        static {
            try {
                Method stat =  BROs.getRealClass().getMethod("stat", String.class);
                Class<?> StructStat = stat.getReturnType();
                st_uid = StructStat.getDeclaredField("st_uid");
                st_uid.setAccessible(true);
            } catch (Throwable e) {
                throw new IllegalStateException(e);
            }
        }

        @Override
        protected Object afterHook(Object result) throws Throwable {
            int uid = (int) st_uid.get(result);
            if (uid == BlackBoxCore.getHostUid()) {
                st_uid.set(result, BActivityThread.getBAppId());
            }
            return result;
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Object invoke = null;
            try {
                invoke = method.invoke(who, args);
            } catch (Throwable e) {
                throw e.getCause();
            }
            Reflector.with(invoke).field("st_uid").set(getFakeUid(-1));
            return invoke;
        }
    }

    @ProxyMethod("lstat")
    public static class lstat extends MethodHook {

        @Override
        protected Object afterHook(Object result) throws Throwable {
            if (result != null) {
                Reflect pwd = Reflect.on(result);
                int uid = pwd.get("st_uid");
                if (uid == BlackBoxCore.getHostUid()) {
                    pwd.set("st_uid", BActivityThread.getBUid());
                }
            }
            return result;
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Object invoke = null;
            try {
                invoke = method.invoke(who, args);
            } catch (Throwable e) {
                throw e.getCause();
            }
            Reflector.with(invoke).field("st_uid").set(getFakeUid(-1));
            return invoke;
        }
    }

    @ProxyMethod("fstat")
    public static class fstat extends MethodHook {

        @Override
        protected Object afterHook(Object result) throws Throwable {
            if (result != null) {
                Reflect pwd = Reflect.on(result);
                int uid = pwd.get("st_uid");
                if (uid == BlackBoxCore.getHostUid()) {
                    pwd.set("st_uid", BActivityThread.getBUid());
                }
            }
            return result;
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Object invoke = null;
            try {
                invoke = method.invoke(who, args);
            } catch (Throwable e) {
                throw e.getCause();
            }
            Reflector.with(invoke).field("st_uid").set(getFakeUid(-1));
            return invoke;
        }
    }

    @ProxyMethod("getpwnam")
    public static class getpwnam extends MethodHook {

        @Override
        protected Object afterHook(Object result) throws Throwable {
            if (result != null) {
                Reflect pwd = Reflect.on(result);
                int uid = pwd.get("pw_uid");
                if (uid == BlackBoxCore.getHostUid()) {
                    pwd.set("pw_uid", BActivityThread.getBUid());
                }
            }
            return result;
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Object invoke = null;
            try {
                invoke = method.invoke(who, args);
            } catch (Throwable e) {
                throw e.getCause();
            }
            Reflector.with(invoke).field("pw_uid").set(getFakeUid(-1));
            return invoke;
        }
    }


    @ProxyMethod("getsockoptUcred")
    public static class getsockoptUcred extends MethodHook {

        @Override
        protected Object afterHook(Object result) throws Throwable {
            if (result != null) {
                Reflect ucred = Reflect.on(result);
                int uid = ucred.get("uid");
                if (uid == BlackBoxCore.getHostUid()) {
                    ucred.set("uid", BActivityThread.getBUid());
                }
            }
            return result;
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Object invoke = null;
            try {
                invoke = method.invoke(who, args);
            } catch (Throwable e) {
                throw e.getCause();
            }
            Reflector.with(invoke).field("uid").set(getFakeUid(-1));
            return invoke;
        }
    }



    private static int getFakeUid(int callUid) {
        if (callUid > 0 && callUid <= Process.FIRST_APPLICATION_UID)
            return callUid;
//            Log.d(TAG, "getuid: " + BActivityThread.getAppPackageName() + ", " + BActivityThread.getAppUid());
        if (BActivityThread.isThreadInit() && BActivityThread.currentActivityThread().isInit()) {
            return BActivityThread.getBAppId();
        } else {
            return BlackBoxCore.getHostUid();
        }
    }
}
