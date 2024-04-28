package top.niunaijun.blackbox.fake.service.context.providers;

import android.os.Build;
import android.os.Bundle;
import android.os.IInterface;
import android.text.TextUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import black.android.content.BRAttributionSource;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.fake.hook.ClassInvocationStub;
import top.niunaijun.blackbox.utils.compat.BuildCompat;
import top.niunaijun.blackbox.utils.compat.ContextCompat;

/**
 * Created by Milk on 4/8/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class SettingsProviderStub extends ClassInvocationStub implements BContentProvider {
    private IInterface mBase;
    private static final int METHOD_GET = 0;
    private static final int METHOD_PUT = 1;

    private static final Map<String, String> PRE_SET_VALUES = new HashMap<>();

    private static final Set<String> SETTINGS_DIRECT_TO_SYSTEM = new HashSet();
    private static final Set<String> sSystemTableColums = new HashSet();

    static {
        PRE_SET_VALUES.put("user_setup_complete", "1");
        PRE_SET_VALUES.put("install_non_market_apps", "1");
        PRE_SET_VALUES.put("development_settings_enabled", "0");
        PRE_SET_VALUES.put("adb_enabled", "0");
        SETTINGS_DIRECT_TO_SYSTEM.add("device_provisioned");
        SETTINGS_DIRECT_TO_SYSTEM.add("location_providers_allowed");
    }


    private static int getMethodType(String method) {
        if (method.startsWith("GET_")) {
            return METHOD_GET;
        }
        if (method.startsWith("PUT_")) {
            return METHOD_PUT;
        }
        return -1;
    }

    private static boolean isSecureMethod(String method) {
        return method.endsWith("secure");
    }

    static int getTableIndex(String str) {
        if (str.contains("secure")) {
            return METHOD_PUT;
        }
        if (str.contains("system")) {
            return METHOD_GET;
        }
        if (str.contains("global")) {
            return 2;
        }
        return str.contains("config") ? 3 : -1;
    }

    @Override
    public IInterface wrapper(IInterface contentProviderProxy, String appPkg) {
        mBase = contentProviderProxy;
        injectHook();
        return (IInterface) getProxyInvocation();
    }

    @Override
    protected Object getWho() {
        return mBase;
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {

    }

    @Override
    protected void onBindMethod() {

    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("asBinder".equals(method.getName())) {
            return method.invoke(mBase, args);
        }
        int tableIndex = getTableIndex(method.getName());
        if (tableIndex >= 0) {
            if (BuildCompat.isR() && TextUtils.equals(method.getName(), "SET_ALL_config")) {
                Bundle bundle = new Bundle();
                bundle.putInt("config_set_all_return", 1);
                return bundle;
            }else if(BuildCompat.isR() && TextUtils.equals(method.getName(), "LIST_config")){
                return null;
            }else{
                int methodType = getMethodType(method.getName());
                if (methodType == 0) {
                    if (args != null && args.length > 0) {
                        Object arg = args[0];
                        if (arg instanceof String) {
                            String presetValue = PRE_SET_VALUES.get(arg);
                            if (presetValue != null) {
                                return wrapBundle(arg.toString(), presetValue);
                            }
                            if ("android_id".equals(arg)) {
                                //id留空待观察
                                return wrapBundle("android_id", "");
                            }
                            if (SETTINGS_DIRECT_TO_SYSTEM.contains(arg)) {
                                //待定
                                return (Bundle) method.invoke(mBase, args);
                            }
                        }
                    }
                }else{
                    return new Bundle();
                }
            }
        }
        if (args != null && args.length > 0) {
            Object arg = args[0];
            if (arg instanceof String) {
                args[0] = BlackBoxCore.getHostPkg();
            } else if (arg.getClass().getName().equals(BRAttributionSource.getRealClass().getName())) {
                ContextCompat.fixAttributionSourceState(arg, BlackBoxCore.getHostUid());
            }
        }
        return method.invoke(mBase, args);
    }


    private Bundle wrapBundle(String name, String value) {
        Bundle bundle = new Bundle();
        if (Build.VERSION.SDK_INT >= 24) {
            bundle.putString("name", name);
            bundle.putString("value", value);
        } else {
            bundle.putString(name, value);
        }
        return bundle;
    }
}
