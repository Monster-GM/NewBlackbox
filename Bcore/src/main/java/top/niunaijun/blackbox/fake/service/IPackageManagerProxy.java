package top.niunaijun.blackbox.fake.service;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.VersionedPackage;
import android.text.TextUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import black.android.app.BRActivityThread;
import black.android.app.BRContextImpl;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.core.env.AppSystemEnv;
import top.niunaijun.blackbox.fake.frameworks.BPackageManager;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;
import top.niunaijun.blackbox.fake.service.base.PkgMethodProxy;
import top.niunaijun.blackbox.fake.service.base.ValueMethodProxy;
import top.niunaijun.blackbox.utils.MethodParameterUtils;
import top.niunaijun.blackbox.utils.Reflector;
import top.niunaijun.blackbox.utils.Slog;
import top.niunaijun.blackbox.utils.compat.BuildCompat;
import top.niunaijun.blackbox.utils.compat.ParceledListSliceCompat;

/**
 * Created by Milk on 3/30/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class IPackageManagerProxy extends BinderInvocationStub {
    public static final String TAG = "PackageManagerStub";

    public IPackageManagerProxy() {
        super(BRActivityThread.get().sPackageManager().asBinder());
    }

    @Override
    protected Object getWho() {
        return BRActivityThread.get().sPackageManager();
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        BRActivityThread.get()._set_sPackageManager(proxyInvocation);
        replaceSystemService("package");
        Object systemContext = BRActivityThread.get(BlackBoxCore.mainThread()).getSystemContext();
        PackageManager packageManager = BRContextImpl.get(systemContext).mPackageManager();
        if (packageManager != null) {
            try {
                Reflector.on("android.app.ApplicationPackageManager")
                        .field("mPM")
                        .set(packageManager, proxyInvocation);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @Override
    protected void onBindMethod() {
        super.onBindMethod();
        addMethodHook(new ValueMethodProxy("addOnPermissionsChangeListener", 0));
        addMethodHook(new ValueMethodProxy("removeOnPermissionsChangeListener", 0));
    }

    @ProxyMethod("shouldShowRequestPermissionRationale")
    public static class shouldShowRequestPermissionRationale extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("resolveIntent")
    public static class ResolveIntent extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Intent intent = (Intent) args[0];
            String resolvedType = (String) args[1];
            ResolveInfo resolveInfo;
            if (BuildCompat.isT()) {
                long flags = (long) args[2];
                resolveInfo = BlackBoxCore.getBPackageManager().resolveIntent(intent, resolvedType, Math.toIntExact(flags), BActivityThread.getUserId());
            } else {
                int flags = (int) args[2];
                resolveInfo = BlackBoxCore.getBPackageManager().resolveIntent(intent, resolvedType, flags, BActivityThread.getUserId());
            }            if (resolveInfo != null) {
                return resolveInfo;
            }
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("resolveService")
    public static class ResolveService extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Intent intent = (Intent) args[0];
            String resolvedType = (String) args[1];
            ResolveInfo resolveInfo;
            if (BuildCompat.isT()) {
                long flags = (long) args[2];
                resolveInfo = BlackBoxCore.getBPackageManager().resolveService(intent, Math.toIntExact(flags), resolvedType, BActivityThread.getUserId());
            } else {
                int flags = (int) args[2];
                resolveInfo = BlackBoxCore.getBPackageManager().resolveService(intent, flags, resolvedType, BActivityThread.getUserId());
            }
            if (resolveInfo != null) {
                return resolveInfo;
            }
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("setComponentEnabledSetting")
    public static class SetComponentEnabledSetting extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return 0;
        }
    }

    @ProxyMethod("getPackageInfo")
    public static class GetPackageInfo extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            String packageName = (String) args[0];
            PackageInfo packageInfo;

            if (BuildCompat.isT()) {
                long flags = (long) args[1];
                packageInfo = BlackBoxCore.getBPackageManager().getPackageInfo(packageName, Math.toIntExact(flags), BActivityThread.getUserId());
            } else {
                int flags = (int) args[1];
                packageInfo = BlackBoxCore.getBPackageManager().getPackageInfo(packageName, flags, BActivityThread.getUserId());
            }
            if (packageInfo != null) {
                return packageInfo;
            }
            if (AppSystemEnv.isOpenPackage(packageName)) {
                return method.invoke(who, args);
            }
            return null;
        }
    }

    @ProxyMethod("getPackageUid")
    public static class GetPackageUid extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("setApplicationBlockedSettingAsUser")
    public static class setApplicationBlockedSettingAsUser extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            MethodParameterUtils.replaceLastUserId(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("getProviderInfo")
    public static class GetProviderInfo extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            ComponentName componentName = (ComponentName) args[0];
            ProviderInfo providerInfo;
            if (BuildCompat.isT()) {
                long flags = (long) args[1];
                providerInfo = BlackBoxCore.getBPackageManager().getProviderInfo(componentName, Math.toIntExact(flags), BActivityThread.getUserId());
            } else {
                int flags = (int) args[1];
                providerInfo = BlackBoxCore.getBPackageManager().getProviderInfo(componentName, flags, BActivityThread.getUserId());
            }
            if (providerInfo != null)
                return providerInfo;
            if (AppSystemEnv.isOpenPackage(componentName)) {
                return method.invoke(who, args);
            }
            return null;
        }
    }

    @ProxyMethod("getReceiverInfo")
    public static class GetReceiverInfo extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            ComponentName componentName = (ComponentName) args[0];
            ActivityInfo receiverInfo;

            if (BuildCompat.isT()) {
                long flags = (long) args[1];
                receiverInfo = BlackBoxCore.getBPackageManager().getReceiverInfo(componentName, Math.toIntExact(flags), BActivityThread.getUserId());
            } else {
                int flags = (int) args[1];
                receiverInfo = BlackBoxCore.getBPackageManager().getReceiverInfo(componentName, flags, BActivityThread.getUserId());
            }
            if (receiverInfo != null)
                return receiverInfo;
            if (AppSystemEnv.isOpenPackage(componentName)) {
                return method.invoke(who, args);
            }
            return null;
        }
    }

    @ProxyMethod("getActivityInfo")
    public static class GetActivityInfo extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            ComponentName componentName = (ComponentName) args[0];
            ActivityInfo activityInfo;
            if (BuildCompat.isT()) {
                long flags = (long) args[1];
                activityInfo = BlackBoxCore.getBPackageManager().getActivityInfo(componentName, Math.toIntExact(flags), BActivityThread.getUserId());
            } else {
                int flags = (int) args[1];
                activityInfo = BlackBoxCore.getBPackageManager().getActivityInfo(componentName, Math.toIntExact(flags), BActivityThread.getUserId());
            }
            if (activityInfo != null)
                return activityInfo;
            if (AppSystemEnv.isOpenPackage(componentName)) {
                MethodParameterUtils.replaceLastUserId(args);
                return method.invoke(who, args);
            }
            return null;
        }
    }

    @ProxyMethod("getPackageUidEtc")
    public static class getPackageUidEtc extends GetPackageUid {

    }

    @ProxyMethod("getServiceInfo")
    public static class GetServiceInfo extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            ComponentName componentName = (ComponentName) args[0];
            ServiceInfo serviceInfo;
            if (BuildCompat.isT()) {
                long flags = (long) args[1];
                serviceInfo = BlackBoxCore.getBPackageManager().getServiceInfo(componentName, Math.toIntExact(flags), BActivityThread.getUserId());
            } else {
                int flags = (int) args[1];
                serviceInfo = BlackBoxCore.getBPackageManager().getServiceInfo(componentName, flags, BActivityThread.getUserId());
            }
            MethodParameterUtils.replaceLastUserId(args);
            if (serviceInfo != null)
                return serviceInfo;
            if (AppSystemEnv.isOpenPackage(componentName)) {
                return method.invoke(who, args);
            }
            return null;
        }
    }

    @ProxyMethod("getInstalledApplications")
    public static class GetInstalledApplications extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            List<ApplicationInfo> installedApplications;
            if (BuildCompat.isT()) {
                long flags = (long) args[0];
                installedApplications = BlackBoxCore.getBPackageManager().getInstalledApplications(Math.toIntExact(flags), BActivityThread.getUserId());
            } else {
                int flags = (int) args[0];
                installedApplications = BlackBoxCore.getBPackageManager().getInstalledApplications(flags, BActivityThread.getUserId());
            }
            return ParceledListSliceCompat.create(installedApplications);
        }
    }

    @ProxyMethod("getPackageGids")
    public static class getPackageGids extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            MethodParameterUtils.replaceLastUserId(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("getInstalledPackages")
    public static class GetInstalledPackages extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            List<PackageInfo> installedPackages;

            if (BuildCompat.isT()) {
                long flags = (long) args[0];
                installedPackages = BlackBoxCore.getBPackageManager().getInstalledPackages(Math.toIntExact(flags), BActivityThread.getUserId());
            } else {
                int flags = (int) args[0];
                installedPackages = BlackBoxCore.getBPackageManager().getInstalledPackages(flags, BActivityThread.getUserId());
            }
            return ParceledListSliceCompat.create(installedPackages);
        }
    }

    @ProxyMethod("getApplicationInfo")
    public static class GetApplicationInfo extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            String packageName = (String) args[0];
            ApplicationInfo applicationInfo;
//            if (ClientSystemEnv.isFakePackage(packageName)) {
//                packageName = BlackBoxCore.getHostPkg();
//            }
            if (BuildCompat.isT()) {
                long flags = (long) args[1];
                applicationInfo = BlackBoxCore.getBPackageManager().getApplicationInfo(packageName, Math.toIntExact(flags), BActivityThread.getUserId());
            } else {
                int flags = (int) args[1];
                applicationInfo = BlackBoxCore.getBPackageManager().getApplicationInfo(packageName, flags, BActivityThread.getUserId());
            }
            MethodParameterUtils.replaceLastUserId(args);
            if (applicationInfo != null) {
                return applicationInfo;
            }
            if (AppSystemEnv.isOpenPackage(packageName)) {
                return method.invoke(who, args);
            }
            return null;
        }
    }

    @ProxyMethod("queryContentProviders")
    public static class QueryContentProviders extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            List<ProviderInfo> providers;

            if (BuildCompat.isT()) {
                long flags = (long) args[2];

                providers = BlackBoxCore.getBPackageManager()
                        .queryContentProviders(BActivityThread.getAppProcessName(), BlackBoxCore.getHostUid(), Math.toIntExact(flags), BActivityThread.getUserId());
            } else {
                int flags = (int) args[2];

                providers = BlackBoxCore.getBPackageManager()
                        .queryContentProviders(BActivityThread.getAppProcessName(), BlackBoxCore.getHostUid(), flags, BActivityThread.getUserId());
            }
            return ParceledListSliceCompat.create(providers);
        }
    }

    @ProxyMethod("queryIntentReceivers")
    public static class QueryBroadcastReceivers extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Intent intent = MethodParameterUtils.getFirstParam(args, Intent.class);
            String type = MethodParameterUtils.getFirstParam(args, String.class);
            List<ResolveInfo> resolves;
            if (BuildCompat.isT()) {
                Long flags = MethodParameterUtils.getFirstParam(args, Long.class);
                resolves = BlackBoxCore.getBPackageManager().queryBroadcastReceivers(intent, Math.toIntExact(flags), type, BActivityThread.getUserId());
            } else {
                Integer flags = MethodParameterUtils.getFirstParam(args, Integer.class);
                resolves = BlackBoxCore.getBPackageManager().queryBroadcastReceivers(intent, flags, type, BActivityThread.getUserId());
            }            Slog.d(TAG, "queryIntentReceivers: " + resolves);

            // http://androidxref.com/7.0.0_r1/xref/frameworks/base/core/java/android/app/ApplicationPackageManager.java#872
            if (BuildCompat.isN()) {
                return ParceledListSliceCompat.create(resolves);
            }

            // http://androidxref.com/6.0.1_r10/xref/frameworks/base/core/java/android/app/ApplicationPackageManager.java#699
            return resolves;
        }
    }

    @ProxyMethod("resolveContentProvider")
    public static class ResolveContentProvider extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            String authority = (String) args[0];
            ProviderInfo providerInfo;
            if (BuildCompat.isT()) {
                long flags = (long) args[1];
                providerInfo = BlackBoxCore.getBPackageManager().resolveContentProvider(authority, Math.toIntExact(flags), BActivityThread.getUserId());
            } else {
                int flags = (int) args[1];
                providerInfo = BlackBoxCore.getBPackageManager().resolveContentProvider(authority, flags, BActivityThread.getUserId());
            }
            if (providerInfo == null) {
                return method.invoke(who, args);
            }
            return providerInfo;
        }
    }

    @ProxyMethod("canRequestPackageInstalls")
    public static class CanRequestPackageInstalls extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("getPackagesForUid")
    public static class GetPackagesForUid extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            int uid = (Integer) args[0];
            if (uid == BlackBoxCore.getHostUid()) {
                args[0] = BlackBoxCore.getHostUid();
                uid = (int) args[0];
            }
            String[] packagesForUid = BlackBoxCore.getBPackageManager().getPackagesForUid(uid);
            Slog.d(TAG, args[0] + " , " + BActivityThread.getAppProcessName() + " GetPackagesForUid: " + Arrays.toString(packagesForUid));
            return packagesForUid;
        }
    }

//    @ProxyMethod("checkSignatures")
//    public static class checkSignatures extends MethodHook {
//        @Override
//        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
//            if (args.length == 2 && args[0] instanceof String && args[1] instanceof String) {
//                String pkgNameOne = (String) args[0], pkgNameTwo = (String) args[1];
//                if (TextUtils.equals(pkgNameOne, pkgNameTwo)) {
//                    return PackageManager.SIGNATURE_MATCH;
//                }
//                return BPackageManager.get().checkSignatures(pkgNameOne, pkgNameTwo);
//            }
//            return method.invoke(who, args);
//        }
//    }

    @ProxyMethod("getInstallerPackageName")
    public static class GetInstallerPackageName extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            // fake google play
            return "com.android.vending";
        }
    }

    @ProxyMethod("getPermissionInfo")
    public static class getPermissionInfo extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            String name = (String) args[0];
            int flags = (int) args[args.length - 1];
            PermissionInfo info = BPackageManager.get().getPermissionInfo(name, flags);
            if (info != null) {
                return info;
            }
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("getPermissions")
    public static class getPermissions extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastUserId(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("getSharedLibraries")
    public static class GetSharedLibraries extends MethodHook {
        private static final int MATCH_ANY_USER = 0x00400000;
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            int flags =  (int) getIntOrLongValue(args[1]);

            if ((flags & MATCH_ANY_USER) != 0) {
                flags &= ~MATCH_ANY_USER;
                args[1] = flags;
            }
            args[0] = BlackBoxCore.getHostPkg();

            return method.invoke(who, args);
            // todo
            //return ParceledListSliceCompat.create(new ArrayList<>());
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

    @ProxyMethod("getComponentEnabledSetting")
    public static class getComponentEnabledSetting extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return PackageManager.COMPONENT_ENABLED_STATE_DEFAULT;
        }
    }

    @ProxyMethod("checkPermission")
    public static class CheckPermission extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            String permName = (String) args[0];
            String pkgName = (String) args[1];
            Slog.e(TAG,"checkPermission pkg = "+pkgName);
            return BPackageManager.get().checkPermission(permName,pkgName,BActivityThread.getUserId());
        }
    }

    @ProxyMethod("canForwardTo")
    public static class canForwardTo extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            int sourceUserId = (int) args[2];
            int targetUserId = (int) args[3];
            return sourceUserId == targetUserId;
        }
    }

    @ProxyMethod("getPreferredActivities")
    public static class getPreferredActivities extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastAppPkg(args);
            return method.invoke(who, args);
        }
    }

//    @ProxyMethod("getPreferredActivities")
//    public static class getPreferredActivities extends MethodHook {
//        @Override
//        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
//            MethodParameterUtils.replaceLastAppPkg(args);
//            return method.invoke(who, args);
//        }
//    }

    @ProxyMethod("getPackageInfoVersioned")
    public static class getPackageInfoVersioned extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            VersionedPackage versionedPackage = (VersionedPackage) args[0];
            String packageName = versionedPackage.getPackageName();
            PackageInfo packageInfo;
            if (BuildCompat.isT()) {
                long flags = (long) args[1];
                packageInfo = BlackBoxCore.getBPackageManager().getPackageInfo(packageName, Math.toIntExact(flags), BActivityThread.getUserId());
            } else {
                int flags = (int) args[1];
                packageInfo = BlackBoxCore.getBPackageManager().getPackageInfo(packageName, flags, BActivityThread.getUserId());
            }

            if (packageInfo != null) {
                return packageInfo;
            }

            if (AppSystemEnv.isOpenPackage(packageName)) {
                return method.invoke(who, args);
            }
            return null;
        }
    }

    @ProxyMethod("queryIntentActivities")
    public static class QueryIntentActivities extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Intent intent = (Intent) args[0];
            String resolvedType = (String) args[1];
            List<ResolveInfo> intentActivities;
            if (BuildCompat.isT()) {
                long flags = (long) args[2];
                intentActivities = BlackBoxCore.getBPackageManager().queryIntentActivities(intent, Math.toIntExact(flags), resolvedType, BActivityThread.getUserId());
            } else {
                int flags = (int) args[2];
                intentActivities = BlackBoxCore.getBPackageManager().queryIntentActivities(intent, flags, resolvedType, BActivityThread.getUserId());
            }
            return ParceledListSliceCompat.create(intentActivities);
        }
    }

    @ProxyMethod("checkPackageStartable")
    public static class checkPackageStartable extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            String pkg = (String) args[0];
            if (BlackBoxCore.get().isInstalled(pkg,BActivityThread.getUserId())) {
                return 0;
            }
            MethodParameterUtils.replaceLastUserId(args);
            return method.invoke(who, args);
        }
    }
}
