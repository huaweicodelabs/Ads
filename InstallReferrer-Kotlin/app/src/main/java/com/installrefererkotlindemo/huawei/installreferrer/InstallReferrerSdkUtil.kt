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

import android.content.Context
import android.os.RemoteException
import android.util.Log
import com.huawei.hms.ads.installreferrer.api.InstallReferrerClient
import com.huawei.hms.ads.installreferrer.api.InstallReferrerClient.InstallReferrerResponse.*
import com.huawei.hms.ads.installreferrer.api.InstallReferrerStateListener
import com.huawei.hms.ads.installreferrer.api.ReferrerDetails
import java.io.IOException

class InstallReferrerSdkUtil(private var mContext: Context?) {

    companion object {
        private const val TAG = "InstallReferrerSdkUtil"
    }

    private var mReferrerClient: InstallReferrerClient? = null
    private var mCallback: InstallReferrerCallback? = null

    private val mInstallReferrerStateListener: InstallReferrerStateListener =
        object : InstallReferrerStateListener {
            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                when (responseCode) {
                    OK -> {
                        Log.i(TAG, "connect ads kit ok")
                        get()
                    }
                    FEATURE_NOT_SUPPORTED -> Log.i(
                        TAG,
                        "FEATURE_NOT_SUPPORTED"
                    )
                    SERVICE_UNAVAILABLE -> Log.i(
                        TAG,
                        "SERVICE_UNAVAILABLE"
                    )
                    else -> Log.i(TAG, "responseCode: $responseCode")
                }
            }

            override fun onInstallReferrerServiceDisconnected() {
                Log.i(
                    TAG,
                    "onInstallReferrerServiceDisconnected"
                )
            }
        }

    private fun get() {

        mReferrerClient?.let {
            try {
                val mReferrerDetails: ReferrerDetails = it.installReferrer
                mReferrerDetails.apply {
                    mCallback?.apply {
                        onSuccess(
                            installReferrer,
                            referrerClickTimestampMillisecond,
                            installBeginTimestampMillisecond
                        )
                    }
                }
            } catch (e: RemoteException) {
                Log.i(
                    TAG,
                    "getInstallReferrer RemoteException: ${e.message}"
                )
            } catch (e: IOException) {
                Log.i(
                    TAG,
                    "getInstallReferrer IOException: ${e.message}"
                )
            } finally {
                disconnect()
            }
        }
    }

    /**
     * disconnect from huawei ads service.
     */
    private fun disconnect() {
        Log.i(TAG, "disconnect")
        mReferrerClient?.let {
            it.endConnection()
            mReferrerClient = null
            mContext = null
        }
    }

    /**
     * connect huawei ads service.
     */
    private fun connect(): Boolean {
        Log.i(TAG, "connect...")

        mContext?.let {
            mReferrerClient = InstallReferrerClient.newBuilder(it).build()
            mReferrerClient?.startConnection(mInstallReferrerStateListener)
            return true
        }
        return false
    }

    fun getInstallReferrer(callback: InstallReferrerCallback?) {
        if (null == callback) {
            Log.e(TAG, "getInstallReferrer callback is null")
            return
        }
        mCallback = callback
        connect()
    }
}