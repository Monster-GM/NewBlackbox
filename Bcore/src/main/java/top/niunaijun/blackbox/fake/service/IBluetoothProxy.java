package top.niunaijun.blackbox.fake.service;

import static android.os.Build.VERSION_CODES.JELLY_BEAN_MR1;

import android.os.Build;
import android.os.IBinder;

import java.lang.reflect.Method;

import black.android.bluetooth.BRIBluetooth;
import black.android.bluetooth.BRIBluetoothStub;
import black.android.content.AttributionSource;
import black.android.content.BRAttributionSource;
import black.android.os.BRServiceManager;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;
import top.niunaijun.blackbox.utils.MethodParameterUtils;
import top.niunaijun.blackbox.utils.compat.BuildCompat;
import top.niunaijun.blackbox.utils.compat.ContextCompat;

/**
 * @author gm
 * @function
 * @date :2024/4/23 15:21
 **/
public class IBluetoothProxy extends BinderInvocationStub {
    private final static String SERVER_NAME = Build.VERSION.SDK_INT >= JELLY_BEAN_MR1 ?
            "bluetooth_manager" : "bluetooth";
    public IBluetoothProxy() {
        super(BRServiceManager.get().getService(SERVER_NAME));
    }

    @Override
    protected Object getWho() {
        return BRIBluetoothStub.get().asInterface(BRServiceManager.get().getService(SERVER_NAME));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(SERVER_NAME);
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @ProxyMethod("enable")
    public static class enable extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (BuildCompat.isS()){
                if (args != null && args.length > 0) {
                    for (int i = 0; i < args.length; i++) {
                        if (BRAttributionSource.getRealClass().isInstance(args[i])) {
                            ContextCompat.fixAttributionSourceState(args[i], BlackBoxCore.getHostUid());
                        }
                    }
                }
                return method.invoke(who, args);
            }else{
                MethodParameterUtils.replaceFirstAppPkg(args);
                return method.invoke(who, args);
            }
        }
    }

    @ProxyMethod("disable")
    public static class disable extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (BuildCompat.isS()){
                if (args != null && args.length > 0) {
                    for (int i = 0; i < args.length; i++) {
                        if (BRAttributionSource.getRealClass().isInstance(args[i])) {
                            ContextCompat.fixAttributionSourceState(args[i], BlackBoxCore.getHostUid());
                        }
                    }
                }
                return method.invoke(who, args);
            }else{
                MethodParameterUtils.replaceFirstAppPkg(args);
                return method.invoke(who, args);
            }
        }
    }

    @ProxyMethod("enableNoAutoConnect")
    public static class enableNoAutoConnect extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (BuildCompat.isS()){
                if (args != null && args.length > 0) {
                    for (int i = 0; i < args.length; i++) {
                        if (BRAttributionSource.getRealClass().isInstance(args[i])) {
                            ContextCompat.fixAttributionSourceState(args[i], BlackBoxCore.getHostUid());
                        }
                    }
                }
                return method.invoke(who, args);
            }else{
                MethodParameterUtils.replaceFirstAppPkg(args);
                return method.invoke(who, args);
            }
        }
    }

    @ProxyMethod("updateBleAppCount")
    public static class updateBleAppCount extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (BuildCompat.isS()){
                if (args != null && args.length > 0) {
                    for (int i = 0; i < args.length; i++) {
                        if (BRAttributionSource.getRealClass().isInstance(args[i])) {
                            ContextCompat.fixAttributionSourceState(args[i], BlackBoxCore.getHostUid());
                        }
                    }
                }
                return method.invoke(who, args);
            }else{
                MethodParameterUtils.replaceFirstAppPkg(args);
                return method.invoke(who, args);
            }
        }
    }

    @ProxyMethod("enableBle")
    public static class enableBle extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (BuildCompat.isS()){
                if (args != null && args.length > 0) {
                    for (int i = 0; i < args.length; i++) {
                        if (BRAttributionSource.getRealClass().isInstance(args[i])) {
                            ContextCompat.fixAttributionSourceState(args[i], BlackBoxCore.getHostUid());
                        }
                    }
                }
                return method.invoke(who, args);
            }else{
                MethodParameterUtils.replaceFirstAppPkg(args);
                return method.invoke(who, args);
            }
        }
    }

    @ProxyMethod("disableBle")
    public static class disableBle extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (BuildCompat.isS()){
                if (args != null && args.length > 0) {
                    for (int i = 0; i < args.length; i++) {
                        if (BRAttributionSource.getRealClass().isInstance(args[i])) {
                            ContextCompat.fixAttributionSourceState(args[i], BlackBoxCore.getHostUid());
                        }
                    }
                }
                return method.invoke(who, args);
            }else{
                MethodParameterUtils.replaceFirstAppPkg(args);
                return method.invoke(who, args);
            }
        }
    }
}
