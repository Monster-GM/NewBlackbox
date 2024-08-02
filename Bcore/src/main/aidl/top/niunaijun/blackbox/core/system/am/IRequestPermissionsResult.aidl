// IRequestPermissionsResult.aidl
package top.niunaijun.blackbox.core.system.am;

interface IRequestPermissionsResult {
    boolean onResult(int requestCode,in String[] permissions,in int[] grantResults);
}