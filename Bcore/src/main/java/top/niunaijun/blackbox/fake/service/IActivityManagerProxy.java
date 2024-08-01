package top.niunaijun.blackbox.fake.service;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Application;
import android.app.IServiceConnection;
import android.app.Notification;
import android.content.ComponentName;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.IInterface;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;

import black.android.app.BRActivityManagerNative;
import black.android.app.BRActivityManagerOreo;
import black.android.app.BRLoadedApkReceiverDispatcher;
import black.android.app.BRLoadedApkReceiverDispatcherInnerReceiver;
import black.android.app.BRLoadedApkServiceDispatcher;
import black.android.app.BRLoadedApkServiceDispatcherInnerConnection;
import black.android.content.BRContentProviderNative;
import black.android.content.pm.BRUserInfo;
import black.android.util.BRSingleton;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.core.env.AppSystemEnv;
import top.niunaijun.blackbox.core.system.DaemonService;
import top.niunaijun.blackbox.core.system.user.BUserHandle;
import top.niunaijun.blackbox.entity.AppConfig;
import top.niunaijun.blackbox.entity.am.RunningAppProcessInfo;
import top.niunaijun.blackbox.entity.am.RunningServiceInfo;
import top.niunaijun.blackbox.fake.delegate.ContentProviderDelegate;
import top.niunaijun.blackbox.fake.delegate.InnerReceiverDelegate;
import top.niunaijun.blackbox.fake.delegate.ServiceConnectionDelegate;
import top.niunaijun.blackbox.fake.frameworks.BActivityManager;
import top.niunaijun.blackbox.fake.frameworks.BPackageManager;
import top.niunaijun.blackbox.fake.hook.ClassInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;
import top.niunaijun.blackbox.fake.hook.ScanClass;
import top.niunaijun.blackbox.fake.service.base.PkgMethodProxy;
import top.niunaijun.blackbox.fake.service.context.providers.ContentProviderStub;
import top.niunaijun.blackbox.fake.service.context.providers.SettingsProviderStub;
import top.niunaijun.blackbox.proxy.ProxyManifest;
import top.niunaijun.blackbox.proxy.record.ProxyBroadcastRecord;
import top.niunaijun.blackbox.proxy.record.ProxyPendingRecord;
import top.niunaijun.blackbox.utils.ArrayUtils;
import top.niunaijun.blackbox.utils.ComponentUtils;
import top.niunaijun.blackbox.utils.MethodParameterUtils;
import top.niunaijun.blackbox.utils.Reflector;
import top.niunaijun.blackbox.utils.compat.ActivityManagerCompat;
import top.niunaijun.blackbox.utils.compat.BuildCompat;
import top.niunaijun.blackbox.utils.compat.ParceledListSliceCompat;
import top.niunaijun.blackbox.utils.compat.TaskDescriptionCompat;

import static android.content.pm.PackageManager.GET_META_DATA;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Created by Milk on 3/30/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
@ScanClass(ActivityManagerCommonProxy.class)
public class IActivityManagerProxy extends ClassInvocationStub {
    public static final String TAG = "ActivityManagerStub";

    @Override
    protected Object getWho() {
        Object iActivityManager = null;
        if (BuildCompat.isOreo()) {
            iActivityManager = BRActivityManagerOreo.get().IActivityManagerSingleton();
        } else if (BuildCompat.isL()) {
            iActivityManager = BRActivityManagerNative.get().gDefault();
        }
        return BRSingleton.get(iActivityManager).get();
    }

    @Override
    protected void inject(Object base, Object proxy) {
        Object iActivityManager = null;
        if (BuildCompat.isOreo()) {
            iActivityManager = BRActivityManagerOreo.get().IActivityManagerSingleton();
        } else if (BuildCompat.isL()) {
            iActivityManager = BRActivityManagerNative.get().gDefault();
        }
        BRSingleton.get(iActivityManager)._set_mInstance(proxy);
    }

