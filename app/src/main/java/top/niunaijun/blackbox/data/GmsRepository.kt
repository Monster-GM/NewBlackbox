package top.niunaijun.blackbox.data

import androidx.lifecycle.MutableLiveData
import top.niunaijun.bcore.BlackBoxCore
import top.niunaijun.blackbox.R
import top.niunaijun.blackbox.app.AppManager
import top.niunaijun.blackbox.bean.GmsBean
import top.niunaijun.blackbox.bean.GmsInstallBean
import top.niunaijun.blackbox.util.ResUtil.getString

class GmsRepository {
    fun getGmsInstalledList(mInstalledLiveData: MutableLiveData<List<GmsBean>>) {
        val userList = arrayListOf<GmsBean>()

        BlackBoxCore.get().users.forEach {
            val userId = it.id
            val userName = AppManager.mRemarkSharedPreferences.getString("Remark$userId", "User $userId") ?: ""
            val isInstalled = BlackBoxCore.get().isInstallGms(userId)
            val bean = GmsBean(userId, userName, isInstalled)
            userList.add(bean)
        }
        mInstalledLiveData.postValue(userList)
    }

    fun installGms(userID: Int, mUpdateInstalledLiveData: MutableLiveData<GmsInstallBean>) {
        val installResult = BlackBoxCore.get().installGms(userID)
        val result = if (installResult.success) {
            getString(R.string.install_success)
        } else {
            getString(R.string.install_fail, installResult.msg)
        }

        val bean = GmsInstallBean(userID, installResult.success, result)
        mUpdateInstalledLiveData.postValue(bean)
    }

    fun uninstallGms(userID: Int, mUpdateInstalledLiveData: MutableLiveData<GmsInstallBean>) {
        var isSuccess = false
        if (BlackBoxCore.get().isInstallGms(userID)) {
            isSuccess = BlackBoxCore.get().uninstallGms(userID)
        }

        val result = if (isSuccess) {
            getString(R.string.uninstall_success)
        } else {
            getString(R.string.uninstall_fail)
        }

        val bean = GmsInstallBean(userID, isSuccess, result)
        mUpdateInstalledLiveData.postValue(bean)
    }
}
