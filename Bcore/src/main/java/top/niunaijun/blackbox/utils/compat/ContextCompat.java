package top.niunaijun.blackbox.utils.compat;

import android.content.ContentProvider;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Binder;

import black.android.app.BRContextImpl;
import black.android.app.BRContextImplKitkat;
import black.android.content.AttributionSourceStateContext;
import black.android.content.BRAttributionSource;
import black.android.content.BRAttributionSourceState;
import black.android.content.BRContentResolver;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.app.BActivityThread;

/**
 * Created by Milk on 3/31/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class ContextCompat {
    public static final String TAG = "ContextCompat";

    public static void fixAttributionSourceState(Object obj, int uid) {
        Object mAttributionSourceState;
        if (obj != null && BRAttributionSource.get(obj)._check_mAttributionSourceState() != null) {
            mAttributionSourceState = BRAttributionSource.get(obj).mAttributionSourceState();

            AttributionSourceStateContext attributionSourceStateContext = BRAttributionSourceState.get(mAttributionSourceState);
            attributionSourceStateContext._set_packageName(BlackBoxCore.getHostPkg());
            attributionSourceStateContext._set_uid(uid);
            fixAttributionSourceState(BRAttributionSource.get(obj).getNext(), uid);
        }
    }


    public static void fix(Context context) {
        try {
            int deep = 0;
            while (context instanceof ContextWrapper) {
                context = ((ContextWrapper) context).getBaseContext();
                deep++;
                if (deep >= 10) {
                    return;
                }
            }
            BRContextImpl.get(context)._set_mPackageManager(null);
            try {
                context.getPackageManager();
            } catch (Throwable e) {
                e.printStackTrace();
            }
            //判断是否是虚拟进程
            if (!BlackBoxCore.get().isBlackProcess()){
                return;
            }
            if (context.getPackageName() != null){
                BRContextImpl.get(context)._set_mBasePackageName(BlackBoxCore.getHostPkg());
                BRContextImplKitkat.get(context)._set_mOpPackageName(BlackBoxCore.getHostPkg());
                BRContentResolver.get(context.getContentResolver())._set_mPackageName(BlackBoxCore.getHostPkg());

                if (BuildCompat.isS()) {
                    fixAttributionSourceState(BRContextImpl.get(context).getAttributionSource(), BlackBoxCore.getHostUid());
                    fixContentProvider();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void fixContentProvider() {
        try {
            XposedHelpers.findAndHookMethod(ContentProvider.class, "getCallingAttributionSource", new Object[]{new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    int callingPid = Binder.getCallingPid();
                    int mypid = android.os.Process.myPid();
                    Object attributionSource = param.getResult();
                    if (callingPid == mypid) {
                        ContextCompat.fixAttributionSourceState(attributionSource,BlackBoxCore.getHostUid());
                    }
                }
            }});
        } catch (Throwable e) {
            e.printStackTrace();
        }
        try {
            XposedHelpers.findAndHookMethod(ContentProvider.class, "getCallingPackage", new Object[]{new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    int callingPid = Binder.getCallingPid();
                    int mypid = android.os.Process.myPid();
                    if (callingPid == mypid) {
                        param.setResult(BActivityThread.getAppPackageName());
                    }
                }
            }});
        } catch (Throwable e2) {
            e2.printStackTrace();
        }
        try {
            XposedHelpers.findAndHookMethod(ContentProvider.class, "getCallingPackageUnchecked", new Object[]{new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    int callingPid = Binder.getCallingPid();
                    int mypid = android.os.Process.myPid();
                    if (callingPid == mypid) {
                        param.setResult(BActivityThread.getAppPackageName());
                    }
                }
            }});
        } catch (Throwable e3) {
            e3.printStackTrace();
        }
    }
}
