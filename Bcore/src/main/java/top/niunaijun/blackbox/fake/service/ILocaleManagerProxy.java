package top.niunaijun.blackbox.fake.service;

import android.content.Context;
import android.os.IBinder;

import black.android.app.BRILocaleManager;
import black.android.app.BRILocaleManagerStub;
import black.android.os.BRServiceManager;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.service.base.PkgMethodProxy;

/**
 * @author gm
 * @function
 * @date :2024/4/20 20:06
 **/
public class ILocaleManagerProxy extends BinderInvocationStub {
    public static final String TAG = "ILocaleManagerProxy";

    public ILocaleManagerProxy() {
        super(BRServiceManager.get().getService("locale"));
    }

    @Override
    protected void onBindMethod() {
        super.onBindMethod();
        addMethodHook(new PkgMethodProxy("setApplicationLocales"));
        addMethodHook(new PkgMethodProxy("getApplicationLocales"));
    }

    @Override
    protected Object getWho() {
        return BRILocaleManagerStub.get().asInterface(BRServiceManager.get().getService("locale"));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService("locale");
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }
}