    @Override
    public boolean isBadEnv() {
        return getProxyInvocation() != getWho();
    }

    @ProxyMethod("reportJunkFromApp")
    public static class reportJunkFromApp extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("setAppLockedVerifying")
    public static class setAppLockedVerifying extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("getAppStartMode")
    public static class getAppStartMode extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("getContentProvider")
    public static class GetContentProvider extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Exception {
            int authIndex = getAuthIndex();
            Object auth = args[authIndex];
            Object content = null;

            if (auth instanceof String) {
                if (ProxyManifest.isProxy((String) auth)) {
                    return method.invoke(who, args);
                }
                if (BuildCompat.isQ()) {
                    args[1] = BlackBoxCore.getHostPkg();
                }
                int userId = BUserHandle.myUserId();
                ProviderInfo info = BlackBoxCore.getBPackageManager().resolveContentProvider((String) auth, GET_META_DATA, BActivityThread.getUserId());
                if (info != null && !info.enabled) {
                    return null;
                }
                if (auth.equals("settings") || auth.equals("media") || auth.equals("telephony")) {
                    content = method.invoke(who, args);
                    ContentProviderDelegate.update(content, (String) auth);
                    return content;
                } else {
                    Log.d(TAG, "hook getContentProvider: " + auth);
                    ProviderInfo providerInfo = BlackBoxCore.getBPackageManager().resolveContentProvider((String) auth, GET_META_DATA, BActivityThread.getUserId());
                    if (providerInfo == null) {
//                        Log.d(TAG, "hook system: " + auth);
//                        Object invoke = method.invoke(who, args);
//                        if (invoke != null) {
//                            Object provider = Reflector.with(invoke)
//                                    .field("provider")
//                                    .get();
//                            if (provider != null && !(provider instanceof Proxy)) {
//                                Reflector.with(invoke)
//                                        .field("provider")
//                                        .set(new SettingsProviderStub().wrapper((IInterface) provider, BlackBoxCore.getHostPkg()));
//                            }
//                        }
                        return null;
                    }

                    Log.d(TAG, "hook app: " + auth);
                    IBinder providerBinder = null;
                    if (BActivityThread.getAppPid() != -1) {
                        AppConfig appConfig = BlackBoxCore.getBActivityManager().initProcess(providerInfo.packageName, providerInfo.processName, BActivityThread.getUserId());
                        if (appConfig.bpid != BActivityThread.getAppPid()) {
                            providerBinder = BlackBoxCore.getBActivityManager().acquireContentProviderClient(providerInfo);
                        }
                        args[authIndex] = ProxyManifest.getProxyAuthorities(appConfig.bpid);
                        args[getUserIndex()] = BlackBoxCore.getHostUserId();
                    }
                    if (providerBinder == null)
                        return null;

                    content = method.invoke(who, args);
                    Reflector.with(content)
                            .field("info")
                            .set(providerInfo);
                    Reflector.with(content)
                            .field("provider")
                            .set(new ContentProviderStub().wrapper(BRContentProviderNative.get().asInterface(providerBinder), BActivityThread.getAppPackageName()));
                }
                MethodParameterUtils.replaceLastUserId(args);
                return content;
            }
            MethodParameterUtils.replaceLastUserId(args);
            return method.invoke(who, args);
        }

        private int getAuthIndex() {
            // 10.0
            if (BuildCompat.isQ()) {
                return 2;
            } else {
                return 1;
            }
        }

        private int getUserIndex() {
            return getAuthIndex() + 1;
        }
    }

    static IServiceConnection  StartService;

    //android 13-14问题待修复
    @ProxyMethod("startService")
    public static class StartService extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
