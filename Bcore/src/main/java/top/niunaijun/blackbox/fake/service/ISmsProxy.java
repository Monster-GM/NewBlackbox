package top.niunaijun.blackbox.fake.service;

import android.content.Context;
import android.os.IBinder;

import java.lang.reflect.Method;

import black.android.os.BRServiceManager;
import black.com.android.internal.telephony.BRISmsStub;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;
import top.niunaijun.blackbox.utils.MethodParameterUtils;

/**
 * @author gm
 * @function
 * @date :2024/4/25 13:56
 **/
public class ISmsProxy extends BinderInvocationStub {
    public ISmsProxy() {
        super(BRServiceManager.get().getService("isms"));
    }

    @Override
    protected Object getWho() {
        return BRISmsStub.get().asInterface(BRServiceManager.get().getService("isms"));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService("isms");
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @ProxyMethod("getAllMessagesFromIccEfForSubscriber")
    public static class getAllMessagesFromIccEfForSubscriber extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (args != null) {
                int i = 1;
                if (i < 0) {
                    i += args.length;
                }
                if (i >= 0 && i < args.length && args[i] instanceof String) {
                    args[i] = BlackBoxCore.getHostPkg();
                }
            }
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("updateMessageOnIccEfForSubscriber")
    public static class updateMessageOnIccEfForSubscriber extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (args != null) {
                int i = 1;
                if (i < 0) {
                    i += args.length;
                }
                if (i >= 0 && i < args.length && args[i] instanceof String) {
                    args[i] = BlackBoxCore.getHostPkg();
                }
            }
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("copyMessageToIccEfForSubscriber")
    public static class copyMessageToIccEfForSubscriber extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (args != null) {
                int i = 1;
                if (i < 0) {
                    i += args.length;
                }
                if (i >= 0 && i < args.length && args[i] instanceof String) {
                    args[i] = BlackBoxCore.getHostPkg();
                }
            }
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("sendDataForSubscriber")
    public static class sendDataForSubscriber extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (args != null) {
                int i = 1;
                if (i < 0) {
                    i += args.length;
                }
                if (i >= 0 && i < args.length && args[i] instanceof String) {
                    args[i] = BlackBoxCore.getHostPkg();
                }
            }
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("sendDataForSubscriberWithSelfPermissions")
    public static class sendDataForSubscriberWithSelfPermissions extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (args != null) {
                int i = 1;
                if (i < 0) {
                    i += args.length;
                }
                if (i >= 0 && i < args.length && args[i] instanceof String) {
                    args[i] = BlackBoxCore.getHostPkg();
                }
            }
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("sendTextForSubscriber")
    public static class sendTextForSubscriber extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (args != null) {
                int i = 1;
                if (i < 0) {
                    i += args.length;
                }
                if (i >= 0 && i < args.length && args[i] instanceof String) {
                    args[i] = BlackBoxCore.getHostPkg();
                }
            }
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("sendTextForSubscriberWithSelfPermissions")
    public static class sendTextForSubscriberWithSelfPermissions extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (args != null) {
                int i = 1;
                if (i < 0) {
                    i += args.length;
                }
                if (i >= 0 && i < args.length && args[i] instanceof String) {
                    args[i] = BlackBoxCore.getHostPkg();
                }
            }
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("sendMultipartTextForSubscriber")
    public static class sendMultipartTextForSubscriber extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (args != null) {
                int i = 1;
                if (i < 0) {
                    i += args.length;
                }
                if (i >= 0 && i < args.length && args[i] instanceof String) {
                    args[i] = BlackBoxCore.getHostPkg();
                }
            }
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("sendStoredText")
    public static class sendStoredText extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (args != null) {
                int i = 1;
                if (i < 0) {
                    i += args.length;
                }
                if (i >= 0 && i < args.length && args[i] instanceof String) {
                    args[i] = BlackBoxCore.getHostPkg();
                }
            }
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("sendStoredMultipartText")
    public static class sendStoredMultipartText extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (args != null) {
                int i = 1;
                if (i < 0) {
                    i += args.length;
                }
                if (i >= 0 && i < args.length && args[i] instanceof String) {
                    args[i] = BlackBoxCore.getHostPkg();
                }
            }
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("getAllMessagesFromIccEf")
    public static class getAllMessagesFromIccEf extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("updateMessageOnIccEf")
    public static class updateMessageOnIccEf extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("copyMessageToIccEf")
    public static class copyMessageToIccEf extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("sendData")
    public static class sendData extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("sendText")
    public static class sendText extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("sendMultipartText")
    public static class sendMultipartText extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }
}
