package top.niunaijun.blackbox.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.core.system.am.IRequestPermissionsResult;
import top.niunaijun.blackbox.utils.BundleUtils;

// 20240801 add request permission add start 0
@TargetApi(Build.VERSION_CODES.M)
public class RequestPermissionsActivity extends Activity {
    private static final int REQUEST_PERMISSION_CODE = 996;

    public static void request(Context context, String[] permissions, IRequestPermissionsResult callback) {
        Intent intent = new Intent();
        intent.setClassName(BlackBoxCore.getContext(), RequestPermissionsActivity.class.getName());

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("permissions", permissions);
        BundleUtils.putBinder(intent, "callback", callback.asBinder());
        context.startActivity(intent);
    }

    private IRequestPermissionsResult mCallBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        final String[] permissions = intent.getStringArrayExtra("permissions");
        IBinder binder = BundleUtils.getBinder(intent, "callback");
        if (binder == null || permissions == null) {
            finish();
            return;
        }
        mCallBack = IRequestPermissionsResult.Stub.asInterface(binder);
        RequestPermissionsActivity.this.requestPermissions(permissions, REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, final String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mCallBack != null) {
            try {
                boolean success = mCallBack.onResult(requestCode, permissions, grantResults);
                if (!success) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RequestPermissionsActivity.this, "Request permission failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        finish();
    }
}
// 20240801 add request permission add end 0