//            Intent service = new Intent((Intent) args[1]);
//            String resolvedType = (String) args[2];
//            ComponentName component = service.getComponent();
//            if (component != null && BlackBoxCore.getHostPkg().equals(component.getPackageName())) {
//                return method.invoke(who, args);
//            }
//            boolean requireForeground = false;
//            int userId = service.getIntExtra("_B_|_UserId", -1);
//            userId = userId == -1 ? BActivityThread.getUserId() : userId;
//            service.setDataAndType(service.getData(), resolvedType);
//            ResolveInfo resolveInfo = BlackBoxCore.getBPackageManager().resolveService(service, 0, resolvedType, BActivityThread.getUserId());
//            if (resolveInfo != null) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && args.length >= 6 && args[3] instanceof Boolean) {
//                    args[3] = false;
//                }
//                AppConfig clientConfig = BActivityManager.get().initProcess(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.processName, userId);
//                if (clientConfig == null) {
//                    Log.e("ActivityManager", "failed to initProcess for startService: " + component);
//                    return null;
//                }
//                int requireForegroundIndex = getRequireForeground();
//
//                if (requireForegroundIndex != -1) {
//                   requireForeground = (boolean) args[requireForegroundIndex];
//                }
//                Intent proxyIntent = BlackBoxCore.getBActivityManager().bindService(service,
//                        StartService == null ? null : StartService.asBinder(),
//                        resolvedType,
//                        userId);
//                args[1] = proxyIntent;
//                MethodParameterUtils.replaceLastUserId(args);
//                ComponentName res = (ComponentName) method.invoke(who, args);
//                if (res != null) {
//                    res = new ComponentName(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.name);
//                    return res;
//                }
//                return null;
//            }
//            if (component == null || !AppSystemEnv.isOpenPackage(component.getPackageName())) {
//                if (service.getPackage() != null && !AppSystemEnv.isOpenPackage(service.getPackage())) {
//                    Log.e("ActivityManager", "Block StartService: " + service);
//                    return null;
//                }
//            }
//            MethodParameterUtils.replaceLastUserId(args);
//            BlackBoxCore.getBActivityManager().startService(service, resolvedType, requireForeground, BActivityThread.getUserId());
//            return method.invoke(who, args);
            Intent intent = (Intent) args[1];
            String resolvedType = (String) args[2];
            ResolveInfo resolveInfo = BlackBoxCore.getBPackageManager().resolveService(intent, 0, resolvedType, BActivityThread.getUserId());
            if (resolveInfo == null) {
                return method.invoke(who, args);
            }
            int requireForegroundIndex = getRequireForeground();
            boolean requireForeground = false;
            if (requireForegroundIndex != -1) {
                requireForeground = (boolean) args[requireForegroundIndex];
            }
            BlackBoxCore.getBActivityManager().startService(intent, resolvedType, requireForeground, BActivityThread.getUserId());
            return method.invoke(who, args);
        }

        public int getRequireForeground() {
            if (BuildCompat.isOreo()) {
                return 3;
            }
            return -1;
        }
    }

    @ProxyMethod("stopService")
    public static class StopService extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Intent intent = (Intent) args[1];
            String resolvedType = (String) args[2];
            return BlackBoxCore.getBActivityManager().stopService(intent, resolvedType, BActivityThread.getUserId());
        }
    }

    @ProxyMethod("stopServiceToken")
    public static class StopServiceToken extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            ComponentName componentName = (ComponentName) args[0];
            IBinder token = (IBinder) args[1];
            BlackBoxCore.getBActivityManager().stopServiceToken(componentName, token, BActivityThread.getUserId());
            return true;
        }
    }

    @ProxyMethod("bindService")
    public static class BindService extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            IInterface iInterface = (IInterface) args[0];
            IBinder iBinder = (IBinder) args[1];
            Intent intent = (Intent) args[2];
            String resolvedType = (String) args[3];
            IServiceConnection connection = (IServiceConnection) args[4];
            //待修复
            StartService = connection;
            //-----
            ComponentName component = intent.getComponent();
            long flags = getIntOrLongValue(args[5]);
            int userId = intent.getIntExtra("_B_|_UserId", -1);
            userId = userId == -1 ? BActivityThread.getUserId() : userId;
            ResolveInfo resolveInfo = BlackBoxCore.getBPackageManager().resolveService(intent, 0, resolvedType, userId);
            if (component != null && component.getPackageName().equals(BlackBoxCore.getHostPkg())) {
                return method.invoke(who, args);
            }
            int callingPkgIdx = isIsolated() ? 7 : (char) 6;
            if (args.length >= 8 && (args[callingPkgIdx] instanceof String)) {
                args[callingPkgIdx] = BlackBoxCore.getHostPkg();
            }
            if (resolveInfo == null) {
                if (component == null || !AppSystemEnv.isOpenPackage(component.getPackageName())) {
                    Log.e("ActivityManager", "Block bindService: " + intent);
                    return 0;
                }
                MethodParameterUtils.replaceLastUserId(args);
                return method.invoke(who, args);
            }
            if ((flags & (-2147483648L)) != 0) {
                if (BuildCompat.isU()) {
                    args[5] = Long.valueOf(flags & 2147483647L);
                } else {
                    args[5] = Integer.valueOf((int) (flags & 2147483647L));
                }
            }
            AppConfig appConfig = BActivityManager.get().initProcess(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.name,userId);
            if (appConfig == null) {
                Log.e("ActivityManager", "failed to initProcess for bindService: " + component);
                return 0;
            }
            Intent proxyIntent = BlackBoxCore.getBActivityManager().bindService(intent,
                    connection == null ? null : connection.asBinder(),
                    resolvedType,
                    userId);
            args[2] = proxyIntent;
            args[4] = ServiceConnectionDelegate.createProxy(connection, intent);
            WeakReference<?> weakReference = BRLoadedApkServiceDispatcherInnerConnection.get(connection).mDispatcher();
            if (weakReference != null) {
                BRLoadedApkServiceDispatcher.get(weakReference.get())._set_mConnection(ServiceConnectionDelegate.createProxy(connection, intent));
            }
            return method.invoke(who, args);
        }

        @Override
        protected boolean isEnable() {
            return BlackBoxCore.get().isBlackProcess() || BlackBoxCore.get().isServerProcess();
        }

        protected boolean isIsolated() {
            return false;
        }
    }

    public static long getIntOrLongValue(Object obj) {
        if (obj == null) {
            return 0L;
        }
        if (obj instanceof Integer) {
            return ((Integer) obj).longValue();
        }
        if (obj instanceof Long) {
            return ((Long) obj).longValue();
        }
        return -1L;
    }

    //android 13.0变更
    @ProxyMethod("bindServiceInstance")
    public static class BindServiceInstance extends BindIsolatedService {

    }

    // 10.0
    @ProxyMethod("bindIsolatedService")
    public static class BindIsolatedService extends BindService {
        @Override
        protected Object beforeHook(Object who, Method method, Object[] args) throws Throwable {
            // instanceName
            args[6] = null;
            MethodParameterUtils.replaceLastUserId(args);
            return super.beforeHook(who, method, args);
        }

        @Override
        protected boolean isIsolated() {
            return true;
        }
    }



    @ProxyMethod("unbindService")
    public static class UnbindService extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            IServiceConnection iServiceConnection = (IServiceConnection) args[0];
            if (iServiceConnection == null) {
                return method.invoke(who, args);
            }
            BlackBoxCore.getBActivityManager().unbindService(iServiceConnection.asBinder(), BActivityThread.getUserId());
            ServiceConnectionDelegate delegate = ServiceConnectionDelegate.getDelegate(iServiceConnection.asBinder());
            if (delegate != null) {
                args[0] = delegate;
            }
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("getRunningAppProcesses")
    public static class GetRunningAppProcesses extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            RunningAppProcessInfo runningAppProcesses = BActivityManager.get().getRunningAppProcesses(BActivityThread.getAppPackageName(), BActivityThread.getUserId());
            if (runningAppProcesses == null) {
                return new ArrayList<>();
            }
            return runningAppProcesses.mAppProcessInfoList;
        }
    }

    @ProxyMethod("getServices")
    public static class GetServices extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            RunningServiceInfo runningServices = BActivityManager.get().getRunningServices(BActivityThread.getAppPackageName(), BActivityThread.getUserId());
            if (runningServices == null) {
                return new ArrayList<>();
            }
            return runningServices.mRunningServiceInfoList;
        }
    }

    @ProxyMethod("getIntentSender")
    public static class GetIntentSender extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            int type = (int) args[0];
            Intent[] intents = (Intent[]) args[getIntentsIndex(args)];
            MethodParameterUtils.replaceFirstAppPkg(args);

            for (int i = 0; i < intents.length; i++) {
                Intent intent = intents[i];
                switch (type) {
                    case ActivityManagerCompat.INTENT_SENDER_ACTIVITY:
                        Intent shadow = new Intent();
                        shadow.setComponent(new ComponentName(BlackBoxCore.getHostPkg(), ProxyManifest.getProxyPendingActivity(BActivityThread.getAppPid())));
                        ProxyPendingRecord.saveStub(shadow, intent, BActivityThread.getUserId());
                        intents[i] = shadow;
                        break;
                }
            }
            IInterface invoke = (IInterface) method.invoke(who, args);
            if (invoke != null) {
                String[] packagesForUid = BPackageManager.get().getPackagesForUid(BActivityThread.getCallingBUid());
                if (packagesForUid.length < 1) {
                    packagesForUid = new String[]{BlackBoxCore.getHostPkg()};
                }
                BlackBoxCore.getBActivityManager().getIntentSender(invoke.asBinder(), packagesForUid[0], BActivityThread.getCallingBUid());
            }
            return invoke;
        }

        private int getIntentsIndex(Object[] args) {
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Intent[]) {
                    return i;
                }
            }
            if (BuildCompat.isR()) {
                return 6;
            } else {
                return 5;
            }
        }
    }

    @ProxyMethod("getPackageForIntentSender")
    public static class getPackageForIntentSender extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            IInterface invoke = (IInterface) args[0];
            return BlackBoxCore.getBActivityManager().getPackageForIntentSender(invoke.asBinder());
        }
    }

    @ProxyMethod("getUidForIntentSender")
    public static class getUidForIntentSender extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            IInterface invoke = (IInterface) args[0];
            return BlackBoxCore.getBActivityManager().getUidForIntentSender(invoke.asBinder());
        }
    }

    @ProxyMethod("getIntentSenderWithSourceToken")
    public static class GetIntentSenderWithSourceToken extends GetIntentSender {
    }

    @ProxyMethod("getIntentSenderWithFeature")
    public static class GetIntentSenderWithFeature extends GetIntentSender {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("broadcastIntentWithFeature")
    public static class BroadcastIntentWithFeature extends BroadcastIntent {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Intent intent = new Intent((Intent) args[2]);
            String type = (String) args[3];
            intent.setDataAndType(intent.getData(), type);
            Intent newIntent = BlackBoxCore.getBActivityManager().sendBroadcast(intent, type, BActivityThread.getUserId());
            if (newIntent != null) {
                args[1] = newIntent;
            } else {
                return 0;
            }
            if (args[7] instanceof String || args[7] instanceof String[]) {
                // clear the permission
                args[7] = null;
            }
            int index = ArrayUtils.indexOfFirst(args, Boolean.class);
            args[index] = false;
            MethodParameterUtils.replaceLastUserId(args);
            try {
                return method.invoke(who, args);
            } catch (Throwable e) {
                return 0; //ActivityManager.BROADCAST_SUCCESS
//                return -1; //ActivityManager.BROADCAST_STICKY_CANT_HAVE_PERMISSION
//                return -2; //ActivityManager.BROADCAST_FAILED_USER_STOPPED
            }
        }
    }

    @ProxyMethod("broadcastIntent")
    public static class BroadcastIntent extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            int intentIndex = getIntentIndex(args);
            Intent intent = (Intent) args[intentIndex];
            String resolvedType = (String) args[intentIndex + 1];
            Intent proxyIntent = BlackBoxCore.getBActivityManager().sendBroadcast(intent, resolvedType, BActivityThread.getUserId());
            if (proxyIntent != null) {
                Application application = BActivityThread.getApplication();
                if (application == null){
                    application = BActivityThread.getApplication();
                }
                proxyIntent.setExtrasClassLoader(application.getClassLoader());
                ProxyBroadcastRecord.saveStub(proxyIntent, intent, BActivityThread.getUserId());
                args[intentIndex] = proxyIntent;
            }
            if (args[7] instanceof String || args[7] instanceof String[]) {
                // clear the permission
                args[7] = null;
            }
            int index = ArrayUtils.indexOfFirst(args, Boolean.class);
            args[index] = false;
