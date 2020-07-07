/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.

 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.installrefererkotlindemo.huawei

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Binder
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import com.huawei.android.hms.ppskit.IPPSChannelInfoService


class PpsChannelInfoService : Service() {

    private val mBinder: IPPSChannelInfoService.Stub = object : IPPSChannelInfoService.Stub(){

        @Throws(RemoteException::class)
        override fun getChannelInfo(): String? {
            val mPackageManager = packageManager
            val callerPkg =
                getCallerPkgSafe(mPackageManager, Binder.getCallingUid())
            Log.i(TAG, "callerPkg=$callerPkg")
            val sp = getSharedPreferences(
                Constants.INSTALL_REFERRER_FILE,
                Context.MODE_PRIVATE
            )
            val installReferrer = sp.getString(callerPkg, "")
            Log.i(TAG, "installReferrer=$installReferrer")
            return installReferrer
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.i(TAG, "onBind")
        return mBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.i(TAG, "onUnbind")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
    }

    companion object {
        private const val TAG = "PpsChannelInfoService"

        fun getCallerPkgSafe(mPackageManager: PackageManager?, uid: Int): String {
            if (null == mPackageManager) {
                return ""
            }
            var pkg: String? = ""
            try {
                pkg = mPackageManager.getNameForUid(uid)
            } catch (e: RuntimeException) {
                Log.w(TAG, "get name for uid error")
            } catch (e: Exception) {
                Log.w(TAG, "get name for uid error")
            } catch (e: Throwable) {
                Log.w(TAG, "get name for uid error")
            }
            return pkg!!
        }
    }
}