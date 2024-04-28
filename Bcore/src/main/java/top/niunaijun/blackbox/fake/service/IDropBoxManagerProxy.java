package top.niunaijun.blackbox.fake.service;

import android.content.Context;
import android.os.DropBoxManager;
import android.os.IBinder;

import java.lang.reflect.Method;

import black.android.os.BRServiceManager;
import black.com.android.internal.os.BRIDropBoxManagerService;
import black.com.android.internal.os.BRIDropBoxManagerServiceStub;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;
import top.niunaijun.blackbox.utils.MethodParameterUtils;

/**
 * @author gm
 * @function
 * @date :2024/4/20 22:27
 **/
public class IDropBoxManagerProxy extends BinderInvocationStub {
    public IDropBoxManagerProxy() {
        super(BRServiceManager.get().getService(Context.DROPBOX_SERVICE));
    }

    @Override
    protected Object getWho() {
        return BRIDropBoxManagerServiceStub.get().asInterface(BRServiceManager.get().getService(Context.DROPBOX_SERVICE));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.DROPBOX_SERVICE);
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @ProxyMethod("getNextEntry")
    public static class unregisterAudioFocusClient extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return null;
        }
    }

    @ProxyMethod("getNextEntryWithAttribution")
    public static class getNextEntryWithAttribution extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return null;
        }
    }
}
