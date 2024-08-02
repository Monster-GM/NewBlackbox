/**
 * @Description:
 * @Author: xxxx
 * @CreateDate: 2024/8/1 23:52
 */
package top.niunaijun.blackbox.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.app.RequestPermissionsActivity;
import top.niunaijun.blackbox.core.system.am.IRequestPermissionsResult;
import top.niunaijun.blackbox.utils.compat.BuildCompat;

// 20240801 add request permission add start 0
public class PermissionUtils {

    public static Set<String> DANGEROUS_PERMISSION = new HashSet<String>() {{
        // CALENDAR group
        add(Manifest.permission.READ_CALENDAR);
        add(Manifest.permission.WRITE_CALENDAR);

        // CAMERA
        add(Manifest.permission.CAMERA);

        // CONTACTS
        add(Manifest.permission.READ_CONTACTS);
        add(Manifest.permission.WRITE_CONTACTS);
        add(Manifest.permission.GET_ACCOUNTS);

        // LOCATION
        add(Manifest.permission.ACCESS_FINE_LOCATION);
        add(Manifest.permission.ACCESS_COARSE_LOCATION);

        // PHONE
        add(Manifest.permission.READ_PHONE_STATE);
        add(Manifest.permission.CALL_PHONE);
        if (Build.VERSION.SDK_INT >= 16) {
            add(Manifest.permission.READ_CALL_LOG);
            add(Manifest.permission.WRITE_CALL_LOG);
        }
        add(Manifest.permission.ADD_VOICEMAIL);
        add(Manifest.permission.USE_SIP);
        add(Manifest.permission.PROCESS_OUTGOING_CALLS);

        // SMS
        add(Manifest.permission.SEND_SMS);
        add(Manifest.permission.RECEIVE_SMS);
        add(Manifest.permission.READ_SMS);
        add(Manifest.permission.RECEIVE_WAP_PUSH);
        add(Manifest.permission.RECEIVE_MMS);

        add(Manifest.permission.RECORD_AUDIO);
        // STORAGE
        add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (Build.VERSION.SDK_INT >= 16) {
            add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (Build.VERSION.SDK_INT >= 20) {
            // SENSORS
            add(Manifest.permission.BODY_SENSORS);
        }
    }};

    public static boolean isCheckPermissionRequired(ApplicationInfo info) {
        if (BuildCompat.isM() || BlackBoxCore.getContext().getApplicationInfo().targetSdkVersion < Build.VERSION_CODES.M) {
            return false;
        }
        return info.targetSdkVersion < Build.VERSION_CODES.M;
    }

    public static String[] findDangerousPermissions(List<String> permissions) {
        if (permissions == null) {
            return null;
        }

        List<String> list = new ArrayList<>();
        for (String per : permissions) {
            if (DANGEROUS_PERMISSION.contains(per)) {
                list.add(per);
            }
        }
        return list.toArray(new String[0]);
    }

    // 判断是否有需要权限的要求
    public static boolean checkPermissions(String[] permissions) {
        if (permissions == null) {
            return true;
        }

        for (String permission : permissions) {
            if (!BlackBoxCore.get().checkSelfPermission(permission)) {
                return false;
            }
        }
        return true;
    }

    public interface CallBack {
        boolean onResult(int requestCode, String[] permissions, int[] grantResults);
    }

    public static void startRequestPermissions(Context context, String[] permissions, final CallBack callBack) {
        RequestPermissionsActivity.request(context, permissions, new IRequestPermissionsResult.Stub(){

            @Override
            public boolean onResult(int requestCode, String[] permissions, int[] grantResults) throws RemoteException {
                if (callBack != null) {
                    return callBack.onResult(requestCode, permissions, grantResults);
                }
                return false;
            }
        });
    }

    public static boolean isRequestGranted(int[] grantResults) {
        boolean allGranted = true;
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                allGranted = false;
                break;
            }
        }
        return allGranted;
    }

}
// 20240801 add request permission add end 0
