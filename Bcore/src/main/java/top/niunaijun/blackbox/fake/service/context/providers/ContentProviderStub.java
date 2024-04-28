package top.niunaijun.blackbox.fake.service.context.providers;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.IInterface;

import java.lang.reflect.Method;

import black.android.app.ContextImplContext;
import black.android.content.AttributionSourceContext;
import black.android.content.AttributionSourceStateContext;
import black.android.content.BRAttributionSource;
import black.android.content.BRAttributionSourceState;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.fake.hook.ClassInvocationStub;
import top.niunaijun.blackbox.utils.compat.BuildCompat;
import top.niunaijun.blackbox.utils.compat.ContextCompat;
import top.niunaijun.blackreflection.BlackReflection;
import top.niunaijun.blackreflection.utils.ClassUtil;

/**
 * Created by Milk on 4/8/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class ContentProviderStub extends ClassInvocationStub implements BContentProvider {
    public static final String TAG = "ContentProviderStub";
    private IInterface mBase;
    private String mAppPkg;

    public IInterface wrapper(final IInterface contentProviderProxy, final String appPkg) {
        mBase = contentProviderProxy;
        mAppPkg = appPkg;
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
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("asBinder".equals(method.getName())) {
            return method.invoke(mBase, args);
        }
        if (args != null && args.length > 0) {
            Object arg = args[0];
            if (BuildCompat.isU()){
                if (arg != null && arg.getClass().getName().equals(ClassUtil.classReady(AttributionSourceContext.class).getName())){
                    fixAttributionSourceState(arg, BlackBoxCore.getHostUid(),BActivityThread.getAppPid());
                }
            }else if (BuildCompat.isS() && arg != null && arg.getClass().getName().equals(ClassUtil.classReady(AttributionSourceContext.class).getName())){
                fixAttributionSourceState(arg, BlackBoxCore.getHostUid());
            }
        }
//        if (args != null && args.length > 0) {
//            Object arg = args[0];
//            if (arg instanceof String) {
//                args[0] = mAppPkg;
//            } else if (arg.getClass().getName().equals(BRAttributionSource.getRealClass().getName())) {
//                fixAttributionSourceState(arg, BActivityThread.getBUid());
//            }
//        }
        try {
            return method.invoke(mBase, args);
        } catch (Throwable e) {
            throw e.getCause();
        }
    }

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

    public static void fixAttributionSourceState(Context context,int uid){
        int i = 0;
        Context fixContext = context;
        do {
            try {
                if (fixContext instanceof ContextWrapper){
                    fixContext = ((ContextWrapper)fixContext).getBaseContext();
                    i++;
                }else{
                    fixAttributionSourceState(((ContextImplContext) BlackReflection.create(ContextImplContext.class, fixContext, false)).getAttributionSource(),uid);
                    return;
                }
            }catch (Exception e){
                e.printStackTrace();
                return;
            }
        }while (i < 10);
    }

    public static void fixAttributionSourceState(Object obj, int uid, int pid) {
        if (obj != null && BRAttributionSource.get(obj)._check_mAttributionSourceState() != null) {
            return;
        }
        AttributionSourceStateContext attributionSourceStateContext = (AttributionSourceStateContext) BlackReflection.create(AttributionSourceStateContext.class,BRAttributionSource.get(obj).mAttributionSourceState(), false);
        attributionSourceStateContext._set_packageName(BlackBoxCore.getHostPkg());
        attributionSourceStateContext._set_uid(Integer.valueOf(uid));
        attributionSourceStateContext._set_pid(Integer.valueOf(pid));
        fixAttributionSourceState(BRAttributionSource.get(obj).getNext(), uid,pid);
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }
}
