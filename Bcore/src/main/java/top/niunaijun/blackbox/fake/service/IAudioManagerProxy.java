package top.niunaijun.blackbox.fake.service;

import android.content.Context;
import android.os.IBinder;

import java.lang.reflect.Method;

import black.android.media.BRIAudioServiceStub;
import black.android.os.BRServiceManager;
import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.fake.frameworks.BLocationManager;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;
import top.niunaijun.blackbox.fake.service.base.PkgMethodProxy;
import top.niunaijun.blackbox.utils.MethodParameterUtils;

/**
 * @author gm
 * @function
 * @date :2024/4/20 20:48
 **/
public class IAudioManagerProxy extends BinderInvocationStub {
    public IAudioManagerProxy() {
        super(BRServiceManager.get().getService(Context.AUDIO_SERVICE));
    }

    @Override
    protected Object getWho() {
        return BRIAudioServiceStub.get().asInterface(BRServiceManager.get().getService(Context.AUDIO_SERVICE));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @ProxyMethod("unregisterAudioFocusClient")
    public static class unregisterAudioFocusClient extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("registerRemoteControlClient")
    public static class registerRemoteControlClient extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("disableSafeMediaVolume")
    public static class disableSafeMediaVolume extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("startBluetoothSco")
    public static class startBluetoothSco extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("stopBluetoothSco")
    public static class stopBluetoothSco extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("setBluetoothScoOn")
    public static class setBluetoothScoOn extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("setSpeakerphoneOn")
    public static class setSpeakerphoneOn extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("setWiredDeviceConnectionState")
    public static class setWiredDeviceConnectionState extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("requestAudioFocus")
    public static class requestAudioFocus extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("abandonAudioFocus")
    public static class abandonAudioFocus extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("avrcpSupportsAbsoluteVolume")
    public static class avrcpSupportsAbsoluteVolume extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("setMode")
    public static class setMode extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("setRingerModeInternal")
    public static class setRingerModeInternal extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("setRingerModeExternal")
    public static class setRingerModeExternal extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("setMasterVolume")
    public static class setMasterVolume extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("setStreamVolume")
    public static class setStreamVolume extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("adjustMasterVolume")
    public static class adjustMasterVolume extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("adjustStreamVolume")
    public static class adjustStreamVolume extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("adjustSuggestedStreamVolume")
    public static class adjustSuggestedStreamVolume extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("adjustLocalOrRemoteStreamVolume")
    public static class adjustLocalOrRemoteStreamVolume extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("adjustVolume")
    public static class adjustVolume extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("isHardwareDetected")
    public static class isHardwareDetected extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("setMicrophoneMute")
    public static class setMicrophoneMute extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastAppPkg(args);
            MethodParameterUtils.replaceLastUserId(args);
            return method.invoke(who, args);
        }
    }
}
