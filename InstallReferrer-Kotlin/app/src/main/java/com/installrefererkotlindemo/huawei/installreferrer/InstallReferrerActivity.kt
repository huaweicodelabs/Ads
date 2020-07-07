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

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import com.installrefererkotlindemo.huawei.BaseActivity
import com.installrefererkotlindemo.huawei.CallMode
import com.installrefererkotlindemo.huawei.R
import kotlinx.android.synthetic.main.activity_install_referrer.*

class InstallReferrerActivity : BaseActivity(), InstallReferrerCallback {

    companion object {
        private const val TAG = "InstallReferrerActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_install_referrer)
        init()
    }

    override fun init() {
        super.init()
        connectThread?.start()
    }

    /**
     * Get install referrer from a non-UI thread.
     */
    private var connectThread: Thread? = object : Thread() {
        override fun run() {
            getInstallReferrer()
        }
    }

    /**
     * Update install referrer from a UI thread.
     */

    private fun updateReferrerDetails(
        mInstallReferrer: String?,
        mClickTimestamp: Long,
        mInstallTimestamp: Long
    ) {
        if (TextUtils.isEmpty(mInstallReferrer)) {
            Log.w(TAG, "installReferrer is empty")
            return
        }
        Log.i(
            TAG,
            "installReferrer: $mInstallReferrer , clickTimestamp:  $mClickTimestamp , installTimestamp: $mInstallTimestamp"
        )
        runOnUiThread {
            install_referrer_tv.text = mInstallReferrer
            click_time_tv.text = mClickTimestamp.toString()
            install_time_tv.text = mInstallTimestamp.toString()
        }
    }

    private fun getInstallReferrer() {
        val mode = getIntExtra("mode", CallMode.SDK)
        Log.i(TAG, "getInstallReferrer mode=$mode")
        when(mode){
            CallMode.SDK->{
                // Get install referrer by sdk mode.
                val sdkUtil = InstallReferrerSdkUtil(this)
                sdkUtil.getInstallReferrer(this)
            }
            CallMode.AIDL->{
                // Get install referrer by aidl mode.
                val aidlUtil = InstallReferrerAidlUtil(this)
                aidlUtil.getInstallReferrer(this)
            }
        }
    }

    override fun onSuccess(
        mInstallReferrer: String?,
        mClickTimestamp: Long,
        mInstallTimestamp: Long
    ) {
        Log.i(TAG, "onSuccuss")
        updateReferrerDetails(mInstallReferrer, mClickTimestamp, mInstallTimestamp)
    }

    override fun onFail(mErrMsg: String?) {
        Log.e(TAG, "onFail:$mErrMsg")
    }
}
