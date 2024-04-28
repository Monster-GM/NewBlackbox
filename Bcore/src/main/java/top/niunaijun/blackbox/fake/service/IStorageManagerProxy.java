package top.niunaijun.blackbox.fake.service;

import android.os.Build;
import android.os.IInterface;
import android.os.storage.StorageVolume;

import java.io.File;
import java.lang.reflect.Method;

import black.android.os.BRServiceManager;
import black.android.os.mount.BRIMountServiceStub;
import black.android.os.storage.BRIStorageManagerStub;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;
import top.niunaijun.blackbox.utils.MethodParameterUtils;
import top.niunaijun.blackbox.utils.compat.BuildCompat;

/**
 * Created by Milk on 4/10/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class IStorageManagerProxy extends BinderInvocationStub {

    public IStorageManagerProxy() {
        super(BRServiceManager.get().getService("mount"));
    }

    @Override
    protected Object getWho() {
        IInterface mount;
        if (BuildCompat.isOreo()) {
            mount = BRIStorageManagerStub.get().asInterface(BRServiceManager.get().getService("mount"));
        } else {
            mount = BRIMountServiceStub.get().asInterface(BRServiceManager.get().getService("mount"));
        }
        return mount;
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService("mount");
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @ProxyMethod("getVolumeList")
    public static class GetVolumeList extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (args[0] instanceof Integer) {
                if (BuildCompat.isT()) {
                    args[0] = Integer.valueOf(BlackBoxCore.getHostUserId());
                } else {
                    args[0] = Integer.valueOf(BlackBoxCore.getHostUid());
                }
            }
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
//            if (args == null) {
//                StorageVolume[] volumeList = BlackBoxCore.getBStorageManager().getVolumeList(BlackBoxCore.getHostUid(), null, 0, BActivityThread.getUserId());
//                if (volumeList == null) {
//                    return method.invoke(who, args);
//                }
//                return volumeList;
//            }
//            try {
//                int uid = (int) args[0];
//                String packageName = (String) args[1];
//                int flags = (int) args[2];
//                StorageVolume[] volumeList = BlackBoxCore.getBStorageManager().getVolumeList(uid, packageName, flags, BActivityThread.getUserId());
//                if (volumeList == null) {
//                    return method.invoke(who, args);
//                }
//                return volumeList;
//            } catch (Throwable t) {
//                return method.invoke(who, args);
//            }
        }
    }

    @ProxyMethod("mkdirs")
    public static class mkdirs extends MethodHook {
        @Override
        protected Object beforeHook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return super.beforeHook(who, method, args);
        }

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                return method.invoke(who, args);
            }
            String path;
            if (args.length == 1) {
                path = (String) args[0];
            } else {
                path = (String) args[1];
            }
            File file = new File(path);
            if (!file.exists() && !file.mkdirs()) {
                return -1;
            }
            return 0;
        }
    }

    @ProxyMethod("getTotalBytes")
    public static class getTotalBytes extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("getCacheBytes")
    public static class getCacheBytes extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("getCacheQuotaBytes")
    public static class getCacheQuotaBytes extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (args[args.length - 1] instanceof Integer) {
                args[args.length - 1] = BlackBoxCore.getHostUid();
            }
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("queryStatsForUser")
    public static class queryStatsForUser extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastAppPkg(args);
            MethodParameterUtils.replaceLastUserId(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("queryExternalStatsForUser")
    public static class queryExternalStatsForUser extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastAppPkg(args);
            MethodParameterUtils.replaceLastUserId(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("queryStatsForUid")
    public static class queryStatsForUid extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastAppPkg(args);
            MethodParameterUtils.replaceLastUserId(args);
            return method.invoke(who, args);
        }
    }
}
