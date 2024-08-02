/**
 * @Description:
 * @Author: xxxx
 * @CreateDate: 2024/8/2 18:08
 */
package top.niunaijun.blackbox.utils;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

// 20240801 add request permission add start 0
public class BundleUtils {
    public static void putBinder(Intent intent, String key, IBinder value) {
        Bundle bundle = new Bundle();
        putBinder(bundle, "binder", value);
        intent.putExtra(key, bundle);
    }

    public static void putBinder(Bundle bundle, String key, IBinder value) {
        bundle.putBinder(key, value);
    }

    public static IBinder getBinder(Bundle bundle, String key) {
        return bundle.getBinder(key);
    }

    public static IBinder getBinder(Intent intent, String key) {
        Bundle bundle = intent.getBundleExtra(key);
        if (bundle != null) {
            return getBinder(bundle, "binder");
        }
        return null;
    }
}
// 20240801 add request permission add end 0