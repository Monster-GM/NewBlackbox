package top.niunaijun.blackbox.fake.service;

import android.os.IInterface;

import java.lang.reflect.Method;

import black.android.os.BRServiceManager;
import black.android.view.BRIWindowManagerStub;
import black.android.view.BRWindowManagerGlobal;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;
import top.niunaijun.blackbox.utils.MethodParameterUtils;

/**
 * Created by Milk on 4/6/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class IWindowManagerProxy extends BinderInvocationStub {
    public static final String TAG = "WindowManagerStub";

    public IWindowManagerProxy() {
        super(BRServiceManager.get().getService("window"));
    }

    @Override
    protected Object getWho() {
        return BRIWindowManagerStub.get().asInterface(BRServiceManager.get().getService("window"));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService("window");
        BRWindowManagerGlobal.get()._set_sWindowManagerService(null);
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @ProxyMethod("openSession")
    public static class OpenSession extends BasePatchSession {
//        @Override
//        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
//            IInterface session = (IInterface) method.invoke(who, args);
//            IWindowSessionProxy IWindowSessionProxy = new IWindowSessionProxy(session);
//            IWindowSessionProxy.injectHook();
//            return IWindowSessionProxy.getProxyInvocation();
//        }
    }

    @ProxyMethod("setAppStartingWindow")
    public static class setAppStartingWindow extends BasePatchSession {

    }

    @ProxyMethod("overridePendingAppTransitionInPlace")
    public static class overridePendingAppTransitionInPlace extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (args[0] instanceof String) {
                args[0] = BlackBoxCore.getHostPkg();
            }
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("overridePendingAppTransition")
    public static class overridePendingAppTransition extends BasePatchSession {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (args[0] instanceof String) {
                args[0] = BlackBoxCore.getHostPkg();
            }
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("addAppToken")
    public static class addAppToken extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("setScreenCaptureDisabled")
    public static class setScreenCaptureDisabled extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("isPackageWaterfallExpanded")
    public static class isPackageWaterfallExpanded extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }

    abstract static class BasePatchSession extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            IInterface session = (IInterface) method.invoke(who, args);
            IWindowSessionProxy IWindowSessionProxy = new IWindowSessionProxy(session);
            IWindowSessionProxy.injectHook();
            return IWindowSessionProxy.getProxyInvocation();
        }
    }
}
