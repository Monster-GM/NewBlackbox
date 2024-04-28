package top.niunaijun.blackbox.fake.service.vivo;

import android.os.Process;

import java.lang.reflect.Method;

import black.android.os.BRServiceManager;
import black.model.vivo.BRIVivoPermissionServiceStub;
import black.model.vivo.IVivoPermissionServiceContext;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;
import top.niunaijun.blackbox.utils.ArrayUtils;
import top.niunaijun.blackbox.utils.MethodParameterUtils;

/**
 * @author gm
 * @function
 * @date :2024/4/23 16:54
 **/
public class IVivoPermissionServiceProxy extends BinderInvocationStub {
    public IVivoPermissionServiceProxy() {//型号
        super(BRServiceManager.get().getService("vivo_permission_service"));
    }

    @Override
    protected Object getWho() {
        return BRIVivoPermissionServiceStub.get().asInterface(BRServiceManager.get().getService("vivo_permission_service"));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService("vivo_permission_service");
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @ProxyMethod("checkPermission")
    public static class checkPermission extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            int uid = (int)args[2];
            if (uid == Process.myUid()){
                args[2] = BlackBoxCore.getHostUid();
            }
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("getAppPermission")
    public static class getAppPermission extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("setAppPermission")
    public static class setAppPermission extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("setWhiteListApp")
    public static class setWhiteListApp extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("setBlackListApp")
    public static class setBlackListApp extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("noteStartActivityProcess")
    public static class noteStartActivityProcess extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("isBuildInThirdPartApp")
    public static class isBuildInThirdPartApp extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("checkDelete")
    public static class checkDelete extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (args[1] instanceof String) {
                args[1] = BlackBoxCore.getHostPkg();
            }
            MethodParameterUtils.replaceLastUserId(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("setOnePermission")
    public static class setOnePermission extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastUserId(args);
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("setOnePermissionExt")
    public static class setOnePermissionExt extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastUserId(args);
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("isVivoImeiPkg")
    public static class isVivoImeiPkg extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }
}
