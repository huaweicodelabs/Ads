/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.huawei.hms.ads.exsplash

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import com.huawei.hms.ads.ExSplashService

class ExSplashServiceManager(private var context: Context?) {
    private var serviceConnection: ServiceConnection? = null
    private var exSplashService: ExSplashService? = null
    private var enable = false

    /**
     * Enable user protocol
     */
    fun enableUserInfo(enable: Boolean) {
        this.enable = enable
        bindService()
    }

    private fun bindService(): Boolean {
        Log.i(TAG, "bindService")
        serviceConnection = ExSplashServiceConnection(context)
        val intent = Intent(ACTION_EXSPLASH)
        intent.setPackage(PACKAGE_NAME)
        val result = context!!.bindService(intent, serviceConnection!!, Context.BIND_AUTO_CREATE)
        Log.i(TAG, "bindService result: $result")
        return result
    }

    private fun unbindService() {
        Log.i(TAG, "unbindService")
        if (null == context) {
            Log.e(TAG, "context is null")
            return
        }
        if (null != serviceConnection) {
            context!!.unbindService(serviceConnection!!)
            exSplashService = null
            context = null
        }
    }

    inner class ExSplashServiceConnection(private val context: Context?) : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Log.i(Companion.INNER_TAG, "onServiceConnected")
            exSplashService = ExSplashService.Stub.asInterface(service)
            if (exSplashService != null) {
                try {
                    exSplashService!!.enableUserInfo(enable)
                    Log.i(Companion.INNER_TAG, "enableUserInfo done")
                } catch (e: RemoteException) {
                    Log.i(Companion.INNER_TAG, "enableUserInfo error")
                } finally {
                    context!!.unbindService(this)
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Log.i(Companion.INNER_TAG, "onServiceDisconnected")
        }
    }

    companion object {
        private val TAG = ExSplashServiceManager::class.java.simpleName

        /**
         * Action of ExSplash Service
         */
        private const val ACTION_EXSPLASH = "com.huawei.hms.ads.EXSPLASH_SERVICE"

        /**
         * Package name of ExSplash Service
         */
        private const val PACKAGE_NAME = "com.huawei.hwid"
        private const val INNER_TAG = "ExSplashConnection"
    }

}