//            // ignore permission
//            for (int i = 0; i < args.length; i++) {
//                Object o = args[i];
//                if (o instanceof String[]) {
//                    args[i] = null;
//                }
//            }
//            return method.invoke(who, args);
            MethodParameterUtils.replaceLastUserId(args);
            try {
                return method.invoke(who, args);
            } catch (Throwable e) {
                return 0; //ActivityManager.BROADCAST_SUCCESS
//                return -1; //ActivityManager.BROADCAST_STICKY_CANT_HAVE_PERMISSION
//                return -2; //ActivityManager.BROADCAST_FAILED_USER_STOPPED
            }
        }

        int getIntentIndex(Object[] args) {
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (arg instanceof Intent) {
                    return i;
                }
            }
            return 1;
        }
    }

    @ProxyMethod("unregisterReceiver")
    public static class unregisterReceiver extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("finishReceiver")
    public static class finishReceiver extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("publishService")
    public static class PublishService extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("peekService")
    public static class PeekService extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastAppPkg(args);
            Intent intent = (Intent) args[0];
            String resolvedType = (String) args[1];
            ComponentName component = intent.getComponent();
            if (component != null && component.getPackageName().equals(BlackBoxCore.getHostPkg())){
                return method.invoke(who, args);
            }
            int userId = intent.getIntExtra("_B_|_UserId", -1);
            userId = userId == -1 ? BActivityThread.getUserId() : userId;
            if (userId == -1) {
                throw new IllegalArgumentException();
            }
            intent.setDataAndType(intent.getData(), resolvedType);
            ResolveInfo resolveInfo = BlackBoxCore.getBPackageManager().resolveService(intent, 0, resolvedType, userId);
            if (resolveInfo != null) {
                AppConfig appConfig = BlackBoxCore.get().getAppConfig();
                args[0] = ServiceConnectionDelegate.createProxy(null, intent);
                return method.invoke(who, args);
            }
            if (component != null && AppSystemEnv.isOpenPackage(component.getPackageName())) {
                return method.invoke(who, args);
            }
            IBinder peek = BlackBoxCore.getBActivityManager().peekService(intent, resolvedType, BActivityThread.getUserId());
            return peek;
        }
    }

    // todo
    @ProxyMethod("sendIntentSender")
    public static class SendIntentSender extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return 0;
        }
    }

    // android 10
    @ProxyMethod("registerReceiverWithFeature")
    public static class RegisterReceiverWithFeature extends RegisterReceiver {

    }

    @ProxyMethod("registerReceiver")
    public static class RegisterReceiver extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            int receiverIndex = getReceiverIndex();
            if (args[receiverIndex] != null) {
                IIntentReceiver intentReceiver = (IIntentReceiver) args[receiverIndex];
                IIntentReceiver proxy = InnerReceiverDelegate.createProxy(intentReceiver);

                WeakReference<?> weakReference = BRLoadedApkReceiverDispatcherInnerReceiver.get(intentReceiver).mDispatcher();
                if (weakReference != null) {
                    BRLoadedApkReceiverDispatcher.get(weakReference.get())._set_mIIntentReceiver(proxy);
                }

                args[receiverIndex] = proxy;
            }
            // ignore permission
            if (args[getPermissionIndex()] != null) {
                args[getPermissionIndex()] = null;
            }
            return method.invoke(who, args);
        }

        public int getReceiverIndex() {
            if (BuildCompat.isS()) {
                return 4;
            } else if (BuildCompat.isR()) {
                return 3;
            }
            return 2;
        }

        public int getPermissionIndex() {
            if (BuildCompat.isS()) {
                return 6;
            } else if (BuildCompat.isR()) {
                return 5;
            }
            return 4;
        }
    }

    //这里需要修复
    @ProxyMethod("grantUriPermission")
    public static class GrantUriPermission extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastUid(args);
            MethodParameterUtils.replaceLastUserId(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("setServiceForeground")
    public static class setServiceForeground extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
//            if (args[0] instanceof ComponentName) {
//                args[0] = new ComponentName(BlackBoxCore.getHostPkg(), ProxyManifest.getProxyService(BActivityThread.getAppPid()));
//            }
//            return method.invoke(who, args);
            Notification notification = (Notification) args[3];
            Intent intent = new Intent(BlackBoxCore.getContext(), DaemonService.class);
            if (notification != null) {
                if (BuildCompat.isOreo()) {
                    BlackBoxCore.getContext().startForegroundService(intent);
                } else {
                    BlackBoxCore.getContext().startService(intent);
                }
            } else {
                BlackBoxCore.getContext().stopService(intent);
            }
            return method.invoke(who, args);
            //return 0;
        }
    }

    @ProxyMethod("getHistoricalProcessExitReasons")
    public static class getHistoricalProcessExitReasons extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return ParceledListSliceCompat.create(new ArrayList<>());
        }
    }

    @ProxyMethod("getCurrentUser")
    public static class getCurrentUser extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Object blackBox = BRUserInfo.get()._new(BActivityThread.getUserId(), "BlackBox", BRUserInfo.get().FLAG_PRIMARY());
            return blackBox;
        }
    }

    @ProxyMethod("checkPermission")
    public static class checkPermission extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            String permission = (String) args[0];
            int pid = (int) args[1];
            int uid = (int) args[2];
            return BActivityManager.get().checkPermission(permission, pid, uid,BActivityThread.getAppPackageName());
        }
    }

    @ProxyMethod("checkUriPermission")
    public static class checkUriPermission extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return PackageManager.PERMISSION_GRANTED;
        }
    }

    // for < Android 10
    @ProxyMethod("setTaskDescription")
    public static class SetTaskDescription extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            ActivityManager.TaskDescription td = (ActivityManager.TaskDescription) args[1];
            args[1] = TaskDescriptionCompat.fix(td);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("setRequestedOrientation")
    public static class setRequestedOrientation extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            try {
                return method.invoke(who, args);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return 0;
        }
    }

    @ProxyMethod("registerUidObserver")
    public static class registerUidObserver extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return 0;
        }
    }

    @ProxyMethod("unregisterUidObserver")
    public static class unregisterUidObserver extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return 0;
        }
    }

    @ProxyMethod("updateConfiguration")
    public static class updateConfiguration extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return 0;
        }
    }

    @ProxyMethod("checkPermissionWithToken")
    public static class checkPermissionWithToken extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            String permission = (String) args[0];
            int pid = (int) args[1];
            int uid = (int) args[2];
            return BActivityManager.get().checkPermission(permission, pid, uid,BActivityThread.getAppPackageName());
        }
    }
}
