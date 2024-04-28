package top.niunaijun.blackbox.fake.service;

import android.app.Notification;
import android.content.Context;
import android.util.Log;
import android.view.inputmethod.EditorInfo;

import java.lang.reflect.Method;

import black.android.app.BRNotificationManager;
import black.android.os.BRServiceManager;
import black.com.android.internal.view.BRIInputMethodManager;
import black.com.android.internal.view.BRIInputMethodManagerStub;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.fake.frameworks.BNotificationManager;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;
import top.niunaijun.blackbox.utils.ArrayUtils;
import top.niunaijun.blackbox.utils.MethodParameterUtils;

/**
 * @author gm
 * @function
 * @date :2024/4/25 12:24
 **/
public class IInputMethodManagerProxy extends BinderInvocationStub {

    public IInputMethodManagerProxy() {
        super(BRServiceManager.get().getService(Context.INPUT_METHOD_SERVICE));
    }

    @Override
    protected Object getWho() {
        return BRIInputMethodManagerStub.get().asInterface(BRServiceManager.get().getService(Context.INPUT_METHOD_SERVICE));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @ProxyMethod("startInput")
    public static class startInput extends startInputOrWindowGainedFocus {


    }

    @ProxyMethod("windowGainedFocus")
    public static class windowGainedFocus extends startInputOrWindowGainedFocus {


    }

    @ProxyMethod("startInputOrWindowGainedFocus")
    public static class startInputOrWindowGainedFocus extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Log.e(TAG, "startInputOrWindowGainedFocus called");
            int editorInfoIndex = ArrayUtils.indexOfFirst(args, EditorInfo.class);
            if (editorInfoIndex != -1) {
                EditorInfo attribute = (EditorInfo) args[editorInfoIndex];
                attribute.packageName = BlackBoxCore.getHostPkg();
                args[editorInfoIndex] = attribute;
            }
            //fixEditor(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("getInputMethodList")
    public static class getInputMethodList extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastUserId(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("getEnabledInputMethodList")
    public static class getEnabledInputMethodList extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastUserId(args);
            return method.invoke(who, args);
        }
    }

//    @ProxyMethod("showSoftInput")
//    public static class showSoftInput extends MethodHook {
//
//        @Override
//        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
//            int state = (int)args[1];
//            Log.e(TAG, "showSoftInput state: "+state);
//            //args[1] = 0;
////            fixEditor(args);
//            return method.invoke(who, args);
//        }
//    }
}
