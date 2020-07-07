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
package com.installrefererkotlindemo.huawei.installreferrer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.text.TextUtils
import android.util.Log
import com.huawei.hms.ads.installreferrer.aidl.IPPSChannelInfoService
import com.installrefererkotlindemo.huawei.Constants.Companion.SERVICE_ACTION
import com.installrefererkotlindemo.huawei.Constants.Companion.SERVICE_PACKAGE_NAME
import org.json.JSONObject


class InstallReferrerAidlUtil(private var mContext: Context?) {

    companion object {
        private const val TAG = "InstallReferrerAidlUtil"
    }

    private var mServiceConnection: ServiceConnection? = null
    private var mCallback: InstallReferrerCallback? = null
    private var mService: IPPSChannelInfoService? = null

    fun getInstallReferrer(callback: InstallReferrerCallback) {
        mCallback = callback
        bindService()
    }

    private fun bindService(): Boolean {
        Log.i(TAG, "bindService")
        return mContext?.let {
            mServiceConnection = InstallReferrerServiceConnection()
            val intent = Intent(SERVICE_ACTION)
            intent.setPackage(SERVICE_PACKAGE_NAME)
            it.bindService(intent, mServiceConnection!!, Context.BIND_AUTO_CREATE)
        } ?: kotlin.run {
            Log.e(TAG, "context is null")
            false
        }
    }

    private fun unbindService() {
        Log.i(TAG, "unbindService")

        mServiceConnection?.let {
            // Unbind HUAWEI Ads kit
            mContext?.unbindService(it)
            mContext = null
            mCallback = null
            mService = null
        }
    }

    private inner class InstallReferrerServiceConnection : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, mIBinder: IBinder?) {
            Log.i(TAG, "onServiceConnected")
            mService = IPPSChannelInfoService.Stub.asInterface(mIBinder)
            mService?.let {
                try {
                    val channelJson = it.channelInfo
                    Log.i(TAG, "channelJson: $channelJson")
                    val jsonObject = JSONObject(channelJson)
                    jsonObject.apply {
                        val installReferrer = optString("channelInfo")
                        val clickTimestamp = optLong("clickTimestamp", 0)
                        val installTimestamp = optLong("installTimestamp", 0)
                        if (!TextUtils.isEmpty(installReferrer)) {
                            mCallback?.onSuccess(installReferrer, clickTimestamp, installTimestamp)
                        } else {
                            mCallback?.onFail("install referrer is empty")
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "getChannelInfo Exception")
                    mCallback?.onFail(e.message)
                } finally {
                    unbindService()
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.i(TAG, "onServiceDisconnected")
            mService = null
        }
    }
}