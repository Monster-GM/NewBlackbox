package top.niunaijun.blackbox.fake.service;

import android.content.Context;
import android.os.IBinder;

import java.lang.reflect.Method;
import java.util.Collections;

import black.android.content.integrity.BRIAppIntegrityManager;
import black.android.content.integrity.BRIAppIntegrityManagerStub;
import black.android.os.BRServiceManager;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;
import top.niunaijun.blackbox.utils.compat.ParceledListSliceCompat;

/**
 * @author gm
 * @function
 * @date :2024/4/23 16:13
 **/
public class IAppIntegrityManagerProxy extends BinderInvocationStub {
    private final static String SERVER_NAME = "app_integrity";

    public IAppIntegrityManagerProxy() {
        super(BRServiceManager.get().getService(SERVER_NAME));
    }

    @Override
    protected Object getWho() {
        return BRIAppIntegrityManagerStub.get().asInterface(BRServiceManager.get().getService(Context.ALARM_SERVICE));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(SERVER_NAME);
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @ProxyMethod("updateRuleSet")
    public static class updateRuleSet extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return null;
        }
    }

    @ProxyMethod("getCurrentRuleSetVersion")
    public static class getCurrentRuleSetVersion extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return "";
        }
    }

    @ProxyMethod("getCurrentRuleSetProvider")
    public static class getCurrentRuleSetProvider extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return "";
        }
    }

    @ProxyMethod("getCurrentRules")
    public static class getCurrentRules extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return ParceledListSliceCompat.create(Collections.emptyList());
        }
    }

    @ProxyMethod("getWhitelistedRuleProviders")
    public static class getWhitelistedRuleProviders extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return Collections.emptyList();
        }
    }
}
